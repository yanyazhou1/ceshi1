package com.restkeeper.shop;

import com.google.common.collect.Maps;

import java.util.Map;

public class ThreadLocalDemo {

    static ThreadLocal<Map<String,Object>> mapThreadLocal = new ThreadLocal<Map<String,Object>>(){
        @Override
        protected Map<String, Object> initialValue() {
            return Maps.newHashMap();
        }
    };
    public static void main(String[] args) {

        new Thread(new Runnable() {
            @Override
            public void run() {
               mapThreadLocal.get().put("key","1");
               shuchu();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mapThreadLocal.get().put("key","2");
                shuchu();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mapThreadLocal.get().put("key","3");
                shuchu();
            }
        }).start();
    }

    //输出线程的信息
    private static void shuchu() {
        System.out.println(Thread.currentThread().getName()+" : "+mapThreadLocal.get());
    }
}
