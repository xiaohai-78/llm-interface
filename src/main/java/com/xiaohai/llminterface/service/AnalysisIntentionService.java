package com.xiaohai.llminterface.service;

import com.xiaohai.llminterface.enums.SystemActionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @Description: 分析意图
 * @Author: XiaoYunTao
 * @Date: 2024/10/12
 */
@Slf4j
@Service
public class AnalysisIntentionService {

    private static final String ANALYSIS_INTENTION_PROMPT_TEXT = """
   			%s
   
			你的任务是分析上面问题从下面的选项中，帮我找出最符合用户意图的其中一个选项。
			
			你有 %s 个选项来回答，分别是 %s 。
			
			每个选项代表的含义分别是： %s 。
			
			你只需要回复我选项值，不需要多余解释。
			
			如：ADD_DATA
			或者：NATURAL_DIALOG
			
			请你按照规定回复格式进行返回信息，只需返回其中一个选项。
			""";

	private final ChatModel chatModel;

	public AnalysisIntentionService(@Qualifier("ollamaChatModel") ChatModel chatModel) {
		this.chatModel = chatModel;
	}

	public String getResponseFromGPT(String query) {
		String prompt = String.format(ANALYSIS_INTENTION_PROMPT_TEXT, query, SystemActionEnum.getAllActive().size(), SystemActionEnum.getAllActive(), SystemActionEnum.getAll());
		ChatResponse chatResponse = chatModel.call(new Prompt(prompt));
		String responseString = chatResponse.getResult().getOutput().getContent();
		log.info("response: {}", responseString);
		return responseString;
	}

	public SystemActionEnum getSystemActionEnum(String query) {
		String responseFromGPT = getResponseFromGPT(query);
		return SystemActionEnum.getByActive(responseFromGPT);
	}


	public static void main(String[] args) {
		System.out.println(String.format(ANALYSIS_INTENTION_PROMPT_TEXT, SystemActionEnum.getAllActive().size(), SystemActionEnum.getAllActive()));

	}

}
