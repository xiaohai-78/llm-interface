package com.xiaohai.llminterface.function;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.xiaohai.llminterface.entity.NewCustomerPojo;
import com.xiaohai.llminterface.utils.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @Description:
 * @Author: XiaoYunTao
 * @Date: 2024/10/18
 */
@Service
@Slf4j
@Description("新建客户")
public class NewCustomerFuncion implements Function<NewCustomerPojo, String> {

    @Override
    public String apply(NewCustomerPojo newCustomerPojo) {

        return addCustomerRequest(newCustomerPojo);
    }

    public String addCustomerRequest(NewCustomerPojo newCustomerPojo){
        String name = newCustomerPojo.getName();
        String age = newCustomerPojo.getAge();
        String sex = newCustomerPojo.getSex();

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

        String result = OkHttpUtil.post("https://proapi.xbongbong.com/pro/v2/api/paas/add", bodyStr, OkHttpUtil.APPLICATION_JSON_UTF8_VALUE, headers);

        return result;
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
