package com.xiaohai.llminterface.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @Description:
 * @Author: XiaoYunTao
 * @Date: 2024/10/14
 */
@Slf4j
public abstract class SystemActionAbstractService {

    protected final ChatModel ollamaChatModel;

    public SystemActionAbstractService(ChatModel ollamaChatModel) {
        this.ollamaChatModel = ollamaChatModel;
    }

    public abstract JSONObject execute(String message);

    /**
     * 获取参数提示词
     * @param strings
     * @return
     */
    protected abstract String getParamPrompt(String ...strings);

    /**
     * 自然语言生成请求参数
     * @param prompt
     * @return
     */
    public JSONObject generateParamsFromText(String prompt){
        ChatResponse chatResponse = ollamaChatModel.call(new Prompt(prompt));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(chatResponse.getResult().getOutput().getContent());
        }catch (Exception e){
            log.warn("解析参数失败", e);
            jsonObject = retryGPTParam(prompt, 1);
        }
        return jsonObject;
    }

    public JSONObject retryGPTParam(String prompt, Integer retryNum){
        if (retryNum > 3){
            return null;
        }
        prompt = prompt + "请不要返回其他信息，只返回Json格式的参数，并且参数格式必须正确，否则请重新输入。";
        ChatResponse chatResponse = ollamaChatModel.call(new Prompt(prompt));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(chatResponse.getResult().getOutput().getContent());
        }catch (Exception e){
            jsonObject = retryGPTParam(prompt, ++retryNum);
        }
        return jsonObject;
    }
}
