package com.xiaohai.llminterface.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author xiaoyuntao
 * @date 2025/03/18
 */
public class ForkJoinPoolTestService {

    public static final ForkJoinPool TEST_POOL = new ForkJoinPool(10);
    public static void main(String[] args) {
        ForkJoinPoolTestService service = new ForkJoinPoolTestService();

        // 生成测试数据（1-20）
        List<Integer> testData = IntStream.rangeClosed(1, 20)
                .boxed()
                .collect(Collectors.toList());

        // 同步执行
        List<String> syncResult = service.processWithCustomPool(testData);
        syncResult.forEach(System.out::println); // 输出处理结果

        // 异步执行示例
        service.processAsync(testData)
                .thenAccept(result -> result.forEach(System.out::println))
                .join(); // 等待异步完成

        // 关闭线程池（生产环境需确保调用）
        service.shutdown();
    }

    /**
     * 使用自定义线程池并行处理数据
     * @param data 输入数据列表
     * @return 处理后的结果列表
     */
    public List<String> processWithCustomPool(List<Integer> data) {
        try {
            return TEST_POOL.submit(() ->
                    data.parallelStream()
                            .map(this::processItem) // 处理每个元素
                            .collect(Collectors.toList())
            ).get(); // 阻塞等待结果
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("处理任务失败", e);
        }
    }

    /**
     * 模拟单个元素的处理逻辑（如计算平方并转为字符串）
     */
    private String processItem(Integer item) {
        // 模拟耗时操作（如 I/O 或复杂计算）
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("处理中断", e);
        }
        return "Processed_" + item * item;
    }

    /**
     * 异步处理数据（非阻塞）
     * @param data 输入数据列表
     * @return CompletableFuture 异步结果
     */
    public CompletableFuture<List<String>> processAsync(List<Integer> data) {
        return CompletableFuture.supplyAsync(
                () -> data.parallelStream()
                        .map(this::processItem)
                        .collect(Collectors.toList()),
                TEST_POOL
        );
    }

    /**
     * 关闭线程池（建议在应用关闭时调用）
     */
    public void shutdown() {
        TEST_POOL.shutdown();
        try {
            if (!TEST_POOL.awaitTermination(60, TimeUnit.SECONDS)) {
                TEST_POOL.shutdownNow();
            }
        } catch (InterruptedException e) {
            TEST_POOL.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
