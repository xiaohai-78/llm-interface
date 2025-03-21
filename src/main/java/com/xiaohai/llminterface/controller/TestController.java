package com.xiaohai.llminterface.controller;

import com.xiaohai.llminterface.service.AbcService;
import com.xiaohai.llminterface.service.TestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

/**
 * @author xiaoyuntao
 * @date 2025/02/19
 */
@RestController
@RequestMapping("/test")
public class TestController {

    private final AbcService abcService;

    private static final String FILE_PATH = "static/robots.txt";

    private final RestClient restClient;

    private final TestService testService;

    @RequestMapping("/")
    public String test() {
        return "test";
    }

    public TestController(AbcService abcService, RestClient.Builder restClientBuilder, TestService testService) {
        this.abcService = abcService;
        this.restClient = restClientBuilder.build();
        this.testService = testService;
    }

    @RequestMapping("/test1")
    public String test1() {
        abcService.test();
        return "success";
    }

    @RequestMapping("/test2")
    public String test2(String urlPath) {
        String result = restClient.get()
                .uri("https://{urlPath}.com", urlPath)
                .retrieve()
                .body(String.class);
        System.out.println(result);
        return result;
    }

    @RequestMapping("/testAssert")
    public String testAssert(String msg) {
        return testService.testAssert(msg);
    }

//    @GetMapping("/setting.txt")
//    public ResponseEntity<String> getRobotsTxt() {
//        Resource resource = new ClassPathResource("setting.xml");
//        try {
//            String content = new String(Files.readAllBytes(resource.getFile().toPath()));
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
//            return new ResponseEntity<>(content, headers, HttpStatus.OK);
//        } catch (IOException e) {
//            return new ResponseEntity<>("robots.txt file not found", HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @GetMapping("/robot.txt")
//    public void getRobotsTxt(HttpServletResponse response) {
//        Resource resource = new ClassPathResource(FILE_PATH);
//        try {
//            String content = new String(Files.readAllBytes(resource.getFile().toPath()));
//            response.setContentType("text/plain; charset=UTF-8");
//            response.getWriter().write(content);
//        } catch (IOException e) {
//            response.setStatus(HttpStatus.NOT_FOUND.value());
//            try {
//                response.getWriter().write("robots.txt file not found");
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
//
//    @GetMapping("/setting.xml")
//    public ResponseEntity<Resource> getSettingXml() {
//        Resource resource = new ClassPathResource("setting.xml");
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=setting.xml");
//            headers.add(HttpHeaders.CONTENT_TYPE, "application/xml; charset=UTF-8");
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .body(resource);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//    }
}
