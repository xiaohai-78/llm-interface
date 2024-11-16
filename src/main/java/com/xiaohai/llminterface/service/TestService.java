package com.xiaohai.llminterface.service;

import com.xiaohai.llminterface.entity.ActorsFilms;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Description:
 * @Author: XiaoYunTao
 * @Date: 2024/10/12
 */
@Service
public class TestService {

    private final ChatModel ollamaChatModel;

    public TestService(ChatModel ollamaChatModel) {
        this.ollamaChatModel = ollamaChatModel;
    }


    public ActorsFilms conversionTest() {
        BeanOutputConverter<ActorsFilms> beanOutputConverter =
                new BeanOutputConverter<>(ActorsFilms.class);

        String format = beanOutputConverter.getFormat();

        String actor = "Tom Hanks";

        String template = """
        Generate the filmography of 5 movies for {actor}.
        {format}
        """;

        Generation generation = ollamaChatModel.call(
                new PromptTemplate(template, Map.of("actor", actor, "format", format)).create()).getResult();

        ActorsFilms actorsFilms = beanOutputConverter.convert(generation.getOutput().getContent());

        return actorsFilms;
    }
}
