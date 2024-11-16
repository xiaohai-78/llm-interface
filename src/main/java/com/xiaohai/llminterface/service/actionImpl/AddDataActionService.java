package com.xiaohai.llminterface.service.actionImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaohai.llminterface.service.SystemActionAbstractService;
import com.xiaohai.llminterface.utils.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 新建表单
 * @Author: XiaoYunTao
 * @Date: 2024/10/14
 */
@Slf4j
@Service
public class AddDataActionService extends SystemActionAbstractService {

    /**
     * 获取表单名称prompt
     */
    private static final String GET_FORM_NAME_PROMPT_TEXT = """
            			%s
               
            你的任务是分析上面客户的输入，来生成输入中包含表单名称的Json参数。
            		
            如：帮我新建一个个人客户。
                     {
                         "formName": "个人客户"
                     }
               帮我新建联系人，名字叫张三。
                      {
                         "formName": "联系人"
                     }
               帮我新建云涛新建自定义表单，流水号:Api082301。
                      {
                         "formName": "云涛新建自定义表单"
                     }
            			
            帮我根据客户输入生成上述例子中的Json参数。
            			
            不需要多余解释，请你只返回Json信息。
            """;

    public AddDataActionService(ChatModel ollamaChatModel) {
        super(ollamaChatModel);
    }

    @Override
    public JSONObject execute(String message) {
        String addCustomerParamPrompt = getParamPrompt(message);
        JSONObject formListParams = generateParamsFromText(addCustomerParamPrompt);
        JSONObject formListJsonObject = formListRequest(formListParams);

        return formGet(formListJsonObject);
    }

    @Override
    protected String getParamPrompt(String... strings) {
        return String.format(GET_FORM_NAME_PROMPT_TEXT, strings[0]);
    }

    public JSONObject formGet(JSONObject jsonObject){
        log.info(jsonObject.toJSONString());
        JSONObject formObject = jsonObject.getJSONObject("formObject");
        if (formObject == null) {
            return null;
        }
        Integer formId = formObject.getInteger("formId");

        JSONObject fromGet = new JSONObject();
        // 添加 corpid 和 formId 字段
        fromGet.put("corpid", "ding012d1a0065f8b378ffe93478753d9884");
        fromGet.put("formId", formId);
        String bodyStr = fromGet.toString();

        String saasSign = calculateSHA256(bodyStr + "c3a09fbdc67731c1d8b40b380b7f203a");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("sign", saasSign);
        String fromGetResponse = OkHttpUtil.post("https://proapi.xbongbong.com/pro/v2/api/form/get", bodyStr, OkHttpUtil.APPLICATION_JSON_UTF8_VALUE, headers);
        JSONObject fromGetJson = JSONObject.parseObject(fromGetResponse);

        return fromGetJson.getJSONObject("result");
    }

    public JSONObject formListRequest(JSONObject gptResponse){
        String formName = gptResponse.getString("formName");
        JSONObject responseJson = new JSONObject();
        responseJson.put("formName", formName);

        JSONObject formList = new JSONObject();
        // 添加 corpid 和 formId 字段
        formList.put("corpid", "ding012d1a0065f8b378ffe93478753d9884");
        formList.put("name", formName);
        formList.put("saasMark", 1);
        String saasRequest = formList.toString();

        String saasSign = calculateSHA256(saasRequest + "c3a09fbdc67731c1d8b40b380b7f203a");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("sign", saasSign);
        log.info("计算sign：{}", saasSign);
        String saasResponse = OkHttpUtil.post("https://proapi.xbongbong.com/pro/v2/api/form/list", saasRequest, OkHttpUtil.APPLICATION_JSON_UTF8_VALUE, headers);
        JSONObject saasJson = JSONObject.parseObject(saasResponse);
        JSONArray jsonArray = saasJson.getJSONObject("result").getJSONArray("formList");
        if (jsonArray.size() > 0) {
            responseJson.put("formObject", jsonArray.getJSONObject(0));
            return responseJson;
        }

        formList.put("saasMark", 2);
        String paasRequest = formList.toString();
        String paasSign = calculateSHA256(paasRequest + "c3a09fbdc67731c1d8b40b380b7f203a");
        headers.put("sign", paasSign);
        String paasResponse = OkHttpUtil.post("https://proapi.xbongbong.com/pro/v2/api/form/list", paasRequest, OkHttpUtil.APPLICATION_JSON_UTF8_VALUE, headers);
        JSONObject paasJson = JSONObject.parseObject(paasResponse);
        jsonArray = paasJson.getJSONObject("result").getJSONArray("formList");
        if (jsonArray.size() > 0) {
            responseJson.put("formObject", jsonArray.getJSONObject(0));
            return responseJson;
        }

        return responseJson;
    }

    // 计算 SHA-256 的方法
    public static String calculateSHA256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
