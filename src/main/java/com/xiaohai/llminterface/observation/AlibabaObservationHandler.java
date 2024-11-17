package com.xiaohai.llminterface.observation;

import com.alibaba.fastjson.JSON;
import com.xiaohai.llminterface.entity.NewContext;
import io.micrometer.common.KeyValue;
import io.micrometer.core.instrument.Clock;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.observation.ChatModelObservationContext;

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

    public AlibabaObservationHandler(Clock clock) {
        this.clock = clock;
        this.tracer = GlobalOpenTelemetry.getTracer("com.alibaba.cloud.ai");
    }

    @Override
    public void onStart(Observation.Context context) {
        long startTime = clock.monotonicTime();
        context.put("startTime", startTime);

        // 创建 OpenTelemetry Span
        SpanBuilder spanBuilder = tracer.spanBuilder(context.getName())
                .setAttribute("component", "AlibabaChatClient")
                .setAttribute("start_time", startTime);
        Span span = spanBuilder.startSpan();

        context.put("span", span);
        LOGGER.info("Operation '{}' started. Start time: {}", context.getName(), startTime);
    }

    @Override
    public void onStop(Observation.Context context) {
        long startTime = context.getOrDefault("startTime", 0L);
        long endTime = clock.monotonicTime();
        long duration = endTime - startTime;

        // 获取并结束 Span
        Span span = context.getOrDefault("span", null);
        if (span != null) {
            span.setAttribute("duration_ns", duration);
            span.end();
        }

        extractFromContext(context);

        LOGGER.info("Operation '{}' completed. Duration: {} ns", context.getName(), duration);
    }

    @Override
    public void onError(Observation.Context context) {
        Span span = context.getOrDefault("span", null);
        if (span != null) {
            span.setAttribute("error", true);
            span.setAttribute("error.message", context.getError().getMessage());
            span.recordException(context.getError());
        }
        LOGGER.error("Operation '{}' failed with error: {}", context.getName(), context.getError().getMessage());
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
