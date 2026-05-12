package com.filedownloader.downloaderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncExecutorConfiguration {

    @Bean(name = "fileDescriptionTaskExecutor")
    public TaskExecutor fileDescriptionTaskExecutor() {
        return createExecutor("app-", 4, 8, 500);
    }

    @Bean(name = "chunkDownloadingTaskExecutor")
    public TaskExecutor chunkDownloadingTaskExecutor() {
        return createExecutor("chunk-", 8, 16, 200);
    }

    private ThreadPoolTaskExecutor createExecutor(String prefix, int corePoolSize, int maxPoolSize, int queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(prefix);
        executor.initialize();
        return executor;
    }
}
