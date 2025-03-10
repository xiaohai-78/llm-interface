package com.xiaohai.llminterface.service;

import com.xiaohai.llminterface.enums.SystemActionEnum;
import com.xiaohai.llminterface.function.IdentifyIntentService;
import com.xiaohai.llminterface.function.NewCustomerFuncion;
import com.xiaohai.llminterface.function.WatermarkFunctionService;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 销帮帮动作
 * @Author: XiaoYunTao
 * @Date: 2024/10/18
 */
@Service
public class XbbActionService {

    private static final Tracer tracer = GlobalOpenTelemetry.get().getTracer("com.xiaohai.llminterface.service");


    private final OllamaChatModel ollamaChatModel;
    private final AnalysisIntentionService analysisIntentionService;
    private final ChatClient chatClient;

    private final String XBB_ACTION_PROMPT_TEXT = """
                请你作为销帮帮CRM智能助手，帮助客户了解和使用销帮帮CRM。
            			
               请你尽量礼貌的回复客服的问题。
               
               你只需返回对客户回复的内容。
               
               你可以使用function中的函数调用接口来实现客户的需求。
            """;

//    @Autowired
    public XbbActionService(OllamaChatModel ollamaChatModel, AnalysisIntentionService analysisIntentionService) {
        this.ollamaChatModel = ollamaChatModel;
        this.analysisIntentionService = analysisIntentionService;
        this.chatClient = ChatClient.create(this.ollamaChatModel);
    }

    /**
     * 历史消息存储
     */
    List<Message> messages = new ArrayList<>();

//    public String xbbAction(String message) {
//        messages.add(new UserMessage(message));
//        String content = chatClient.prompt()
//                .system(XBB_ACTION_PROMPT_TEXT)
//                .messages(messages)
//                .user(message)
//                .functions("newCustomerFuncion", "watermarkFunctionService")
//                .call()
//                .content();
//        messages.add(new AssistantMessage(content));
//        return content;
//    }

    public String xbbAction(String message) {
        Span span = tracer.spanBuilder("xbbAction").startSpan();
        try (Scope scope = span.makeCurrent()) {
            long startTime = System.currentTimeMillis();

            // 添加输入参数
            span.setAttribute("request.message", message);

            messages.add(new UserMessage(message));
            String content = chatClient.prompt()
                    .system(XBB_ACTION_PROMPT_TEXT)
                    .messages(messages)
                    .user(message)
                    .functions("newCustomerFuncion", "watermarkFunctionService")
                    .call()
                    .content();

            messages.add(new AssistantMessage(content));

            long endTime = System.currentTimeMillis();

            // 添加链路属性
            span.setAttribute("response.content", content);
            span.setAttribute("execution.time.ms", endTime - startTime);

            return content;
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, "Exception occurred during xbbAction");
            throw e;
        } finally {
            span.end();
        }
    }


    public String xbbAction(String message, List<Message> messages) {

        SystemActionEnum systemActionEnum = analysisIntentionService.getSystemActionEnum(message);

        String aiMessage = "";
        switch (systemActionEnum) {
            case WATERMARK:
                aiMessage =  updateWaterMark(message, messages);
                break;
            case NATURAL_DIALOG:
                aiMessage = naturalDialog(message, messages);
                break;
            case CUSTOMER_ADD:
                aiMessage = addCustomer(message, messages);
                break;
            default:
                aiMessage = naturalDialog(message, messages);
                break;
        }

        return aiMessage;
    }

    public String updateWaterMark(String message, List<Message> messages) {

        messages.add(new UserMessage(message));
        String aiMessage = chatClient.prompt()
                .function("updataWatermark", "更新水印状态", new WatermarkFunctionService())
                .messages(messages)
                .call()
                .content();
        messages.add(new AssistantMessage(aiMessage));
        return aiMessage;
    }

    public SystemActionEnum identifyIntention(String message, List<Message> messages) {
        messages.add(new UserMessage(message));
        String aiMessage = chatClient.prompt()
                .function("identifyIntention", "分析用户的需求", new IdentifyIntentService())
                .messages(messages)
                .call()
                .content();
        messages.add(new AssistantMessage(aiMessage));
        System.out.println("identifyIntention: " + aiMessage);
        return SystemActionEnum.getByActive(aiMessage);
    }

    public String naturalDialog(String message, List<Message> messages) {
        messages.add(new UserMessage(message));
        String aiMessage = chatClient.prompt()
                .messages(messages)
                .call()
                .content();
        messages.add(new AssistantMessage(aiMessage));
        return aiMessage;
    }

    public String addCustomer(String message, List<Message> messages) {
        messages.add(new UserMessage(message));
        String aiMessage = chatClient.prompt()
                .function("addCustomer", "新建客户", new NewCustomerFuncion())
                .messages(messages)
                .call()
                .content();
        return aiMessage;
    }

}
