package com.learn.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Properties;

/**
 * @author zhixinglvren
 * @date 2019/2/20 22:25
 */
public class NacosDiscovery {

    public static void main(String[] args) {
        try {
            String serverIp = "127.0.0.1";
            int serverPort = 8848;
            String serverAddr = serverIp + ":" + serverPort;

            String serviceName = "nacos-sdk-java-discovery";

            Properties properties = new Properties();
            properties.put("serverAddr", serverAddr);

            boolean healthy = true;

            Instance instance = new Instance();
            instance.setIp(serverIp);//IP
            instance.setPort(serverPort);//端口
            instance.setServiceName(serviceName);//服务名
            instance.setEnabled(true);//? true: 上线 false: 下线
            //instance.setInstanceId();
            instance.setHealthy(healthy);//健康状态
            instance.setWeight(1.0);//权重
            /*Map<String, String> instanceMeta = new HashMap<>();
            instanceMeta.put("nacos-sdk-java-discovery", "true");
            instance.setMetadata(instanceMeta);*/
            instance.addMetadata("nacos-sdk-java-discovery", "true");//元数据

            NamingService namingService = NacosFactory.createNamingService(serverAddr);
            // 怎么删除服务信息？

            //NamingService namingService = NamingFactory.createNamingService(properties);
            //
            //NamingService namingService = NacosFactory.createNamingService(serverAddr);
            //
            //NamingService namingService = NamingFactory.createNamingService(properties);

            EventListener discoveryListener = new EventListener() {
                public void onEvent(Event event) {
                    if (event instanceof NamingEvent) {
                        NamingEvent namingEvent = (NamingEvent) event;
                        System.out.println("当前线程：" + Thread.currentThread().getName() + " ,监听到实例名称：" + namingEvent.getServiceName());
                        System.out.println("当前线程：" + Thread.currentThread().getName() + " ,监听到实例内容：" + namingEvent.getInstances());
                    }
                }
            };

            // 获取服务状态
            System.out.println("当前线程：" + Thread.currentThread().getName() + " ,服务状态：" + namingService.getServerStatus());

            // 注册实例
            System.out.println("注册实例");
            namingService.registerInstance(serviceName, serverIp, serverPort);
            //namingService.registerInstance(serviceName, instance);
            System.out.println("注册实例成功");

            // 添加监听
            System.out.println("添加监听");
            namingService.subscribe(serviceName, discoveryListener);
            System.out.println("添加监听成功");

            // 获取所有实例
            // 为什么一个是getAllInstances，一个是selectInstances
            List<Instance> instances = namingService.getAllInstances(serviceName);
            System.out.println("当前线程：" + Thread.currentThread().getName() + " ,注册实例后获取所有实例：" + instances);

            // 获取所有健康实例
            List<Instance> healthyInstances = namingService.selectInstances(serviceName, healthy);
            System.out.println("当前线程：" + Thread.currentThread().getName() + " ,注册实例后获取所有健康实例：" + healthyInstances);

            // 获取一个健康实例（根据负载均衡算法随机获取）
            Instance healthyInstance = namingService.selectOneHealthyInstance(serviceName);
            System.out.println("当前线程：" + Thread.currentThread().getName() + " ,注册实例后获取一个健康实例：" + healthyInstance);

            try {
                Thread.sleep(1000 * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 取消监听
            System.out.println("取消监听");
            namingService.unsubscribe(serviceName, discoveryListener);
            System.out.println("取消监听成功");

            // 删除实例
            System.out.println("删除实例");
            namingService.deregisterInstance(serviceName, serverIp, serverPort);
            System.out.println("删除实例成功");

            // 需要等待，才能观察到实例删除后的状态
            try {
                Thread.sleep(1000 * 3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 获取所有实例
            List<Instance> instancesAfterDeregister = namingService.getAllInstances(serviceName);
            System.out.println("当前线程：" + Thread.currentThread().getName() + " ,删除实例后获取所有实例：" + instancesAfterDeregister);

            // 获取所有健康实例
            List<Instance> healthyInstancesAfterDeregister = namingService.selectInstances(serviceName, healthy);
            System.out.println("当前线程：" + Thread.currentThread().getName() + " ,删除实例后获取所有健康实例：" + healthyInstancesAfterDeregister);

            // 获取一个健康实例（根据负载均衡算法随机获取）
            Instance healthyInstanceAfterDeregister = namingService.selectOneHealthyInstance(serviceName);
            System.out.println("当前线程：" + Thread.currentThread().getName() + " ,删除实例后获取一个健康实例：" + healthyInstanceAfterDeregister);

        } catch (NacosException e) {
            e.printStackTrace();
        }
    }
}
