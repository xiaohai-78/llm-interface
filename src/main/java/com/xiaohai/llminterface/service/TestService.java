package com.xiaohai.llminterface.service;

import com.alibaba.fastjson.JSONObject;
import com.xiaohai.llminterface.entity.ActorsFilms;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public String testAssert(String msg) {
        Assert.notNull(msg, "msg cannot be null");
        return msg;
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

    private final static String apiUri = "/open/api/inner/v1/ai/selectByRecordId";

    private final static String url = "https://adic.d.design/open/api/inner/v1/ai/selectByRecordId";
    public static List<String> getImageUrl(String recordPublicId) {
        List<String> imgUrlList = new ArrayList<>();

        Map<String, String> headers = new HashMap<>();
        Map<String, String> dataSelect = new HashMap<>();
        WebClient webClient = WebClient.builder()
                .baseUrl("https://adic.d.design")
                .defaultHeader("Content-Type", "application/json")
                .build();
        try {
            // 生成时间戳
            long timestamp = System.currentTimeMillis();

            // 生成签名（需要根据实际签名逻辑实现）
            String signature = createSignature(String.valueOf(timestamp), apiUri);

            // 设置请求头
            headers.put("X-Auth-Signature", signature);
            headers.put("X-Request-Timestamp", String.valueOf(timestamp));

            // 设置请求体
            dataSelect.put("publicId", recordPublicId);

            // 发送 POST 请求
            Mono<Map> responseMono = webClient.post()
                    .uri(apiUri)
                    .headers(httpHeaders -> httpHeaders.setAll(headers))
                    .bodyValue(dataSelect)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(5));// 设置超时时间

            Map response = responseMono.block();

            System.out.println(response);
            // 处理响应
            if (response != null && (int) response.get("code") == 200) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if ("SUCCEED".equals(data.get("status"))) {
//                    List<Map<String, String>> worksVOList = (List<Map<String, String>>) data.get("worksVOList");
//                    for (Map<String, String> work : worksVOList) {
//                        imgUrlList.add(work.get("result"));
//                    }
                    System.out.println(data.toString());
                    return imgUrlList;
                }
            }
        } catch (Exception e) {
            System.err.println("请求失败: " + e.getMessage());
            // 可选：记录更详细的错误信息
            e.printStackTrace();
        }
        return null;
    }

    public static String createSignature(String timestamp, String uri) {
        String method = "POST";
        String secretKey = "1O9F8NEyK3iP4KmJig4r4SQPyqLGSQrsRU5VV4+OC/s=";
        if (uri == null || uri.isEmpty()) {
            uri = "/open/api/inner/v1/ai/generate";
        }
        String dataToSign = method + "&" + uri + "&" + timestamp;

        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKeySpec);

            return bytesToHex(sha256Hmac.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC SHA256 signature", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static void main(String[] args) {

        Integer dsInterval = 0;
        System.out.println("ds=" + DateTimeFormatter.ofPattern("yyyyMMdd")
                .format(LocalDate.now().minusDays(Math.abs(Objects.nonNull(dsInterval) ? dsInterval : -1))));
    }



}
