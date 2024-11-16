package com.xiaohai.llminterface.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: XiaoYunTao
 * @Date: 2024/11/15
 */
@Configuration
public class OpenTelemetryConfig {
    private static final String JAEGER_ENDPOINT = "http://localhost:6831";

//    public static OpenTelemetry initializeOpenTelemetry() {
//        JaegerGrpcSpanExporter jaegerExporter = JaegerGrpcSpanExporter.builder()
//                .setEndpoint(JAEGER_ENDPOINT)
//                .build();
//
//        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
//                .addSpanProcessor(BatchSpanProcessor.builder(jaegerExporter).build())
//                .build();
//
//        return OpenTelemetrySdk.builder()
//                .setTracerProvider(tracerProvider)
//                .buildAndRegisterGlobal();
//    }

    @Bean
    public OpenTelemetry openTelemetry() {
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .build();
        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .build();
    }
}