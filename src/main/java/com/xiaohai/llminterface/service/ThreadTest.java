package com.xiaohai.llminterface.service;

class MyThread implements Runnable {
    private static int count;

    public MyThread() {
        count = 0;
    }

    public void method() {
        for (int i = 0; i < 5; i ++) {
            try {
                System.out.println(Thread.currentThread().getName() + ":" + (count++));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        method();
    }


    public static void main(String[] args) {
        MyThread mThread1 = new MyThread();
        MyThread mThread2 = new MyThread();
        Thread thread1 = new Thread(mThread1, "Thread1");
        Thread thread2 = new Thread(mThread2, "Thread2");
        thread1.start();
        thread2.start();
    }
}


