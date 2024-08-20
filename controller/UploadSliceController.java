package com.example.mydfs_back.controller;

import com.example.mydfs_back.copy.AutoCopy;
import com.example.mydfs_back.spaceController.FileSelf;
import com.example.mydfs_back.threadPool.FileUploadExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

@RestController
public class UploadSliceController {

    @Autowired
    FileSelf fileSelf;
    @Autowired
    FileUploadExecutor uploadExecutor;

    @Autowired
    AutoCopy autoCopy;

    @Value("storageNum")
    Integer storageNum;
    @Value("savePath")
    String savePath;


    @PostMapping("uploadSlices")
    public void uploadSlices(MultipartFile file) throws IOException {

        uploadExecutor.execute(()->{
            try{
                String originalFilename = file.getOriginalFilename(); //策略：client在分片的时候将file的name，序数存在name里
                String[] split = originalFilename.split("\\.");

                String[] metaData = split[0].split("\\|");
                String num = metaData[1];
                //TODO 不应该用UUID，应该换成hash
                UUID uuid = UUID.randomUUID();
                String fileName = uuid+"\\."+ split[1];

                FileOutputStream outputStream = new FileOutputStream(savePath+"/slices"+fileName);
                uploadFile(file, fileName, outputStream);

            }catch (Exception e){
                e.printStackTrace();
            }
        });
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
    public Boolean uploadOneSlice(InputStream inputStream) throws IOException { //核心是解决并发写

    }
    @PostMapping("upload")
    public Boolean uploadOneSlice(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        return uploadOneSlice(inputStream);
    }


}
