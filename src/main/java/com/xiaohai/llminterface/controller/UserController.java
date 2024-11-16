package com.xiaohai.llminterface.controller;

import com.xiaohai.llminterface.entity.Trace;
import com.xiaohai.llminterface.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: 肖云涛
 * @Date: 2024/11/16 下午5:01
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/getAll")
    public List<Trace> getAll() {
        return userService.list();
    }

    @GetMapping("/getById")
    public Trace getById(Integer id) {
        return userService.getById(id);
    }

    @GetMapping("/add")
    public boolean add() {
        List<Trace> traces = new ArrayList<>();
        for (int i = 0; i < 9999; i++) {
            traces.add(new Trace(i, "name" + i, i));
        }
        return userService.saveBatch(traces, traces.size());
    }

    @PostMapping("/add")
    public boolean add(@RequestBody Trace trace) {
        try {
            userService.save(trace);
        }catch (Exception e) {
            return false;
        }
        return true;
    }
}
