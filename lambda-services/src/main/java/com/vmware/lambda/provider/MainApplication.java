package com.vmware.lambda.provider;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.vmware.lambda.provider.api.model.AppOrm;
import com.vmware.lambda.provider.api.repository.AppRepository;


@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableAutoConfiguration
public class MainApplication implements CommandLineRunner {
    static String KUBERNETES_PROPERTIES_FILE_PATH = "/usr/lambda/kubernetes.properties";
    public static Logger logger = LoggerFactory.getLogger(MainApplication.class);

    @Autowired
    private AppRepository appRepository;

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        jpaTest();
        initializeProperties();
    }

    private void jpaTest() {
        AppOrm testAppOrm = new AppOrm();
        testAppOrm.setName("test");
        testAppOrm.setConfigs("test");
        appRepository.save(testAppOrm);
        appRepository.delete(testAppOrm);
    }

    private void initializeProperties() {
        logger.info("Initializing property manager with property values");
        PropertyManager.initializeResource("kubernetes.properties");
        PropertyManager.initializeResource("app.properties");

        Path path = Paths.get(KUBERNETES_PROPERTIES_FILE_PATH);
        if (Files.exists(path)){
            PropertyManager.initializeFile(KUBERNETES_PROPERTIES_FILE_PATH);
        }
    }
}
