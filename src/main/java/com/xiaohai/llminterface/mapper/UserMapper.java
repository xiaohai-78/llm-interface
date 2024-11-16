package com.xiaohai.llminterface.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.xiaohai.llminterface.entity.Trace;

/**
 * @Description:
 * @Author: 肖云涛
 * @Date: 2024/11/16 下午4:59
 */
@Mapper
public interface UserMapper extends BaseMapper<Trace> {

}
