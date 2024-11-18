package com.xiaohai.llminterface.observation;

import com.alibaba.fastjson.JSON;
import com.xiaohai.llminterface.ali.entity.ModelObservationEntity;
import com.xiaohai.llminterface.ali.mapper.ModelObservationMapper;
import com.xiaohai.llminterface.entity.NewContext;
import io.micrometer.common.KeyValue;
import io.micrometer.core.instrument.Clock;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.observation.ChatClientObservationContext;
import org.springframework.ai.chat.observation.ChatModelObservationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: AlibabaObservationHandler
 * @Author: 肖云涛
 * @Date: 2024/11/17
 */
public class AlibabaObservationHandler implements ObservationHandler<Observation.Context> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlibabaObservationHandler.class);
    private final Clock clock;
    private final Tracer tracer;

    private final ModelObservationMapper modelObservationMapper;

    public AlibabaObservationHandler(Clock clock, ModelObservationMapper modelObservationMapper) {
        this.clock = clock;
        this.tracer = GlobalOpenTelemetry.getTracer("com.alibaba.cloud.ai");
        this.modelObservationMapper = modelObservationMapper;
    }

    @Override
    public void onStart(Observation.Context context) {
        long startTime = clock.monotonicTime();
        context.put("startTime", startTime);

        SpanBuilder spanBuilder = tracer.spanBuilder(context.getName())
                .setAttribute("component", "AlibabaChatClient")
                .setAttribute("start_time", startTime);
        Span span = spanBuilder.startSpan();

        context.put("span", span);
        LOGGER.info("onStart: Operation '{}' started. Start time: {}", context.getName(), startTime);
    }

    @Override
    public void onStop(Observation.Context context) {
        long startTime = context.getOrDefault("startTime", 0L);
        long endTime = clock.monotonicTime();
        long duration = endTime - startTime;

        Span span = context.getOrDefault("span", null);
        if (span != null) {
            span.setAttribute("duration_ns", duration);
            span.end();
        }
        // 获取当前时间
        ZonedDateTime now = ZonedDateTime.now();

        // 格式化时间为指定格式
        String formattedTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

        // 判断 context 是否是 ChatClientObservationContext
        if (context instanceof ChatClientObservationContext clientContext) {
            ModelObservationEntity modelObservationEntity = new ModelObservationEntity();
            modelObservationEntity.setName(clientContext.getName());
            modelObservationEntity.setModel(clientContext.getRequest().getChatOptions().getModel());
            modelObservationEntity.setTotalTokens(clientContext.getRequest().getChatOptions().getMaxTokens());
            modelObservationEntity.setAddTime(formattedTime);
            modelObservationMapper.insert(modelObservationEntity);
            LOGGER.info("Processed ChatClientObservationContext: {}", clientContext);
        } else if (context instanceof ChatModelObservationContext modelContext) {
            ModelObservationEntity modelObservationEntity = new ModelObservationEntity();
            modelObservationEntity.setName(modelContext.getName());
            modelObservationEntity.setModel(modelContext.getRequest().getOptions().getModel());
            modelObservationEntity.setTotalTokens(Math.toIntExact(modelContext.getResponse().getMetadata().getUsage().getTotalTokens()));
            modelObservationEntity.setAddTime(formattedTime);
            modelObservationMapper.insert(modelObservationEntity);
            LOGGER.info("Processed ChatClientObservationContext: {}", modelContext);
        } else {
            LOGGER.warn("Unknown Observation.Context type: {}", context.getClass());
        }

        LOGGER.info("onStop: Operation '{}' completed. Duration: {} ns", context.getName(), duration);
    }

    @Override
    public void onError(Observation.Context context) {
        Span span = context.getOrDefault("span", null);
        if (span != null) {
            span.setAttribute("error", true);
            span.setAttribute("error.message", context.getError().getMessage());
            span.recordException(context.getError());
        }
        LOGGER.error("onError: Operation '{}' failed with error: {}", context.getName(), context.getError().getMessage());
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return true;
    }

    public static NewContext extractFromContext(Observation.Context context) {
        NewContext newContext = new NewContext();

        // Convert allKeyValues to Map
        newContext.setAllKeyValues(context.getAllKeyValues().stream()
            .collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue)));

        // Convert highCardinalityKeyValues to Map
        newContext.setHighCardinalityKeyValues(context.getHighCardinalityKeyValues().stream()
            .collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue)));

        // Convert lowCardinalityKeyValues to Map
        newContext.setLowCardinalityKeyValues(context.getLowCardinalityKeyValues().stream()
            .collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue)));

        // Set other fields
        newContext.setContextualName(context.getContextualName());
        newContext.setName(context.getName());

        ChatModelObservationContext chatModelObservationContext = (ChatModelObservationContext) context;
        System.out.println(chatModelObservationContext.getResponse());

        return newContext;
    }
}
