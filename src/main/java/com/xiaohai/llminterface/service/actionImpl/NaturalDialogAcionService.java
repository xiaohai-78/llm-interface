package com.xiaohai.llminterface.service.actionImpl;

import com.alibaba.fastjson.JSONObject;
import com.xiaohai.llminterface.service.SystemActionAbstractService;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @Description: 自然对话
 * @Author: XiaoYunTao
 * @Date: 2024/10/15
 */
@Service
public class NaturalDialogueAcionService extends SystemActionAbstractService {

    public NaturalDialogueAcionService(@Qualifier("ollamaChatModel") ChatModel chatModel) {
        super(chatModel);
    }

    /**
     * 自然对话提示词
     */
    private static final String WATERMARK_PARAM_GENERATED_PROMPT_TEXT = """
            			%s
               
            你的任务是分析上面客户的输入，来生成相应新建客户信息的Json参数。
            		
            如：
                     {
                         "name": "姓名",
                         "age": "年龄",
                         "sex": "性别"
                     }
            			
            帮我根据客户输入生成上述Json参数。
            			
            不需要多余解释，请你只返回Json信息。
            """;

    @Override
    public JSONObject execute(String message) {
        return null;
    }

    @Override
    protected String getParamPrompt(String... strings) {
        return null;
    }
}
