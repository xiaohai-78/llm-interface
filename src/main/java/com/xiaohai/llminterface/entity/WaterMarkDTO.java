package com.xbongbong.paas.pojo.dto;

import com.xbongbong.paas.toolbox.wrap.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 水印标识
 * @Author: 肖云涛
 * @Date: 2022/8/29 15:41
 */

@Setter
@Getter
@ToString
public class WaterMarkDTO extends BaseDTO {

    /**
     *  水印状态,0表示关闭,1表示打开
     */
    private Integer waterMarkState;

    /**
     *  用户信息水印显示,0表示关闭,1表示打开
     */
    private Integer userInfoWaterMarkDisplay;

    /**
     *  公司名称水印显示,0表示关闭,1表示打开
     */
    private Integer companyNameWaterMarkDisplay;

}
