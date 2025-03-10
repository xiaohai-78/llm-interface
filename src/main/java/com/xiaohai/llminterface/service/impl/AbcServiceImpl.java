package com.xiaohai.llminterface.service.impl;

import com.xiaohai.llminterface.service.AbcService;
import org.springframework.stereotype.Service;

/**
 * @author xiaoyuntao
 * @date 2025/03/07
 */
@Service
public class AbcServiceImpl implements AbcService {

    @Override
    public void test(){
        System.out.println("test");
    }
}
