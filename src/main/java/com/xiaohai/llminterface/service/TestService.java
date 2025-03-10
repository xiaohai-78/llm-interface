package com.xiaohai.llminterface.service;

import com.xiaohai.llminterface.entity.ActorsFilms;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
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

    // 帮我使用Flux写一个demo，帮助我了解和使用它
    public void fluxTest(){
        // 创建一个发射整数 1 到 5 的 Flux
        Flux<Integer> integerFlux = Flux.range(1, 5);

        // 订阅并打印每个发射的整数，每次打印后延迟1秒
        integerFlux
                .flatMap(number -> Mono.just(number)
                        .doOnNext(n -> System.out.println("Received: " + n))
                        .delayElement(Duration.ofSeconds(1)))
                .subscribe(
                        number -> {}, // 这里不需要再打印，已经在 doOnNext 中打印了
                        error -> System.err.println("Error: " + error.getMessage()),
                        () -> System.out.println("Completed integerFlux")
                );

        // 使用 map 方法将每个整数乘以 2
        Flux<Integer> doubledFlux = integerFlux.map(number -> number * 2);
        // 订阅并打印每个发射的整数
        doubledFlux.subscribe(
                number -> System.out.println("Received: " + number),
                error -> System.err.println("Error: " + error.getMessage()),
                () -> System.out.println("Completed doubledFlux")
        );

        // 创建一个发射字符串的 Flux
        Flux<String> stringFlux = Flux.just("Hello", "World", "Flux");
        // 订阅并打印每个发射的字符串
        stringFlux.subscribe(
                this::sleepTest,
                error -> System.err.println("Error: " + error.getMessage()),
                () -> System.out.println("Completed stringFlux")
        );

        // 创建一个发射整数 1 到 5 的 Flux
        Flux<Integer> integerFlux2 = Flux.range(1, 10);
        integerFlux2.subscribe(
                this::sleepTest,
                error -> System.err.println("Error: " + error.getMessage()),
                () -> System.out.println("Completed integerFlux2")
        );

        // 创建一个发射字符串的 Flux
        Flux<String> stringFlux2 = Flux.just("Hello", "World", "Flux");
        // 使用 doOnComplete 打印完成消息
        stringFlux2
                .doOnComplete(() -> System.out.println("String Flux completed stringFlux2"))
                .subscribe(
                        message -> System.out.println("Received: " + message),
                        error -> System.err.println("Error: " + error.getMessage())
                );
    }

    private void sleepTest(Object t) {
        System.out.println("sleepTest Received: " + t);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
