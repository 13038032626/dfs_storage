package com.example.mydfs_back.threadPool;

import com.example.mydfs_back.threadPool.FileAlterExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.*;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean
    public FileUploadExecutor fileUploadExecutor(){
        FileUploadExecutor executor = new FileUploadExecutor(
                10,
                20,
                50,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.DiscardPolicy()
        );
        return executor;
    }
    @Bean
    public com.example.mydfs_storage.threadPool.FileCopyExecutor fileCopyExecutor(){

        com.example.mydfs_storage.threadPool.FileCopyExecutor executor = new com.example.mydfs_storage.threadPool.FileCopyExecutor(
                10,
                20,
                50,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.DiscardPolicy()
        );
        return executor;

    }
    @Bean
    public FileAlterExecutor fileAlterExecutor(){

        FileAlterExecutor executor = new FileAlterExecutor(
                10,
                20,
                50,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.DiscardPolicy()
        );
        return executor;

    }
}
