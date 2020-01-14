package com.example.fpdemo.service;

import com.example.fpdemo.model.FPEnroll;
import com.example.fpdemo.model.FPImage;
import com.example.fpdemo.service.fingerprint.FPJna;
import com.example.fpdemo.service.fingerprint.FPMsg;
import com.example.fpdemo.service.fingerprint.FpCallback;
import com.sun.jna.Pointer;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Base64;

@Service
public class FingerPrintService {
    FPJna mFPJna = new FPJna();
    static boolean isInitDevice = false;
    int mMsgType = -1; // 消息类型
    int mIndex = 0; // 当前采集次数
    int mdwWidth = 0; // 指纹图像宽
    int mdwHeight = 0; // 指纹图像高
    String mstrImage = new String();
    //StringBuilder mstrImage = new StringBuilder();

    // 保存指纹图片
    public void SaveBmp(String path, byte[] data, int w, int h) {
        FileOutputStream fos;
        byte[] d = mFPJna.RawToBmpData(data, w, h);
        try {
            fos = new FileOutputStream(path);
            fos.write(d);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    FpCallback mMsgCallBack = new FpCallback() {
        @Override
        public void FpMessageHandler(int enMsgType, FPMsg pMsgData) throws InterruptedException {
            switch(enMsgType)
            {
                case FPJna.FP_MSG_PRESS_FINGER:
                    System.out.println(">>>>Place your finger");
                    mMsgType = FPJna.FP_MSG_PRESS_FINGER;
                    Thread.sleep(1000);
                    break;
                case FPJna.FP_MSG_RISE_FINGER:
                    System.out.println(">>>>Lift your finger");
                    mMsgType = FPJna.FP_MSG_RISE_FINGER;
                    Thread.sleep(1000);
                    break;
                case FPJna.FP_MSG_ENROLL_TIME:
                    mIndex=pMsgData.dwArg1;
                    System.out.println(">>>>Enroll time:"+mIndex);
                    mMsgType = FPJna.FP_MSG_ENROLL_TIME;
                    Thread.sleep(1000);
                    break;
                case FPJna.FP_MSG_CAPTURED_IMAGE:
                    int w=pMsgData.dwArg1;
                    int h=pMsgData.dwArg2;
                    Pointer p = pMsgData.pbyImage;
                    byte[]data =p.getByteArray(0, w*h) ;
                    //SaveBmp("Enroll"+mIndex+".bmp",data,w,h);
                    System.out.println(">>>>enMsgType:w:"+w+"h:"+h);
                    mMsgType = FPJna.FP_MSG_CAPTURED_IMAGE;
                    mdwWidth = w;
                    mdwHeight = h;
                    Base64.Encoder encoder = Base64.getEncoder();
                    mstrImage = encoder.encodeToString(data);
                    Thread.sleep(1000);
                    break;
            }
        }
    };

    private boolean InitDevice(){
        int iRet = -1;
        if(!isInitDevice){
            //LoadLibrary
            System.out.println("LoadLibrary");
            mFPJna.LoadLibrary();

            //OpenDevice
            System.out.println("start OpenDevice");
            iRet = mFPJna.OpenDevice();
            System.out.println("finish OpenDevice");
            if (iRet != FPJna.FP_SUCCESS) {
                System.out.println("OpenDevice failed. Exit. err:" + iRet);
                isInitDevice = false;
                return false;
            }

            //InstallMessageHandler
            System.out.println("start InstallMessageHandler");
            iRet = mFPJna.InstallMessageHandler(mMsgCallBack);
            System.out.println("finish InstallMessageHandler");
            if(iRet!=FPJna.FP_SUCCESS)
            {
                System.out.println("InstallMessageHandler failed. Exit. err:" + iRet);
                isInitDevice = false;
                mFPJna.CloseDevice();
                return false;
            }

            //SetTimeout
            System.out.println("start SetTimeout");
            iRet=mFPJna.SetTimeout(15);
            System.out.println("finish SetTimeout");
            if(iRet!=FPJna.FP_SUCCESS)
            {
                System.out.println("SetTimeout failed. Exit. err:" + iRet);
                isInitDevice = false;
                mFPJna.CloseDevice();
                return false;
            }
            isInitDevice = true;
        }
        return true;
    }

    public boolean getFPEnroll(FPEnroll fPEnroll){
        int iRet = -1;

        mMsgType = -1; // 消息类型
        mIndex = 0; // 当前采集次数
        mdwWidth = 0; // 指纹图像宽
        mdwHeight = 0; // 指纹图像高
        mstrImage = "";

        if(!isInitDevice){
            if(!InitDevice()){
                return false;
            }
        }

        //FpEnroll
        byte[] pbyFpTemplate=new byte[FPJna.FP_FEATURE_LEN];
        System.out.println("start FpEnroll");
        mFPJna.SetCollectTimes(3);
        iRet =mFPJna.FpEnroll(pbyFpTemplate);
        System.out.println("finish FpEnroll");
        if(iRet!=FPJna.FP_SUCCESS)
        {
            System.out.println("FpEnroll failed. Exit. err:" + iRet);
            isInitDevice = false;
            mFPJna.CloseDevice();
            return false;
        }
        Base64.Encoder encoder = Base64.getEncoder();
        String FpTemplateEncodedStr = encoder.encodeToString(pbyFpTemplate);
        fPEnroll.setFpTemplate(new String(FpTemplateEncodedStr));
        isInitDevice = false;
        mFPJna.CloseDevice();
        return true;
    }

    public void getFPImage(FPImage fPImage){
        fPImage.setMsgType(mMsgType);
        switch (mMsgType)
        {
            case FPJna.FP_MSG_PRESS_FINGER:
                break;
            case FPJna.FP_MSG_RISE_FINGER:
                break;
            case FPJna.FP_MSG_ENROLL_TIME:
                fPImage.setIndex(mIndex);
                break;
            case FPJna.FP_MSG_CAPTURED_IMAGE:
                fPImage.setWidth(mdwWidth);
                fPImage.setHeight(mdwHeight);
                fPImage.setImage(mstrImage);
                break;
            default:
                break;
        }
    }
}
