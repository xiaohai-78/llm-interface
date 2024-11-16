package com.xiaohai.llminterface.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.xiaohai.llminterface.enums.PlatFormEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
public class WaterMarkDTO {

//    /**
//     * 用户ID
//     * 注意：使用BeanUtil.convertBean2Map方法将DTO转查询map时，userId条件需要手动置入
//     */
//    private String userId;
//
//    /**
//     * 公司ID
//     */
//    private String corpid;
//
//    /**
//     * 平台 web：pc浏览器；dingtalk：钉钉；all：全部
//     */
//    private String platform = PlatFormEnum.ALL.getValue();

    /**
     *  全局水印状态,0表示关闭,1表示打开
     */
    @JsonProperty(required = true, value = "waterMarkState")
    @JsonPropertyDescription("全局水印状态,0表示关闭,1表示打开,全局水印状态打开时，才能显示下面的用户信息水印显示和公司名称水印显示；")
    private Integer waterMarkState;

    /**
     *  用户信息水印显示,0表示关闭,1表示打开
     */
    @JsonProperty(required = true, value = "userInfoWaterMarkDisplay")
    @JsonPropertyDescription("用户信息水印显示,0表示关闭,1表示打开；")
    private Integer userInfoWaterMarkDisplay;

    /**
     *  公司名称水印显示,0表示关闭,1表示打开
     */
    @JsonProperty(required = true, value = "companyNameWaterMarkDisplay")
    @JsonPropertyDescription("公司名称水印显示,0表示关闭,1表示打开")
    private Integer companyNameWaterMarkDisplay;

}
