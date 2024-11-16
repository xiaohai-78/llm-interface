package com.xbongbong.paas.pojo.vo;
/**
 * @author xiaoyt
 * @date 2022/8/28 20:35
 * @version v1.0
 * @since v1.0
 */

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 水印标识
 * @Author: 肖云涛
 * @Date: 2022/8/28 20:35
 */

@Getter
@Setter
@ToString
public class WaterMarkVO {

    private String corpName;

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
