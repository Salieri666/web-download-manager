package com.filedownloader.corelib.utils;

import jakarta.persistence.LockTimeoutException;
import lombok.experimental.UtilityClass;
import org.springframework.dao.PessimisticLockingFailureException;

import java.time.Duration;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

@UtilityClass
public class RetryUtils {

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration INITIAL_DELAY = Duration.ofMillis(200);

    public <T> T executeWithRetry(
            Supplier<T> operation,
            IntConsumer onRetry
    ) {
        Duration delay = INITIAL_DELAY;
        int attempt = 1;
        while (true) {
            try {
                return operation.get();
            } catch (RuntimeException ex) {
                if (!isRetryableLockException(ex) || attempt >= MAX_ATTEMPTS) {
                    throw ex;
                }

                if (onRetry != null) {
                    onRetry.accept(attempt);
                }

                sleep(delay);
                delay = delay.multipliedBy(2);
                attempt++;
            }
        }
    }

    public void executeWithRetry(
            Runnable operation,
            IntConsumer onRetry
    ) {
        executeWithRetry(() -> {
            operation.run();
            return null;
        }, onRetry);
    }

    private void sleep(Duration delay) {
        try {
            Thread.sleep(delay.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting to retry operation", e);
        }
    }

    private boolean isRetryableLockException(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof PessimisticLockingFailureException
                    || current instanceof LockTimeoutException
                    || isHibernateLockAcquisitionException(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private boolean isHibernateLockAcquisitionException(Throwable throwable) {
        return "org.hibernate.exception.LockAcquisitionException".equals(throwable.getClass().getName());
    }
}
