package io.github.houli;

import io.github.houli.service.AnnotatedClassTestService;
import io.github.houli.service.AnnotatedMethodTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private final static Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        var service1 = new AnnotatedMethodTestService();
        var service2 = new AnnotatedClassTestService();

        LOGGER.info("Calling annotated methods");

        service1.testMethod1();
        service2.testMethod1();

        LOGGER.info("Done");
    }
}
