package com.example.fingerprint5.service;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.example.fingerprint5.service.fingerprint.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.UUID;

@Service
public class FingerPrintService {

    static FPJna mFPJna = new FPJna();

    // 保存指纹图片
    public static void SaveBmp(String path, byte[] data, int w, int h) {
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

    public boolean getPbyImageData(StringBuilder imageName) throws IOException {
        int iRet = -1;

        //Init
        System.out.println("LoadLibrary");
        mFPJna.LoadLibrary();

        //OpenDevice
        System.out.println("start OpenDevice");
        iRet = mFPJna.OpenDevice();
        System.out.println("finish OpenDevice:" + iRet);
        if (iRet != FPJna.FP_SUCCESS) {
            System.out.println("Exit");
            return false;
        }

        //GetDeviceInfo
        String devInfo = mFPJna.GetDeviceInfo();
        System.out.println("GetDeviceInfo:" + devInfo);
        if (devInfo == null) {
            System.out.println("Exit");
            mFPJna.CloseDevice();
            return false;
        }

        //GetSDKVersion
        String sdkInfo = mFPJna.GetSDKVersion();
        System.out.println("GetSDKVersion:" + sdkInfo);
        if (sdkInfo == null) {
            System.out.println("Exit");
            mFPJna.CloseDevice();
           return false;
        }

        //指纹采集
        long st = System.currentTimeMillis();
        IntByReference pdwFpstatus = new IntByReference();
        IntByReference pdwWidth = new IntByReference();
        IntByReference pdwHeight = new IntByReference();
        byte[] pbyImageData = new byte[FPJna.FP_IMAGE_WIDTH * FPJna.FP_IMAGE_HEIGHT];
        System.out.println("start DetectFinger");
        System.out.println(">>>>Place your finger in 15s");
        while ((System.currentTimeMillis() - st) / 1000 < 15) {
            iRet = mFPJna.DetectFinger(pdwFpstatus);
            if (iRet != FPJna.FP_SUCCESS) {
                System.out.println(String.format("DetectFinger() failed,Err=%d. Exit.", iRet));
                mFPJna.CloseDevice();
                return false;
            }
            if (pdwFpstatus.getValue() == 1) {
                // 采集指纹图像
                System.out.println(">>>>CaptureImage...");
                iRet = mFPJna.CaptureImage(pbyImageData, pdwWidth, pdwHeight);
                if (iRet != FPJna.FP_SUCCESS) {
                    System.out.println(String.format("CaptureImage() failed,Err=%d, Exit.", iRet));
                    mFPJna.CloseDevice();
                    return false;
                }
                String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                imageName.append(uuid +".bmp");
                SaveBmp("D:/DetectFinger/"+imageName.toString(), pbyImageData, pdwWidth.getValue(), pdwHeight.getValue());
                System.out.println(">>>>CaptureImage:" + iRet);
                break;
            }
        }
        if (pdwFpstatus.getValue() != 1) {
            System.out.println("finish DetectFinger TimeOut");
            mFPJna.CloseDevice();
            System.out.println("Exit");
            return false;
        }
        System.out.println("finish DetectFinger");
        mFPJna.CloseDevice();
        System.out.println("Exit");
        return true;
    }

}
