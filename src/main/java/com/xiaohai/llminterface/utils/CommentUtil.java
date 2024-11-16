package com.xiaohai.newsassistant.utils;

import java.util.Map;

public class CommentUtil {
    /**
     * 拼接形成url，带?（即以“?”符号开始）
     * @param mapStr
     * @return java.lang.String
     * @date 历史
     * @since v1.0
     * @version v1.0
     */
    public static String mapToUrl(Map<String, Object> mapStr) {
        String url = "?1";
        String paramStr = mapAppendToUrl(mapStr);
        return url + paramStr;
    }
    /**
     * 把mapStr内的key、value拼到url上，不带?（即以“&”符号开始）
     * @param mapStr url后面的参数map
     * @return java.lang.String
     * @since v1.0
     * @version v1.0
     */
    public static String mapAppendToUrl(Map<String, Object> mapStr) {
        StringBuilder paramStr = new StringBuilder();
        if (mapStr == null) {
            return paramStr.toString();
        }
        for (Map.Entry<String, Object> entry : mapStr.entrySet()) {
            Object value = entry.getValue();
            if (value != null && !"".equals(value)) {
                paramStr.append("&").append(entry.getKey()).append("=").append(value);
            }
        }
        return paramStr.toString();
    }
}
