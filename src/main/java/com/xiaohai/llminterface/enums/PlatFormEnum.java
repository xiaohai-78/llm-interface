package com.xbongbong.paas.enums;

import com.xbongbong.i18n.util.I18nMessageUtil;

import java.util.Objects;

/**
 * 平台表示枚举
 * @author kaka
 * @time 2018-10-15 20:09
 */
public enum PlatFormEnum {

    /**
     * pc浏览器
     */
    WEB("web", "enum.platForm.memo.web"),

    /**
     * 钉钉内部访问
     */
    DINGTALK("dingtalk", "enum.platForm.memo.dingtalk"),
    
    /**
     * 小程序
     */
    MINI_APP("miniApp","enum.platForm.memo.miniApp"),

    /**
     * 检查一下这个类型标示什么时候有用到 TODO
     */
    ALL("all", "enum.platForm.memo.all"),

    /**
     * 内部访问
     */
    INTERNAL("internal", "enum.platForm.memo.internal"),
    ;

    /**
     * 平台标示值
     */
    private String value;

    /**
     * 平台标示中文描述
     */
    private String memo;

    PlatFormEnum(String value, String memo) {
        this.value = value;
        this.memo = memo;
    }

    public static PlatFormEnum getByValue(String value) {
        for (PlatFormEnum platFormEnum: values()) {
            if (Objects.equals(platFormEnum.getValue(), value)) {
                return platFormEnum;
            }
        }
        return null;
    }

    /**
     * 获取平台标示值
     * @return 平台标示值
     */
    public String getValue() {
        return value;
    }

    /**
     * 获取平台标示中文描述
     * @return 平台标示中文描述
     */
    public String getMemo() {
        return I18nMessageUtil.getMessage(memo);
    }
}
