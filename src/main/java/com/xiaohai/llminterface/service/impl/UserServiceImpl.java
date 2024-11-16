package com.xiaohai.llminterface.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaohai.llminterface.entity.Trace;
import com.xiaohai.llminterface.mapper.UserMapper;
import com.xiaohai.llminterface.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: 肖云涛
 * @Date: 2024/11/16 下午5:01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, Trace>
    implements UserService {

}
