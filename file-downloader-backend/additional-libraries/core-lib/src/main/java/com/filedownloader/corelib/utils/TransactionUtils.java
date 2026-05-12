package com.filedownloader.corelib.utils;

import lombok.experimental.UtilityClass;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@UtilityClass
public class TransactionUtils {

    public <T> T execute(PlatformTransactionManager transactionManager, Supplier<T> callback) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        return template.execute(status -> callback.get());
    }

    public void executeWithoutResult(PlatformTransactionManager transactionManager, Runnable runnable) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.executeWithoutResult(status -> runnable.run());
    }
}
