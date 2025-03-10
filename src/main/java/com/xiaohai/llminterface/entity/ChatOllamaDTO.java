package com.xiaohai.llminterface.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * @Description:
 * @Author: XiaoYunTao
 * @Date: 2024/9/9
 */
@Getter
public class ChatOllamaDTO {

    @NotBlank(message = "message不能为空")
    String message;

    @NotNull(message = "userId不能为null")
    String userId;
}
