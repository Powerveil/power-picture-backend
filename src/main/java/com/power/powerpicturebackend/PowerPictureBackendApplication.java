package com.power.powerpicturebackend;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@MapperScan("com.power.powerpicturebackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
@Slf4j
public class PowerPictureBackendApplication {

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext context = SpringApplication.run(PowerPictureBackendApplication.class, args);

        // 获取Environment实例
        Environment env = context.getEnvironment();

        // 获取端口号和上下文路径
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path", "");

        // 打印启动信息
        log.info("\n\n===========> 系统启动成功！后台文档地址：http://localhost:{}{}/doc.html", serverPort, contextPath);
    }

}
