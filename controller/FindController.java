package com.example.mydfs_back.controller;

import com.example.mydfs_storage.spaceController.FileSelf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
public class FindController {

    /*
        用于找文件
     */

    @Autowired
    FileSelf fileSelf;

    @Value("savePath")
    String savePath;

    HashMap<Integer,byte[]> cache = FileSelf.hotCache;
    HashMap<Integer,Integer> timesCount = new HashMap<>();
    public Integer findFileIndex(String hash){
        List<String> hashes = fileSelf.getHashes();
        int index = hashes.indexOf(hash);
        return index;
    }
    @GetMapping("/findFile")
    public byte[] findFile(String hash) throws IOException {
        File file = new File(savePath);
        File[] files = file.listFiles();
        for (File f : files
        ) {
            if(f.getName().equals(hash)){
                FileInputStream inputStream = new FileInputStream(f);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int readLen = -1;
                while ((readLen=inputStream.read(bytes))!=-1){
                    outputStream.write(bytes,0,readLen);
                }
                return outputStream.toByteArray();
            }
        }
        return null;
    }

}
