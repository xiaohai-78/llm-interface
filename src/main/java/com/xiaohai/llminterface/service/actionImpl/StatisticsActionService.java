package com.xiaohai.llminterface.service.actionImpl;

import com.alibaba.fastjson.JSONObject;
import com.xiaohai.llminterface.service.SystemActionAbstractService;
import com.xiaohai.llminterface.utils.OkHttpUtil;
import org.springframework.ai.chat.model.ChatModel;

/**
 * @Description: 统计
 * @Author: XiaoYunTao
 * @Date: 2024/10/15
 */
public class StatisticsActionService extends SystemActionAbstractService {

    public StatisticsActionService(ChatModel ollamaChatModel) {
        super(ollamaChatModel);
    }

    @Override
    public JSONObject execute(String message) {
        return null;
    }

    @Override
    protected String getParamPrompt(String... strings) {
        return null;
    }

    private String getStatisticsStatus() {
        String contactUrl = "http://192.168.20.145:5555" + "/pro/v1/chart/dataSourceAttr?lang=zh_CN";
        String jsonData = "{\"corpid\":\"ding464c4bae11043b92f5bf40eda33b7ba0\",\"userId\":\"1\",\"platform\":\"web\",\"frontDev\":\"1\",\"chartType\":1,\"single\":\"1\",\"selectSubFormMap\":{},\"driverSources\":{\"appId\":\"4076\",\"businessType\":100,\"distributorMark\":0,\"formId\":13410,\"menuId\":15116,\"name\":\"个人客户\",\"saasMark\":1,\"id\":13410,\"allowSelect\":true,\"selected\":true,\"disabled\":false,\"label\":\"CRM--个人客户\",\"labelName\":\"CRM--个人客户\"},\"slaveSources\":[]}";
        String s = OkHttpUtil.post("https://proapi.xbongbong.com/pro/v2/api/paas/add", jsonData, OkHttpUtil.APPLICATION_JSON_UTF8_VALUE, null);
        System.out.println(s);
        return s;
    }
}
