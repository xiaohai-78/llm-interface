package com.xiaohai.llminterface.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @Author: 肖云涛
 * @Date: 2024/11/16 下午4:59
 */
@TableName(value ="trace_detail")
@Data
@AllArgsConstructor
public class Trace implements Serializable {
    /**
     *
     */
    private Integer id;

    /**
     *
     */
    private String name;

    /**
     *
     */
    private Integer time;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
