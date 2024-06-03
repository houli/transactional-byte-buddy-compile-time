# Byte Buddy Transactional Annotation Test
I wanted to learn how to use Byte Buddy compile-time plugins to create an annotation similar to Spring's `@Transactional`
annotation that would automatically start and commit a transaction for a method. I went with a bytecode rewriting
approach since it avoids issues like requiring interfaces with dynamic proxies or generating subclasses. The actual
implementation of the `TransactionManager` mechanics probably isn't all that robust but the main thing I wanted to
figure out was how to match annotated classes and methods and then inject the transactional logic into them.

The output of the decompiled bytecode seems promising at least.

Original class:
```java
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
```

Transformed class:
```java
package io.github.houli.service;

import io.github.houli.TransactionManager;
import io.github.houli.annotation.Propagation;
import io.github.houli.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotatedMethodTestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatedMethodTestService.class);

    public AnnotatedMethodTestService() {
    }

    @Transactional(
            propagation = Propagation.REQUIRED
    )
    public List<String> testMethod1() {
        TransactionManager var1 = null;
        var1 = TransactionManager.getInstance();
        var1.beginNewOrExistingTransaction(false);
        AnnotatedMethodTestService var2 = this;

        Throwable var3;
        List var6;
        label23: {
            List var10000;
            try {
                LOGGER.info("testMethod1 called");
                var10000 = var2.testMethod2("123");
            } catch (Throwable var5) {
                var3 = var5;
                var6 = null;
                break label23;
            }

            var6 = var10000;
            var3 = null;
        }

        if (var3 != null) {
            var1.rollbackTransaction();
            throw var3;
        } else {
            var1.commitExistingTransaction();
            if (var3 != null) {
                throw var3;
            } else {
                return var6;
            }
        }
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    public List<String> testMethod2(String var1) {
        TransactionManager var2 = null;
        var2 = TransactionManager.getInstance();
        var2.beginNewOrExistingTransaction(true);
        AnnotatedMethodTestService var3 = this;
        String value = var1;

        List var8;
        Throwable var9;
        label23: {
            List var10000;
            try {
                LOGGER.info("testMethod2 called");
                var10000 = var3.testMethod3(value);
            } catch (Throwable var7) {
                var9 = var7;
                var8 = null;
                break label23;
            }

            var8 = var10000;
            var9 = null;
        }

        if (var9 != null) {
            var2.rollbackTransaction();
            throw var9;
        } else {
            var2.commitExistingTransaction();
            if (var9 != null) {
                throw var9;
            } else {
                return var8;
            }
        }
    }

    public List<String> testMethod3(String value) {
        LOGGER.info("testMethod3 called");
        return Collections.nCopies(5, value);
    }
}
```
