package com.example.mydfs_back.controller;

import com.example.mydfs_back.copy.AutoCopy;
import com.example.mydfs_back.spaceController.FileSelf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class UploadController {

    /*
        用于上传文件

        思路：
        链表化，上传的每个slice跟到后面

        Attention：
        解决并发写问题 写时复制
     */

    @Autowired
    FileSelf fileSelf;
    @Autowired
    com.example.mydfs_back.threadPool.FileUploadExecutor uploadExecutor;

    @Autowired
    AutoCopy autoCopy;
    List<byte[]> addList = new ArrayList<>();
    int queuedNum = 0;
    Integer startIndex = fileSelf.getStartIndex();
    @Value("savePath")
    String savePath;


    @PostMapping("upload")
    public void upload(MultipartFile file) throws IOException {
        //用线程池初步解决并发问题——>当多个请求同时来存储数据，在线程池中依次执行，但有风险被reject掉
        //第一个想法：文件下载允许等————将阻塞队列设置为无限————有点串行化的意思 | 但可能耗时很长，有些线程会长时间阻塞（即使它的小文件）
        //第二个想法：降低每个线程上传速度，但确保进度条可以慢慢滚动（但线程切换和内存占用都挺高的）
        //第三个想法：结合线程池和短链接————由于我的DFS天然文件分片，可以将大文件上传按照片为单位与线程池进行短链接（可以保证小文件尽快上传，大文件慢慢传）

        //原本负载均衡就是不想让单台服务器并发太高，所以感觉解决并发还得是买设备
        uploadExecutor.execute(()->{
            try {
                String contentType = file.getContentType();
                String originalFilename = file.getOriginalFilename();
                String form = originalFilename.split("\\.")[1];

                UUID uuid = UUID.randomUUID();
                String fileName = uuid+"\\."+form;

                FileOutputStream outputStream = new FileOutputStream(savePath + fileName);
                uploadFile(file, fileName, outputStream);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        //实现当高并发访问结束后进行复制————当线程池中空余线程较多时，说明上传结束

        /*
        核心问题：复制的频率
        原本设计是上传一个文件复制一次（一个/两个http）
        但在并发量较高时，复制操作需要避开上传的高峰，所以加入队列中临时保存，当高并发访问即将结束时启动复制（任然是一个文件两个http）
         */

        if(uploadExecutor.getActiveCount()>uploadExecutor.getCorePoolSize()){
            autoCopy.copy();
        }
    }
    private void uploadFile(MultipartFile file, String fileName, FileOutputStream outputStream) throws IOException {
        InputStream inputStream = file.getInputStream();

        byte[] bytes = new byte[1024];
        int readLen = -1;
        while ((readLen=inputStream.read(bytes))!=-1){
            outputStream.write(bytes,0,readLen);
        }
        System.out.println("存储完成: "+fileName);
        //在此将文件加入队列
        autoCopy.queue.add(file);
    }

}

