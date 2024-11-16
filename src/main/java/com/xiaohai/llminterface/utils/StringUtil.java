package com.xiaohai.newsassistant.utils;

/**
 * @Description: String工具类
 * @Author: XiaoYunTao
 * @Date: 2024/9/4
 */
public class StringUtil {
    public static boolean isEmpty(String value) {
        return isEmpty(value, true);
    }

    public static boolean isEmpty(String value, boolean trim) {
        return isEmpty(value, trim, ' ');
    }

    public static boolean isEmpty(String value, boolean trim, char... trimChars) {
        if (trim) {
            return value == null || trim(value, trimChars).length() <= 0;
        }
        return value == null || value.length() <= 0;
    }

    public static String trim(String value, char... chars) {
        return trim(3, value, chars);
    }

    private static String trim(int mode, String value, char... chars) {
        if (value == null || value.length() <= 0) {
            return value;
        }
        value = value.trim();
        int startIndex = 0, endIndex = value.length(), index = 0;
        if (mode == 1 || mode == 3) {
            // trim头部
            while (index < endIndex) {
                if (contains(chars, value.charAt(index++))) {
                    startIndex++;
                    continue;
                }
                break;
            }
        }

        if (startIndex >= endIndex) {
            return "";
        }

        if (mode == 2 || mode == 3) {
            // trim尾部
            index = endIndex - 1;
            while (index >= 0) {
                if (contains(chars, value.charAt(index--))) {
                    endIndex--;
                    continue;
                }
                break;
            }
        }

        if (startIndex >= endIndex) {
            return "";
        }

        return value.substring(startIndex, endIndex);
    }

    private static boolean contains(char[] chars, char chr) {
        if (chars == null || chars.length <= 0) {
            return false;
        }
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == chr) {
                return true;
            }
        }
        return false;
    }
}
