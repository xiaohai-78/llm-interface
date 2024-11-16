package com.xiaohai.llminterface.service;

import com.alibaba.fastjson.JSONObject;
import com.xiaohai.llminterface.utils.OkHttpUtil;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: XiaoYunTao
 * @Date: 2024/10/14
 */
@Service
public class WatermarkActionService extends SystemActionAbstractService{

    /**
     * 生产水印的请求提示词
     */
    private static final String WATERMARK_PARAM_GENERATED_PROMPT_TEXT = """
   			%s
   
			你的任务是分析上面客户的输入，来生成相应控制系统水印开关的Json参数，其中0表示关闭，1表示打开。
			
			客户没有提到的水印开关信息就不用修改，保持原样。
			
			以下三个是关键参数，返回信息时必须携带：
			waterMarkState：全局水印状态,0表示关闭,1表示打开,全局水印状态打开时，才能显示下面的用户信息水印显示和公司名称水印显示；
			userInfoWaterMarkDisplay：用户信息水印显示,0表示关闭,1表示打开；
			companyNameWaterMarkDisplay：公司名称水印显示,0表示关闭,1表示打开
			
			原始水印开关信息： %s
			
			帮我生成修改后的Json参数。
			
			不需要多余解释，请你只返回Json信息。
			""";

    public WatermarkActionService(@Qualifier("ollamaChatModel") ChatModel chatModel) {
        super(chatModel);
    }

    @Override
    public JSONObject execute(String message) {
        // 获取当前水印状态
        JSONObject xbbState = getXbbWatermarkState();
        System.out.println(xbbState);
        String prompt = getParamPrompt(message, xbbState.toString());
        JSONObject gptParams = generateParamsFromText(prompt);
        return updataWaterMark(gptParams);
    }

    @Override
    protected String getParamPrompt(String... strings) {
        return String.format(WATERMARK_PARAM_GENERATED_PROMPT_TEXT, strings[0], strings[1]);
    }


    public JSONObject updataWaterMark(JSONObject watermarkState){
        Integer waterMarkState = watermarkState.getInteger("waterMarkState");
        Integer userInfoWaterMarkDisplay = watermarkState.getInteger("userInfoWaterMarkDisplay");
        Integer companyNameWaterMarkDisplay = watermarkState.getInteger("companyNameWaterMarkDisplay");

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
        return jsonObject.getJSONObject("result");
    }

    public JSONObject getXbbWatermarkState(){
        Map<String ,Object> hashMap = new HashMap();
        hashMap.put("userId", "xiao001");
        hashMap.put("corpid", "xbbxing");
        hashMap.put("platform", "all");
        hashMap.put("frontDev", "1");
        String s = OkHttpUtil.postCommon("http://127.0.0.1:2001/pro/v1/waterMark/getWaterMark", hashMap, OkHttpUtil.APPLICATION_JSON_UTF8_VALUE, null);
        JSONObject jsonObject = JSONObject.parseObject(s);
        return jsonObject.getJSONObject("result");
    }
}
