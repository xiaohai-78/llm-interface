package com.xiaohai.llminterface.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.xiaohai.llminterface.enums.SystemActionEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

/**
 * @Description:
 * @Author: XiaoYunTao
 * @Date: 2024/10/18
 */
@Data
@NoArgsConstructor
public class XbbActionPojo {

    @JsonProperty(required = true, value = "message")
    @JsonPropertyDescription("用户当前发送的消息")
    private String message;

    @JsonProperty(required = true, value = "message")
    @JsonPropertyDescription("选择用户当前想执行的动作Action")
    private SystemActionEnum systemActionEnum;

    @JsonProperty(required = true, value = "message")
    @JsonPropertyDescription("用户的历史信息")
    private List<Message> messages;
}
