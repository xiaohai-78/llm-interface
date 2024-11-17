package com.xiaohai.llminterface.entity;

import java.util.Map;

/**
 * @Description:
 * @Author: 肖云涛
 * @Date: 2024/11/17
 */
public class NewContext {
    private Map<String, String> allKeyValues;
    private String contextualName;
    private Map<String, String> highCardinalityKeyValues;
    private Map<String, String> lowCardinalityKeyValues;
    private String name;
    private Object request;
    private Object requestOptions;
    private Object response;

    // Getters and setters...
    public Map<String, String> getAllKeyValues() {
        return allKeyValues;
    }

    public void setAllKeyValues(Map<String, String> allKeyValues) {
        this.allKeyValues = allKeyValues;
    }

    public String getContextualName() {
        return contextualName;
    }

    public void setContextualName(String contextualName) {
        this.contextualName = contextualName;
    }

    public Map<String, String> getHighCardinalityKeyValues() {
        return highCardinalityKeyValues;
    }

    public void setHighCardinalityKeyValues(Map<String, String> highCardinalityKeyValues) {
        this.highCardinalityKeyValues = highCardinalityKeyValues;
    }

    public Map<String, String> getLowCardinalityKeyValues() {
        return lowCardinalityKeyValues;
    }

    public void setLowCardinalityKeyValues(Map<String, String> lowCardinalityKeyValues) {
        this.lowCardinalityKeyValues = lowCardinalityKeyValues;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = request;
    }

    public Object getRequestOptions() {
        return requestOptions;
    }

    public void setRequestOptions(Object requestOptions) {
        this.requestOptions = requestOptions;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }
}
