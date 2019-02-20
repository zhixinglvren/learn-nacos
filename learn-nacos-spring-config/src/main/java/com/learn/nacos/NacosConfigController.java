package com.learn.nacos;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "config")
public class NacosConfigController {

    @NacosInjected
    private ConfigService configService;

    /**
     * 通过Nacos Open API 向 Nacos Server 发布配置
     * curl -X POST "http://127.0.0.1:8848/nacos/v1/cs/configs?dataId=XXX&group=XXX&content=XXX"
     */
    @NacosValue(value = "${useLocalCache:false}", autoRefreshed = true)
    private boolean useLocalCache;

    /**
     * 浏览器访问：http://127.0.0.1:8080/config/get
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public boolean get() {
        return useLocalCache;
    }

    /**
     * 发布配置
     *
     * 浏览器访问：http://127.0.0.1:8080/config?dataId=nacos.spring.config&content=useLocalCache=true
     *
     * @param dataId  配置集的ID
     * @param group   配置集
     * @param content 配置内容
     * @return
     * @throws NacosException
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> publish(@RequestParam String dataId,
                                          @RequestParam(defaultValue = "DEFAULT_GROUP") String group,
                                          @RequestParam String content) throws NacosException {
        boolean result = configService.publishConfig(dataId, group, content);
        if (result) {
            return new ResponseEntity<String>("Success", HttpStatus.OK);
        }
        return new ResponseEntity<String>("Fail", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}