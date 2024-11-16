package com.xiaohai.llminterface.enums;

import lombok.Getter;

/**
 * @Description: 系统执行动作枚举
 * @Author: XiaoYunTao
 * @Date: 2024/10/12
 */
@Getter
public enum SystemActionEnum {

    CUSTOMER_ADD("1", "新建客户"),
    WATERMARK("2", "水印"),
    EXECUTE_ACTION_3("3", "执行动作3"),
    ;

    private final String code;
    private final String desc;

    SystemActionEnum(String code, String desc)
    {
        this.code = code;
        this.desc = desc;
    }

}
