package com.xiaohai.llminterface.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: XiaoYunTao
 * @Date: 2024/10/14
 */
@Service
public class NewCustomerActionService extends SystemActionAbstractService{
    public NewCustomerActionService(@Qualifier("ollamaChatModel") ChatModel chatModel) {
        super(chatModel);
    }

    @Override
    public JSONObject execute(String message) {
        return null;
    }

    @Override
    protected String getParamPrompt(String... strings) {
        return null;
    }

}
