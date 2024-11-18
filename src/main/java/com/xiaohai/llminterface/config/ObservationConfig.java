package com.xiaohai.llminterface.config;

import com.xiaohai.llminterface.ali.mapper.ModelObservationMapper;
import com.xiaohai.llminterface.observation.AlibabaObservationHandler;
import io.micrometer.core.instrument.Clock;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: XiaoYunTao
 * @Date: 2024/11/18
 */
@Configuration
public class ObservationConfig {

    @Bean
    public ObservationRegistry observationRegistry(ModelObservationMapper modelObservationMapper) {
        ObservationRegistry observationRegistry = ObservationRegistry.create();
        observationRegistry.observationConfig().observationHandler(new AlibabaObservationHandler(modelObservationMapper));
        return observationRegistry;
    }

    @Bean
    public OllamaOptions ollamaOptions(ChatModel ollamaChatModel) {
        return (OllamaOptions)ollamaChatModel.getDefaultOptions();
    }
}
