package com.xiaohai.llminterface.controller;

import com.xiaohai.llminterface.entity.ChatOllamaDTO;
import com.xiaohai.llminterface.function.WatermarkFunctionService;
import com.xiaohai.llminterface.service.TestService;
import com.xiaohai.llminterface.service.XbbActionService;
import com.xiaohai.llminterface.service.impl.TestServiceImpl;
import io.micrometer.observation.ObservationRegistry;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: XiaoYunTao
 * @Date: 2024/10/12
 */
@RestController
@CrossOrigin
@RequestMapping("/chat")
public class ChatController {

    private final OllamaChatModel ollamaChatModel;

    @Resource
    private TestService testService;

    @Resource
    private XbbActionService xbbActionService;

    public final ChatClient ollamaChatClient;

    private final TestServiceImpl testServiceImpl;

    public ChatController(OllamaApi ollamaApi, OllamaOptions ollamaOptions, ObservationRegistry observationRegistry, TestServiceImpl testServiceImpl) {
        this.ollamaChatModel = new OllamaChatModel(
                ollamaApi,
                ollamaOptions,
                null,
                null,
                observationRegistry
        );
        this.ollamaChatClient = ChatClient.create(ollamaChatModel, observationRegistry);
        this.testServiceImpl = testServiceImpl;
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
     * 直接对话接口
     * @param chatOllamaDTO
     * @return
     */
    @PostMapping(value = "generateClient", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> generateClient(@RequestBody ChatOllamaDTO chatOllamaDTO) {
        String call = ollamaChatClient.prompt().user(chatOllamaDTO.getMessage()).call().content();
        System.out.println(call);
        return Map.of("message", call);
    }

    /**
     * 直接对话接口
     * @param chatOllamaDTO
     * @return
     */
    @PostMapping(value = "generateChatModel", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> generateChatModel(@RequestBody ChatOllamaDTO chatOllamaDTO) {
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

    @GetMapping(value = "fluxTest")
    public void fluxTest() {
        testService.fluxTest();
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
        String aiMessage = ollamaChatClient.prompt()
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

    @PostMapping(value = "observation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> observation(@RequestBody ChatOllamaDTO chatOllamaDTO) {
        String content = ollamaChatClient.prompt()
                .function("watermarkFunctionService", "更新水印状态", new WatermarkFunctionService())
                .system("你是一个alibabaAi助手，请礼貌的回答用户问题")
                .user(chatOllamaDTO.getMessage())
                .call().content();
        System.out.println(content);
        return Map.of("message", content);
    }

    @PostMapping(value = "testServiceImpl", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> testServiceImpl(@RequestBody @Validated ChatOllamaDTO chatOllamaDTO) {
        testServiceImpl.test2();
        System.out.println(chatOllamaDTO);
        return Map.of("message", "success");
    }

    @PostMapping(value = "testSse")
    public Flux<String> testSse(@RequestBody ChatOllamaDTO chatOllamaDTO) {
        Flux<String> content = ollamaChatClient.prompt()
                .function("watermarkFunctionService", "更新水印状态", new WatermarkFunctionService())
                .system("你是一个alibabaAi助手，请礼貌的回答用户问题")
                .user(chatOllamaDTO.getMessage()).stream().content();
        return content;
    }

    @GetMapping(value = "testSse")
    public Flux<ChatResponse> testGetSse() {
        return ollamaChatClient.prompt()
                .function("watermarkFunctionService", "更新水印状态", new WatermarkFunctionService())
                .system("你是一个alibabaAi助手，请礼貌的回答用户问题")
                .user("你是谁？ 你能做什么？").stream().chatResponse();
    }

    @GetMapping(value = "testSseString", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> testGetSseString(String input) {
        return ollamaChatClient.prompt()
                .function("watermarkFunctionService", "更新水印状态", new WatermarkFunctionService())
                .system("你是一个alibabaAi助手，请礼貌的回答用户问题")
                .user(input).stream().content();
    }
}