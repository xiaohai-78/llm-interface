package com.xiaohai.llminterface.service.actionImpl;

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
public class NewDataActionService extends SystemActionAbstractService {
    public NewDataActionService(@Qualifier("ollamaChatModel") ChatModel chatModel) {
        super(chatModel);
    }

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

    @Override
    public JSONObject execute(String message) {
        String addCustomerParamPrompt = getParamPrompt(message);
        JSONObject generateParams = generateParamsFromText(addCustomerParamPrompt);
        return addCustomerRequest(generateParams);
    }

    @Override
    protected String getParamPrompt(String... strings) {
        return String.format(GET_FORM_NAME_PROMPT_TEXT, strings[0]);
    }

    public JSONObject addCustomerRequest(JSONObject response){
        String name = response.getString("name");
        String age = response.getString("age");
        String sex = response.getString("sex");

        JSONObject addCustomer = new JSONObject();
        // 添加 corpid 和 formId 字段
        addCustomer.put("corpid", "ding012d1a0065f8b378ffe93478753d9884");
        addCustomer.put("formId", 8858717);
        // 创建 dataList 字段并添加子元素
        JSONObject dataList = new JSONObject();
        dataList.put("text_1", name);
        dataList.put("text_2", sex);
        dataList.put("text_3", age);
        // 将 dataList 放入最外层的 jsonObject
        addCustomer.put("dataList", dataList);
        String bodyStr = addCustomer.toString();

        String sign = calculateSHA256(bodyStr + "c3a09fbdc67731c1d8b40b380b7f203a");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("sign", sign);
        log.info("计算sign：{}", sign);

        String s = OkHttpUtil.post("https://proapi.xbongbong.com/pro/v2/api/paas/add", bodyStr, OkHttpUtil.APPLICATION_JSON_UTF8_VALUE, headers);
        JSONObject jsonObject = JSONObject.parseObject(s);
        return jsonObject.getJSONObject("result");
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
