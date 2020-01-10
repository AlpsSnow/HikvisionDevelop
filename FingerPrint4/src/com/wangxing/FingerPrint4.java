package com.wangxing;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.wangxing.fingerprint.FPJna;
import com.wangxing.fingerprint.FPMsg;
import com.wangxing.fingerprint.FpCallback;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FingerPrint4 {

    static FPJna mFPJna=new FPJna();
    static int index;

    //  指纹模板录入的回调函数
    static FpCallback mFpCallback = new FpCallback() {
        @Override
        public void FpMessageHandler(int enMsgType, FPMsg pMsgData) {

            switch(enMsgType)
            {
                case FPJna.FP_MSG_PRESS_FINGER:
                    // 提示按下手指
                    System.out.println(">>>>Place your finger");
                    break;
                case FPJna.FP_MSG_RISE_FINGER:
                    // 提示抬起手指
                    System.out.println(">>>>Lift your finger");
                    break;
                case FPJna.FP_MSG_ENROLL_TIME:
                    // 提示当前采集次数
                    index=pMsgData.dwArg1;
                    System.out.println(">>>>Enroll time:"+index);
                    break;
                case FPJna.FP_MSG_CAPTURED_IMAGE:
                    // 显示当前录入的指纹图像信息
                    int w=pMsgData.dwArg1; //指纹图像宽
                    int h=pMsgData.dwArg2; //指纹图像高
                    Pointer p = pMsgData.pbyImage; //指纹图像数据
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

        // InstallMessageHandler 注册回调函数
        System.out.println("start InstallMessageHandler");
        iRet = mFPJna.InstallMessageHandler(mFpCallback);
        System.out.println("finish InstallMessageHandler:"+iRet);
        if(iRet!=FPJna.FP_SUCCESS)
        {
            System.out.println("Exit");
            mFPJna.CloseDevice();
            return;
        }

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

        // SetTimeout 设置录入指纹时，等待手指按下和抬起的超时等待时间。
        System.out.println("Start SetTimeout");
        iRet=mFPJna.SetTimeout(15);
        System.out.println("finish SetTimeout:"+iRet);
        if (iRet != FPJna.FP_SUCCESS)
        {
            System.out.println("Exit");
            mFPJna.CloseDevice();
            return;
        }

        // GetTimeout
        System.out.println("start GetTimeout");
        IntByReference ref = new IntByReference();
        iRet=mFPJna.GetTimeout(ref);
        System.out.println("finish GetTimeout:"+iRet+", Time:"+ref.getValue());
        if(iRet!=FPJna.FP_SUCCESS)
        {
            System.out.println("Exit");
            mFPJna.CloseDevice();
            return;
        }

        // FpEnroll 录入指纹模板
        System.out.println("start FpEnroll");
        byte[] pbyFpTemplate=new byte[FPJna.FP_FEATURE_LEN]; // 指纹模板数据
        mFPJna.SetCollectTimes(3); // 设置指纹采集次数
        iRet =mFPJna.FpEnroll(pbyFpTemplate);
        System.out.println("finish FpEnroll:"+iRet);
        if(iRet!=FPJna.FP_SUCCESS)
        {
            System.out.println("Exit");
            mFPJna.CloseDevice();
            return;
        }

        //GetQuality 查询指纹模板质量
        System.out.println("start GetQuality");
        iRet =mFPJna.GetQuality(pbyFpTemplate);
        System.out.println("finish GetQuality:"+iRet);

        //CloseDevice
        mFPJna.CloseDevice();
        System.out.println("Exit");
    }
}
