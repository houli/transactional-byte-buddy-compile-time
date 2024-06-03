package io.github.houli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionManager {
    private final static class Transaction {
        private final static Logger LOGGER = LoggerFactory.getLogger(Transaction.class);
        private int transactionLevel = 0;
        private Transaction parent;

        public void begin() {
            transactionLevel++;
            LOGGER.info("Transaction begin");
        }

        public void commit() {
            if (transactionLevel == 0) {
                throw new IllegalStateException("Transaction is not started");
            }

            transactionLevel--;
            LOGGER.info("Transaction commit");

            if (transactionLevel == 0) {
                LOGGER.info("Transaction actually committed");
            }
        }

        public void rollback() {
            if (transactionLevel == 0) {
                throw new IllegalStateException("Transaction is not started");
            }

            transactionLevel = 0;
            LOGGER.info("Transaction rollback");
        }

        public int getTransactionLevel() {
            return transactionLevel;
        }

        public Transaction getParent() {
            return parent;
        }

        public void setParent(Transaction parent) {
            this.parent = parent;
        }
    }

    private static final ThreadLocal<TransactionManager> INSTANCE = ThreadLocal.withInitial(TransactionManager::new);
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManager.class);
    private Transaction current;

    private TransactionManager() {
    }

    public static TransactionManager getInstance() {
        return INSTANCE.get();
    }

    public void beginNewOrExistingTransaction(boolean forceNew) {
        if (current == null || forceNew) {
            LOGGER.info("Using new transaction");
            Transaction newTransaction = new Transaction();

            if (current != null) {
                newTransaction.setParent(current);
            }

            current = newTransaction;
        } else {
            LOGGER.info("Using existing transaction");
        }
        current.begin();
    }

    public void commitExistingTransaction() {
        if (current == null) {
            throw new IllegalStateException("No existing transaction to commit");
        }

        current.commit();

        // Current transaction finished
        if (current.getTransactionLevel() == 0) {
            current = current.getParent();
        }
    }

    public void rollbackTransaction() {
        if (current == null) {
            throw new IllegalStateException("Transaction is not started");
        }

        while (current != null) {
            current.rollback();
            current = current.getParent();
        }
    }
}
