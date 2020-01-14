package com.example.fpdemo.controller;

import com.example.fpdemo.model.FPEnroll;
import com.example.fpdemo.model.FPImage;
import com.example.fpdemo.service.FingerPrintService;
import com.example.fpdemo.service.fingerprint.FPJna;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins="*")
@RestController
public class FpdemoController {
    @Autowired
    private FingerPrintService fingerPrintService;

    @RequestMapping(value = "/getFpTemplate")
    @ResponseBody
    public Object getFpTemplate(){
        FPEnroll fPEnroll = new FPEnroll();
        boolean isSucceed = fingerPrintService.getFPEnroll(fPEnroll);
        if (!isSucceed){
            fPEnroll.setStatus(0);
            return fPEnroll;
        }

        fPEnroll.setStatus(200);
        System.out.println("getFPEnroll");
        System.out.println(fPEnroll.getFpTemplate());
        return fPEnroll;
    }

    @RequestMapping(value = "/getFpImage")
    @ResponseBody
    public Object getFpImage(){
        FPImage fPImage = new FPImage();
        fingerPrintService.getFPImage(fPImage);
        fPImage.setStatus(200);
        switch (fPImage.getMsgType()){
            case FPJna.FP_MSG_PRESS_FINGER:
                break;
            case FPJna.FP_MSG_RISE_FINGER:
                break;
            case FPJna.FP_MSG_ENROLL_TIME:
                break;
            case FPJna.FP_MSG_CAPTURED_IMAGE:
                break;
            default:
                fPImage.setStatus(0);
                break;
        }
        System.out.println("getFpImage");
        System.out.println("status:"+fPImage.getStatus()+"  MsgType:"+ fPImage.getMsgType() );
        return fPImage;
    }
}
