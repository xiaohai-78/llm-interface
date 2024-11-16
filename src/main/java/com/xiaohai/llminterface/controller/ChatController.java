package com.xiaohai.llminterface.controller;

import com.alibaba.fastjson.JSONObject;
import com.xiaohai.llminterface.entity.ChatOllamaDTO;
import com.xiaohai.llminterface.function.WatermarkFunctionService;
import com.xiaohai.llminterface.service.SystemActionAbstractService;
import com.xiaohai.llminterface.service.SystemActionServiceFactory;
import com.xiaohai.llminterface.service.TestService;
import com.xiaohai.llminterface.service.XbbActionService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: XiaoYunTao
 * @Date: 2024/10/12
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final OllamaChatModel ollamaChatModel;

    @Resource
    private TestService testService;

    @Resource
    private XbbActionService xbbActionService;


    public ChatController(OllamaChatModel ollamaChatModel) {
        this.ollamaChatModel = ollamaChatModel;
    }

    public ChatClient getOllamaChatClient(){
        return ChatClient.create(this.ollamaChatModel);
    }

    List<Message> messages = new ArrayList<>();

    /**
     * 直接对话接口
     * @param chatOllamaDTO
     * @return
     */
    @PostMapping(value = "generate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> generate(@RequestBody ChatOllamaDTO chatOllamaDTO) {
        String call = ollamaChatModel.call(chatOllamaDTO.getMessage());
        System.out.println(call);
        return Map.of("message", call);
    }

    /**
     * @return
     */
    @GetMapping(value = "conversion")
    public Map<String, String> allNews() {
        var actorsFilms = testService.conversionTest();
        String actor = actorsFilms.actor();
        System.out.println(actorsFilms);
        return Map.of("message", actorsFilms.toString());
    }

    @PostMapping(value = "analysisIntention", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String analysisIntention(@RequestBody ChatOllamaDTO chatOllamaDTO) {
        String message = chatOllamaDTO.getMessage();
        return xbbActionService.xbbAction(message, new ArrayList<>());
    }

    @PostMapping(value = "functionTest", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String analysisIntfunctionTestention(@RequestBody ChatOllamaDTO chatOllamaDTO) {
        String message = chatOllamaDTO.getMessage();
        messages.add(new UserMessage(message));
        String aiMessage = getOllamaChatClient().prompt()
                .function("updataWatermark", "更新水印状态", new WatermarkFunctionService())
                .messages(messages)
                .call()
                .content();
        messages.add(new AssistantMessage(aiMessage));
        return aiMessage;
    }

    @PostMapping(value = "analysisIntentionFunction", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String analysisIntentionFunction(@RequestBody ChatOllamaDTO chatOllamaDTO) {
        String message = chatOllamaDTO.getMessage();
        return xbbActionService.xbbAction(message);
    }
}