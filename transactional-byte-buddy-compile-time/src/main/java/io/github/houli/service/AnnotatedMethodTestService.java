package io.github.houli.service;

import io.github.houli.annotation.Propagation;
import io.github.houli.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class AnnotatedMethodTestService {
    private final static Logger LOGGER = LoggerFactory.getLogger(AnnotatedMethodTestService.class);

    @Transactional(propagation = Propagation.REQUIRED)
    public List<String> testMethod1() {
        LOGGER.info("testMethod1 called");
        return testMethod2("123");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<String> testMethod2(String value) {
        LOGGER.info("testMethod2 called");
        return testMethod3(value);
    }

    public List<String> testMethod3(String value) {
        LOGGER.info("testMethod3 called");
        return Collections.nCopies(5, value);
    }
}
