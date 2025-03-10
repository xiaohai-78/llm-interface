package com.xiaohai.llminterface.service.impl;

import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl {

    public void test() {
        System.out.println("test");
    }

    public void test2() {
        System.out.println("test2");
        test();
    }
}
