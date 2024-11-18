package com.xiaohai.llminterface.ali.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description:
 * @Author: XiaoYunTao
 * @Date: 2024/11/18
 */
@Data
public class ModelObservationDetailEntity {

    @TableId
    private Integer id;

    private String highCardinalityKeyValues;

    private String lowCardinalityKeyValues;

    private String operationMetadata;

    private String request;

    private String response;

    private String contextualName;

}
