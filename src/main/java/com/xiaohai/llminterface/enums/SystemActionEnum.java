package com.xiaohai.llminterface.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 系统执行动作枚举
 * @Author: XiaoYunTao
 * @Date: 2024/10/12
 */
@Getter
public enum SystemActionEnum {

    /**
     * 新建客户
     */
    CUSTOMER_ADD("CUSTOMER_ADD", "新建客户"),

    /**
     * 水印
     */
    WATERMARK("WATERMARK", "水印，对系统内水印开关的相关操作"),

    /**
     * 自然对话
     */
    NATURAL_DIALOG("NATURAL_DIALOG", "自然对话"),

//    /**
//     * 新建数据
//     */
//    ADD_DATA("ADD_DATA", "新建新建数据，如：新建客户、新建合同、新建联系人等新建各种表单数据。"),
    ;

    private final String active;

    private final String desc;

    SystemActionEnum(String active, String desc)
    {
        this.active = active;
        this.desc = desc;
    }

    public static SystemActionEnum getByActive(String active) {
        for(SystemActionEnum systemActionEnum : SystemActionEnum.values()){
            if(systemActionEnum.getActive().equals(active)){
                return systemActionEnum;
            }
        }
        return NATURAL_DIALOG;
    }

    public static List<String> getAllActive() {
        return Arrays.stream(SystemActionEnum.values())
                .map(SystemActionEnum::getDesc)
                .collect(Collectors.toList());
    }

    public static Map<String, String> getAll() {
        return Arrays.stream(SystemActionEnum.values())
                .collect(Collectors.toMap(SystemActionEnum::getActive, SystemActionEnum::getDesc));
    }

    public static void main(String[] args) {
        System.out.println(getAll());
    }
}
