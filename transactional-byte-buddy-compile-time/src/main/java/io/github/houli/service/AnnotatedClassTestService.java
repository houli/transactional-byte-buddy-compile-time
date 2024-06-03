package io.github.houli.service;

import io.github.houli.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import static io.github.houli.annotation.Propagation.REQUIRED;

@Transactional(propagation = REQUIRED)
public class AnnotatedClassTestService {
    private final static Logger LOGGER = LoggerFactory.getLogger(AnnotatedClassTestService.class);

    public List<String> testMethod1() {
        LOGGER.info("testMethod1 called");
        return testMethod2("123");
    }

    public List<String> testMethod2(String value) {
        LOGGER.info("testMethod2 called");
        return testMethod3(value);
    }

    public List<String> testMethod3(String value) {
        LOGGER.info("testMethod3 called");
        return Collections.nCopies(5, value);
    }
}
