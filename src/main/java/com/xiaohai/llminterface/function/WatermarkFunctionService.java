package com.xiaohai.llminterface.function;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.xiaohai.llminterface.entity.WaterMarkDTO;
import com.xiaohai.llminterface.utils.OkHttpUtil;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @Description:
 * @Author: XiaoYunTao
 * @Date: 2024/10/17
 */
@JsonClassDescription("控制系统内的水印状态开启或者关闭")
@Service
@Description("控制系统内的水印状态开启或者关闭")
public class WatermarkFunctionService implements Function<WaterMarkDTO, JSONObject> {
    @Override
    public JSONObject apply(WaterMarkDTO waterMarkDTO) {

        Integer waterMarkState = waterMarkDTO.getWaterMarkState();
        Integer userInfoWaterMarkDisplay = waterMarkDTO.getUserInfoWaterMarkDisplay();
        Integer companyNameWaterMarkDisplay = waterMarkDTO.getCompanyNameWaterMarkDisplay();

        Map<String ,Object> hashMap = new HashMap<>();
        hashMap.put("userId", "xiao001");
        hashMap.put("corpid", "xbbxing");
        hashMap.put("platform", "all");
        hashMap.put("frontDev", "1");
        hashMap.put("waterMarkState", waterMarkState);
        hashMap.put("userInfoWaterMarkDisplay", userInfoWaterMarkDisplay);
        hashMap.put("companyNameWaterMarkDisplay", companyNameWaterMarkDisplay);
        String s = OkHttpUtil.postCommon("http://127.0.0.1:2001/pro/v1/waterMark/updataWaterMark", hashMap, OkHttpUtil.APPLICATION_JSON_UTF8_VALUE, null);
        JSONObject jsonObject = JSONObject.parseObject(s);
        return jsonObject;
    }
}
