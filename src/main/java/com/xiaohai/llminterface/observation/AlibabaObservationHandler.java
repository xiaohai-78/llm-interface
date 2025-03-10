package com.xiaohai.llminterface.observation;

import com.xiaohai.llminterface.ali.entity.ModelObservationEntity;
import com.xiaohai.llminterface.ali.mapper.ModelObservationMapper;
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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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

    public AlibabaObservationHandler(ModelObservationMapper modelObservationMapper) {
        this.clock = Clock.SYSTEM;
        this.tracer = GlobalOpenTelemetry.getTracer("com.alibaba.cloud.ai");
        this.modelObservationMapper = modelObservationMapper;
    }

    @Override
    public void onStart(Observation.Context context) {
        long startTime = clock.monotonicTime() / 1000000;
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
        long endTime = clock.monotonicTime() / 1000000;
        long duration = endTime - startTime;

        Span span = context.getOrDefault("span", null);
        if (span != null) {
            span.setAttribute("duration_ns", duration);
            span.end();
        }

        ZonedDateTime now = ZonedDateTime.now();
        String formattedTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

//        if (context instanceof ChatModelObservationContext modelContext) {
//            ModelObservationEntity modelObservationEntity = new ModelObservationEntity();
//            modelObservationEntity.setName(modelContext.getName());
//            modelObservationEntity.setAddTime(formattedTime);
//            modelObservationEntity.setDuration(duration);
//            modelObservationEntity.setModel(modelContext.getLowCardinalityKeyValue("gen_ai.response.model").getValue());
//            modelObservationEntity.setTotalTokens(Math.toIntExact(modelContext.getResponse().getMetadata().getUsage().getTotalTokens()));
//            modelObservationMapper.insert(modelObservationEntity);
//        } else {
//            LOGGER.warn("Unknown Observation.Context type: {}", context.getClass());
//        }

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

}
