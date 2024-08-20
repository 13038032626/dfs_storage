package com.example.mydfs_back.controller;

import com.example.mydfs_back.lib.eventApi.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

@Component
public class Dispatcher {
    @Autowired
    UpdateController updateController;

    @Autowired
    UploadSliceController uploadSliceController;

    @Autowired
    UploadController uploadController;

    public void dispatch(Event event) throws IOException, ExecutionException, InterruptedException {
        switch (event.getMessage()){
            case "alter":
                Integer num = (Integer) event.getArg("num");
                MultipartFile arg =(MultipartFile) event.getArg("newFile");
                updateController.alter(num,arg);
            case "upload":
                MultipartFile arg2 =(MultipartFile) event.getArg("File");
                uploadController.upload(arg2);
            case "uploadSlices":
                MultipartFile arg3 =(MultipartFile) event.getArg("File");
                uploadController.upload(arg3);
            default:
                throw new IllegalArgumentException("别试其他");
        }
    }
}
