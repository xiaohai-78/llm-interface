package com.xiaohai.llminterface.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: XiaoYunTao
 * @Date: 2024/10/18
 */
@Data
@NoArgsConstructor
public class NewCustomerPojo {

    @JsonProperty(required = true, value = "name")
    @JsonPropertyDescription("名字，姓名")
    private String name;

    @JsonProperty(required = true, value = "age")
    @JsonPropertyDescription("年龄")
    private String age;

    @JsonProperty(required = true, value = "sex")
    @JsonPropertyDescription("性别")
    private String sex;
}
