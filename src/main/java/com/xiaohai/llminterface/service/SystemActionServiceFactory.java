package com.xiaohai.llminterface.service;

import com.xiaohai.llminterface.enums.SystemActionEnum;
import com.xiaohai.llminterface.service.actionImpl.AddDataActionService;
import com.xiaohai.llminterface.service.actionImpl.NaturalDialogAcionService;
import com.xiaohai.llminterface.service.actionImpl.NewCustomerActionService;
import com.xiaohai.llminterface.service.actionImpl.WatermarkActionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: XiaoYunTao
 * @Date: 2024/10/14
 */
@Slf4j
@Service
public class SystemActionServiceFactory {

    @Resource
    private AnalysisIntentionService analysisIntentionService;

    @Resource
    private WatermarkActionService watermarkActionService;

    @Resource
    private NewCustomerActionService newCustomerActionService;

    @Resource
    private NaturalDialogAcionService naturalDialogAcionService;

    @Resource
    private AddDataActionService addDataActionService;

    public SystemActionAbstractService getSystemActionService(String message) {

        SystemActionEnum systemActionEnum = analysisIntentionService.getSystemActionEnum(message);

        log.info("SystemActionEnum: {}", systemActionEnum.getActive());

        return switch (systemActionEnum) {
            case WATERMARK -> watermarkActionService;
//            case CUSTOMER_ADD -> newCustomerActionService;
            case NATURAL_DIALOG -> naturalDialogAcionService;
            case CUSTOMER_ADD -> addDataActionService;
            default -> naturalDialogAcionService;
        };

    }
}
