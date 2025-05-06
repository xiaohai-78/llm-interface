package com.xiaohai.llminterface.service;

import com.xiaohai.llminterface.entity.ChatOllamaDTO;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author xiaoyuntao
 * @date 2025/04/27
 */
public class QueueService {
    public static final LinkedBlockingQueue<ChatOllamaDTO> AD_GROUP_QUEUE = new LinkedBlockingQueue<>(2048);
}
