package com.wangxing;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.wangxing.fingerprint.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FingerPrint3 {

    static FPJna mFPJna=new FPJna();

    static FpCallback mFpCallback = new FpCallback() {
        @Override
        public void FpMessageHandler(int enMsgType, FPMsg pMsgData) {
            int index =0;
            switch(enMsgType)
            {
                case FPJna.FP_MSG_PRESS_FINGER:
                    System.out.println(">>>>Place your finger");
                    break;
                case FPJna.FP_MSG_RISE_FINGER:
                    System.out.println(">>>>Lift your finger");
                    break;
                case FPJna.FP_MSG_ENROLL_TIME:
                    // 提示当前采集次数
                    index=pMsgData.dwArg1;
                    System.out.println(">>>>Enroll time:"+index);
                    break;
                case FPJna.FP_MSG_CAPTURED_IMAGE:
                    // 显示当前录入的指纹图像信息
                    int w=pMsgData.dwArg1;
                    int h=pMsgData.dwArg2;
                    Pointer p = pMsgData.pbyImage;
                    byte[]data =p.getByteArray(0, w*h) ;
                    SaveBmp("Enroll"+index+".bmp",data,w,h);
                    System.out.println(">>>>enMsgType:w:"+w+"h:"+h);
                    break;
                default:
                    // 错误
                    System.out.println("Unexpected value: " + enMsgType);
            }
        }
    };

    // 保存指纹图片
    public static void SaveBmp(String path,byte[] data,int w,int h) {
        FileOutputStream fos;
        byte[]d = mFPJna.RawToBmpData(data,w,h);
        try {
            fos=new FileOutputStream(path);
            fos.write(d);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        int iRet=-1;

        //Init
        System.out.println("LoadLibrary");
        mFPJna.LoadLibrary();

        //OpenDevice
        System.out.println("start OpenDevice");
        iRet = mFPJna.OpenDevice();
        System.out.println("finish OpenDevice:"+iRet);
        if(iRet!=FPJna.FP_SUCCESS)
        {
            System.out.println("Exit");
            return;
        }

        //InstallMessageHandler
        /*
        System.out.println("start InstallMessageHandler");
        iRet = mFPJna.InstallMessageHandler(mFpCallback);
        System.out.println("finish InstallMessageHandler:"+iRet);
        if(iRet!=FPJna.FP_SUCCESS)
        {
            System.out.println("Exit");
            mFPJna.CloseDevice();
            return;
        }
        */
        //GetDeviceInfo
        String devInfo = mFPJna.GetDeviceInfo();
        System.out.println("GetDeviceInfo:"+devInfo);
        if(devInfo==null)
        {
            System.out.println("Exit");
            mFPJna.CloseDevice();
            return;
        }

        //GetSDKVersion
        String sdkInfo = mFPJna.GetSDKVersion();
        System.out.println("GetSDKVersion:"+sdkInfo);
        if(sdkInfo==null)
        {
            System.out.println("Exit");
            mFPJna.CloseDevice();
            return;
        }

        //指纹采集
        long st = System.currentTimeMillis();
        IntByReference pdwFpstatus = new IntByReference();
        IntByReference pdwWidth = new IntByReference();
        IntByReference pdwHeight = new IntByReference();
        byte[]pbyImageData = new byte[FPJna.FP_IMAGE_WIDTH*FPJna.FP_IMAGE_HEIGHT];
        System.out.println("start DetectFinger");
        System.out.println(">>>>Place your finger in 15s");
        while((System.currentTimeMillis()-st)/1000<15)
        {
            iRet=mFPJna.DetectFinger(pdwFpstatus);
            if(iRet!=FPJna.FP_SUCCESS)
            {
                System.out.println(String.format("DetectFinger() failed,Err=%d. Exit.", iRet));
                mFPJna.CloseDevice();
                return;
            }
            if(pdwFpstatus.getValue()==1)
            {
                // 采集指纹图像
                System.out.println(">>>>CaptureImage...");
                iRet =mFPJna.CaptureImage(pbyImageData, pdwWidth, pdwHeight);
                if(iRet!=FPJna.FP_SUCCESS)
                {
                    System.out.println(String.format("CaptureImage() failed,Err=%d, Exit.", iRet));
                    mFPJna.CloseDevice();
                    return;
                }
                SaveBmp("CaptureImage.bmp",pbyImageData,pdwWidth.getValue(),pdwHeight.getValue());
                System.out.println(">>>>CaptureImage:"+iRet);
                break;
            }
        }
        if(pdwFpstatus.getValue()!=1)
            System.out.println("finish DetectFinger TimeOut");
        else
            System.out.println("finish DetectFinger");
    }
}
