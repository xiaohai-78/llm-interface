package com.xiaohai.llminterface.function;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.xiaohai.llminterface.enums.SystemActionEnum;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * @Description: 意图识别
 * @Author: XiaoYunTao
 * @Date: 2024/10/17
 */
@JsonClassDescription("识别用户的意图，从SystemActionEnum中选择最符合的枚举")
@Service
public class IdentifyIntentService implements Function<SystemActionEnum, SystemActionEnum> {

    @Override
    public SystemActionEnum apply(SystemActionEnum systemActionEnum) {
        return systemActionEnum;
    }
}
