package io.github.houli.plugin.advice;

import io.github.houli.TransactionManager;
import net.bytebuddy.asm.Advice;

public final class ForceNewTransactionTransactionalAdvice {
    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Local("transactionManager") TransactionManager transactionManager) {
        transactionManager = TransactionManager.getInstance();
        transactionManager.beginNewOrExistingTransaction(true);
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void onExit(
            @Advice.Local("transactionManager") TransactionManager transactionManager,
            @Advice.Thrown Throwable throwable
    ) throws Throwable {
        if (throwable != null) {
            transactionManager.rollbackTransaction();
            throw throwable;
        } else {
            transactionManager.commitExistingTransaction();
        }
    }
}
