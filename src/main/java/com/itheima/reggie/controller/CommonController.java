package com.itheima.reggie.controller;

import com.itheima.reggie.pojo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        log.info(file.toString());
        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        //修改为随机文件名防止重复
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        //.xxx获取文件后缀
        String newName = UUID.randomUUID().toString() + substring;

        File f = new File(basePath);
        if (!f.exists()) {
            f.mkdirs();
        }
        InputStream inputStream = file.getInputStream();
        OutputStream outputStream = new FileOutputStream(new File(basePath + newName));

        byte[] bytes = new byte[1024];
        int l;
        while ((l = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, l);
        }
        outputStream.close();

        //判断目录是否存在，不存在时则会创建
        //file.transferTo();
        //这里改用io流解决使用相对路径所产生的java.io.FileNotFoundException
        //设置指定保存路径转存


        //返回修改后文件名

        return R.success(newName);


    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        FileInputStream inputStream = new FileInputStream(basePath + name);
        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("image//jpeg");
        int len = 0;
        byte[] bytes = new byte[1024];

        while ((len = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, len);
            outputStream.flush();
        }
        outputStream.close();
        inputStream.close();


    }


}
