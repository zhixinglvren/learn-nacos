package com.learn.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;

import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @author zhixinglvren
 * @date 2019/2/20 22:25
 */
public class NacosConfig {

    public static void main(String[] args) {
        try {
            String serverAddr = "127.0.0.1:8848";
            String dataId = "nacos-sdk-java-config";
            String group = "DEFAULT_GROUP";
            String content = "nacos-sdk-java-config:yes";
            Properties properties = new Properties();
            properties.put("serverAddr", serverAddr);

            // 方式一：创建ConfigService
            ConfigService configService = NacosFactory.createConfigService(serverAddr);

            // 方式二：创建ConfigService
            //ConfigService configService = ConfigFactory.createConfigService(serverAddr);

            /*// 方式三：创建ConfigService
            ConfigService configService = NacosFactory.createConfigService(properties);

            // 方式四：创建ConfigService
            ConfigService configService = ConfigFactory.createConfigService(properties);*/

            // 获取服务状态
            System.out.println("当前线程：" + Thread.currentThread().getName() + " ,服务状态：" + configService.getServerStatus());

            Listener configListener = new Listener() {
                public Executor getExecutor() {
                    return null;
                }

                public void receiveConfigInfo(String configInfo) {
                    System.out.println("当前线程：" + Thread.currentThread().getName() + " ,监听到配置内容变化：" + configInfo);
                }
            };

            // 添加监听（有时候能监听到，有时候监听不到，为什么？）
            System.out.println("添加监听");
            configService.addListener(dataId, group, configListener);
            System.out.println("添加监听成功");

            // 发布配置（多次运行，有时候会获取不到配置内容，难道要等待一段时间才能获取？）
            System.out.println("发布配置");
            configService.publishConfig(dataId, group, content);
            System.out.println("发布配置成功");
            try {
                Thread.sleep(1000 * 3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 本地缓存：\nacos\config\fixed-127.0.0.1_8848_nacos\snapshot\DEFAULT_GROUP
            // 读取配置
            String configAfterPublish = configService.getConfig(dataId, group, 3000);
            System.out.println("当前线程：" + Thread.currentThread().getName() + " ,发布配置后获取配置内容：" + configAfterPublish);

            // 重新发布配置
            content = "sdk-java-config:update";
            System.out.println("重新发布配置");
            boolean rePublishFlag = configService.publishConfig(dataId, group, content);
            if(rePublishFlag) {
                System.out.println("重新发布配置成功");
            } else {
                System.out.println("重新发布配置失败");
            }

            // 重新读取配置
            String configAfterUpdate = configService.getConfig(dataId, group, 3000);
            System.out.println("当前线程：" + Thread.currentThread().getName() + " ,重新发布配置后获取配置内容：" + configAfterUpdate);

            try {
                Thread.sleep(1000 * 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 移除配置
            System.out.println("移除配置");
            boolean removeFlag = configService.removeConfig(dataId, group);
            if(removeFlag) {
                System.out.println("移除配置成功");
            } else {
                System.out.println("移除配置失败");
            }

            String configAfterRemove = configService.getConfig(dataId, group, 3000);
            System.out.println("当前线程：" + Thread.currentThread().getName() + " ,移除配置后获取配置内容：" + configAfterRemove);

            // 取消监听
            System.out.println("取消监听");
            configService.removeListener(dataId, group, configListener);
            System.out.println("取消监听成功");
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }
}
