package com.example.fingerprint5.controller;

import com.example.fingerprint5.model.FingerPrintImage;
import com.example.fingerprint5.service.FingerPrintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


@RestController
public class FingerPrintController {

    @RequestMapping(value = "/image", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[]  getImage() throws IOException {
        File file = new File("D:/github/HikvisionDevelop/FingerPrint3/out/artifacts/FingerPrint3_jar/CaptureImage.bmp");
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes, 0, inputStream.available());
        return bytes;
    }

    @RequestMapping(value = "/getImage", method = RequestMethod.GET /*, produces = "image/png"*/)
    public ResponseEntity getFile() throws IOException {
        //Result result = fileService.getFile(id);
        File file = new File("D:/github/HikvisionDevelop/FingerPrint3/out/artifacts/FingerPrint3_jar/CaptureImage.bmp");
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes, 0, inputStream.available());
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = MediaType.parseMediaType("image/png");
        headers.setContentType(mediaType);
        ResponseEntity e = new ResponseEntity(bytes, headers, HttpStatus.OK);
        return e;
    }

    @Autowired
    private FingerPrintService fingerPrintService;

    @RequestMapping(value = "/test", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[]  getFingerPrintImage() throws IOException {
        StringBuilder imageName = new StringBuilder();
        boolean iRet = false;
        iRet = fingerPrintService.getPbyImageData(imageName);
        if (!iRet){
            byte[] date = new byte[0];
            return date;
        }
        File file = new File(imageName.toString());
        FileInputStream inputStream = new FileInputStream(file);
        byte[] imagedata = new byte[inputStream.available()];
        inputStream.read(imagedata, 0, inputStream.available());
        return imagedata;
    }

    @RequestMapping(value = "/getImageName")
    @ResponseBody
    public Object  getImageName() throws IOException {
        FingerPrintImage fingerPrintImage = new FingerPrintImage();
        StringBuilder imageName = new StringBuilder();
        boolean iRet = false;
        iRet = fingerPrintService.getPbyImageData(imageName);
        if (!iRet){
            return fingerPrintImage;
        }
        File file = new File("D:/DetectFinger/"+imageName.toString());
        FileInputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer, 0, inputStream.available());
        inputStream.close();
        BASE64Encoder encoder = new BASE64Encoder();
        String imgStr = encoder.encode(buffer);
        fingerPrintImage.setName(imageName.toString());
        fingerPrintImage.setBase64data("date:image/jpeg;base64,"+imgStr);
        return fingerPrintImage;

    }


}
