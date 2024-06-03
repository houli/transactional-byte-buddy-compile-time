package io.github.houli.plugin;

import io.github.houli.annotation.Propagation;
import io.github.houli.annotation.Transactional;
import io.github.houli.plugin.advice.ForceNewTransactionTransactionalAdvice;
import io.github.houli.plugin.advice.NewOrExistingTransactionTransactionalAdvice;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.matcher.ElementMatcher;

import java.io.IOException;
import java.util.Objects;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class TransactionalByteBuddyPlugin extends Plugin.ForElementMatcher {
    private static final ElementMatcher.Junction<AnnotationSource> ANNOTATION_MATCHER = isAnnotatedWith(Transactional.class);
    private static final ElementMatcher.Junction<MethodDescription> ANNOTATED_METHOD_MATCHER = isMethod().and(ANNOTATION_MATCHER);

    public TransactionalByteBuddyPlugin() {
        // We want to run our plugin for all types annotated with @Transactional or have methods annotated with @Transactional
        super(ANNOTATION_MATCHER.or(declaresMethod(ANNOTATED_METHOD_MATCHER)));
    }

    @Override
    public Builder<?> apply(Builder<?> builder,
                            TypeDescription typeDescription,
                            ClassFileLocator classFileLocator) {
        if (typeDescription.getDeclaredAnnotations().isAnnotationPresent(Transactional.class)) {
            System.out.println("Weird class");
            return applyForClass(builder, typeDescription);
        } else {
            System.out.println("Weird method");
            return applyForMethods(builder, typeDescription);
        }
    }

    private Builder<?> applyForClass(Builder<?> builder, TypeDescription typeDescription) {
        Transactional transactional = Objects
                .requireNonNull(typeDescription.getDeclaredAnnotations().ofType(Transactional.class))
                .load();

        if (transactional.propagation() == Propagation.REQUIRES_NEW) {
            return builder.visit(Advice.to(ForceNewTransactionTransactionalAdvice.class).on(isMethod()));
        } else {
            return builder.visit(Advice.to(NewOrExistingTransactionTransactionalAdvice.class).on(isMethod()));
        }
    }

    private Builder<?> applyForMethods(Builder<?> builder, TypeDescription typeDescription) {
        // Apply the advice to all methods annotated with @Transactional
        for (var method : typeDescription.getDeclaredMethods()) {
            if (method.getDeclaredAnnotations().isAnnotationPresent(Transactional.class)) {
                Transactional transactional = Objects
                        .requireNonNull(method.getDeclaredAnnotations().ofType(Transactional.class))
                        .load();

                if (transactional.propagation() == Propagation.REQUIRES_NEW) {
                    builder = builder.visit(Advice.to(ForceNewTransactionTransactionalAdvice.class).on(is(method)));
                } else {
                    builder = builder.visit(Advice.to(NewOrExistingTransactionTransactionalAdvice.class).on(is(method)));
                }
            }
        }
        return builder;
    }

    @Override
    public void close() throws IOException {
    }
}
