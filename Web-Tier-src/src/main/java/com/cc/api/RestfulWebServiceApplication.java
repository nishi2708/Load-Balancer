package com.cc.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.util.logging.LogManager;

import com.cc.api.service.LoadBalancer;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
public class RestfulWebServiceApplication {
	static LoadBalancer loadBalancer = new LoadBalancer();
	public static void main(String[] args) {
		 LogManager.getLogManager().reset();
         Set<String> loggers = new HashSet<>(Arrays.asList("org.apache.http", "groovyx.net.http","com.amazonaws"));
           
         for(String log:loggers) {
            Logger logger = (Logger)LoggerFactory.getLogger(log);
            logger.setLevel(Level.WARN);
            logger.setAdditive(false);
         }
		Thread t = new Thread(loadBalancer);
		t.start();
		SpringApplication.run(RestfulWebServiceApplication.class, args);
		
	}
}
