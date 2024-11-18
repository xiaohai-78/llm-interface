package com.xiaohai.llminterface.ali.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @Author: XiaoYunTao
 * @Date: 2024/11/18
 */

@Data
@TableName("tb_model_observation")
public class ModelObservationEntity{

    @TableId
    private Integer id;

    private String name;

    private String addTime;

    private Integer parentId;

    private Integer totalTokens;

    private String model;

    private String error;

}
