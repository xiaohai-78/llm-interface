package com.xiaohai.llminterface.service.actionImpl;

import com.alibaba.fastjson.JSONObject;
import com.xiaohai.llminterface.service.SystemActionAbstractService;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @Description: 自然对话
 * @Author: XiaoYunTao
 * @Date: 2024/10/15
 */
@Service
public class NaturalDialogAcionService extends SystemActionAbstractService {

    /**
     * 自然对话提示词
     */
    private static final String NATURAL_DIALOG_PROMPT_TEXT = """
               请你作为销帮帮CRM智能助手，帮助客户了解和使用销帮帮CRM。
               
               下面是客户的对话内容：
            			%s
            			
               请你尽量礼貌的回复客服的问题。
               
               你只需返回对客户回复的内容。
            """;

    public NaturalDialogAcionService(ChatModel ollamaChatModel) {
        super(ollamaChatModel);
    }

    @Override
    public JSONObject execute(String message) {
        // 使用 getParamPrompt 获取 prompt 文本
        String response = ollamaChatModel.call(new Prompt(getParamPrompt(message)))
                .getResult()
                .getOutput()
                .getContent();

        // 创建并返回 JSONObject
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("response", response);
        return jsonResponse;
    }


    @Override
    protected String getParamPrompt(String... strings) {
        return String.format(NATURAL_DIALOG_PROMPT_TEXT, strings[0]);
    }
}
