package com.example.fingerprint5.service.fingerprint;

import java.io.File;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

public class FPJna {

    /* 函数返回值定义 */
    public static final int FP_SUCCESS  		=  		(0); // 执行成功
    public static final int FP_CONNECTION_ERR 	=  		(1); // 通信失败
    public static final int FP_TIMEOUT    		=    	(2); // 采集超时
    public static final int FP_ENROLL_FAIL  	=     	(3); // 录入指纹失败
    public static final int FP_PARAM_ERR   		=    	(4); // 参数错误
    public static final int FP_INIT_ERR   		=    	(5); // 未初始化

    public static final int FP_FEATURE_LEN   	=    	(512); // 特征长度
    public static final int FP_IMAGE_WIDTH   	=    	(256); // 图像宽度
    public static final int FP_IMAGE_HEIGHT   	=    	(288); // 图像高度

    /* 消息类型定义 */
    public static final int FP_MSG_PRESS_FINGER =0;//按压消息
    public static final int FP_MSG_RISE_FINGER	=1;//抬起消息
    public static final int FP_MSG_ENROLL_TIME	=2;//注册次数
    public static final int FP_MSG_CAPTURED_IMAGE=3;//采集图像

    Jna mJna;
    public void LoadLibrary() {

        if(System.getProperty("sun.arch.data.model").contains("32")) //判断是32位还是64位
        {
            System.out.println("LoadLibrary lib/x86/FPModule_SDK");
            mJna = (Jna) Native.loadLibrary(new File("DLL"+File.separator+"x86"+File.separator+"FPModule_SDK").getAbsolutePath(), Jna.class);
        }
        else
        {
            System.out.println("LoadLibrary lib/x64/FPModule_SDK");
            mJna = (Jna) Native.loadLibrary(new File("DLL"+File.separator+"x64"+File.separator+"FPModule_SDK").getAbsolutePath(), Jna.class);

        }
    }

    /** @func   : FPModule_OpenDevice
     *  @brief  : 连接设备
     *  @param  : None
     *  @return : 0->连接成功 1->通信失败
     */
    public  int OpenDevice()
    {
        if(mJna==null)
            return FP_INIT_ERR;
        return mJna.FPModule_OpenDevice();
    }


    /** @func   : FPModule_CloseDevice
     *  @brief  : 断开设备
     *  @param  : None
     *  @return : 0->断开成功 1->通信失败
     */
    public  int CloseDevice()
    {
        if(mJna==null)
            return FP_INIT_ERR;
        return mJna.FPModule_CloseDevice();
    }


    /** @func   : FPModule_DetectFinger
     *  @brief  : 检测指纹输入状态
     *  @param  : pdwFpstatus[out] -> 0:无指纹输入  1:有指纹输入
     *  @return : 0->执行成功 1->通信失败
     */
    public  int DetectFinger(IntByReference pdwFpstatus)
    {
        if(mJna==null)
            return FP_INIT_ERR;
        if(pdwFpstatus==null)
            return FP_PARAM_ERR;
        return mJna.FPModule_DetectFinger(pdwFpstatus);
    }


    /** @func   : FPModule_CaptureImage
     *  @brief  : 采集指纹图像
     *  @param  : pbyImageData[out] -> 指纹图像数据（数据长度为 图像宽度 x 图像高度）
    pdwWidth[out]     -> 指纹图像宽度
    pdwHeight[out]    -> 指纹图像高度
     *  @return : 0->执行成功 1->通信失败
     */
    public  int CaptureImage(byte[]pbyImageData, IntByReference pdwWidth, IntByReference pdwHeight)
    {
        if(mJna==null)
            return FP_INIT_ERR;
        if(pbyImageData==null||pdwWidth==null||pdwHeight==null)
            return FP_PARAM_ERR;
        return mJna.FPModule_CaptureImage(pbyImageData,pdwWidth,pdwHeight);
    }


    /** @func   : FPModule_SetTimeout
     *  @brief  : 设置采集超时时间
     *  @param  : dwSecond[in] -> 超时时间(单位：秒) 可设置值：1秒至60秒
     *  @return : 0->执行成功 1->通信失败
     */
    public  int SetTimeout(int dwSecond)
    {
        if(mJna==null)
            return FP_INIT_ERR;
        return mJna.FPModule_SetTimeout(dwSecond);
    }


    /** @func   : FPModule_GetTimeout
     *  @brief  : 获取采集超时时间
     *  @param  : pdwSecond[out] -> 超时时间 单位：秒
     *  @return : 0->执行成功 1->通信失败
     */
    public  int GetTimeout(IntByReference pdwSecond)
    {
        if(mJna==null)
            return FP_INIT_ERR;
        if(pdwSecond==null)
            return FP_PARAM_ERR;
        return mJna.FPModule_GetTimeout(pdwSecond);
    }

    /** @func   : FPModule_SetCollectTimes
     *  @brief  : 设置采集次数
     *  @param  : dwTimes[in] -> 0~4,0默认模式（2~4次），1~3采集次数
     *  @return : 0->执行成功 1->通信失败
     */
    public  int SetCollectTimes(int dwTimes)
    {
        if(mJna==null)
            return FP_INIT_ERR;
        return mJna.FPModule_SetCollectTimes(dwTimes);
    }


    /** @func   : FPModule_GetCollectTimes
     *  @brief  : 获取采集次数
     *  @param  : pdwTimes[out] -> 采集次数
     *  @return : 0->执行成功 1->通信失败
     */
    public  int GetCollectTimes(IntByReference pdwTimes)
    {
        if(mJna==null)
            return FP_INIT_ERR;
        if(pdwTimes==null)
            return FP_PARAM_ERR;
        return mJna.FPModule_GetCollectTimes(pdwTimes);
    }
    /** @func   : FPModule_InstallMessageHandler
     *  @brief  : 设置消息回调函数
     *  @param  : msgHandler[in] -> 消息处理函数
     *  @return : 0->执行成功
     */


    public  int InstallMessageHandler(FpCallback msgHandler)
    {
        if(msgHandler==null)
            return FP_PARAM_ERR;

        return mJna.FPModule_InstallMessageHandler(msgHandler);

    }


    /** @func   : FPModule_FpEnroll
     *  @brief  : 录入指纹
     *  @param  : pbyFpTemplate[out] -> 指纹模板(512字节)
     *  @return : 0->执行成功 1->通信失败 2->采集超时 3->录入失败
     */
    public  int FpEnroll(byte[]pbyFpTemplate)
    {
        if(mJna==null)
            return FP_INIT_ERR;
        if(pbyFpTemplate==null)
            return FP_PARAM_ERR;
        return mJna.FPModule_FpEnroll(pbyFpTemplate);
    }


    /** @func   : FPModule_MatchTemplate
     *  @brief  : 比对两枚指纹模板
     *  @param  : pbyFpTemplate1[in] -> 指纹模板1(512字节)
    pbyFpTemplate2[in] -> 指纹模板2(512字节)
    dwSecurityLevel[in] -> 安全等级（1~5）
     *  @return : 0->比对成功 6->比对失败 4->参数错误
     */
    public int MatchTemplate(byte[]pbyFpTemplate1, byte[]pbyFpTemplate2, int dwSecurityLevel)
    {
        if(mJna==null)
            return FP_INIT_ERR;
        return mJna.FPModule_MatchTemplate(pbyFpTemplate1,pbyFpTemplate2,dwSecurityLevel);
    }



    /** @func   : FPModule_GetQuality
     *  @brief  : 获取指纹模板质量分数
     *  @param  : pbyFpTemplate[in] -> 指纹模板(512字节)
     *  @return : 指纹模板分数(0~100) 分数越高，表示模板的质量越好
     */
    public  int GetQuality(byte[]pbyFpTemplate)
    {
        if(mJna==null)
            return FP_INIT_ERR;
        if(pbyFpTemplate==null)
            return FP_PARAM_ERR;
        return mJna.FPModule_GetQuality(pbyFpTemplate);
    }


    /** @func   : FPModule_GetDeviceInfo
     *  @brief  : 获取指纹采集仪版本信息
     *  @param  : pbyDeviceInfo[out] -> 指纹采集仪版本信息(64字节)
     *  @return : 0->执行成功 1->通信失败
     */
    public  String GetDeviceInfo()
    {
        byte[]pbyDeviceInfo = new byte[64];
        if(mJna==null)
            return null;
        if(mJna.FPModule_GetDeviceInfo(pbyDeviceInfo)!=FP_SUCCESS)
            return null;
        return new String(pbyDeviceInfo).trim();
    }


    /** @func   : FPModule_GetSDKVersion
     *  @brief  : 获取指纹采集仪SDK版本信息
     *  @param  : pbySDKVersion[out] -> 指纹采集仪SDK版本信息(64字节)
     *  @return : 0->执行成功
     */
    public  String GetSDKVersion()
    {
        byte[]pbySDKVersion = new byte[64];
        if(mJna==null)
            return null;
        if(mJna.FPModule_GetSDKVersion(pbySDKVersion)!=FP_SUCCESS)
            return null;
        return new String(pbySDKVersion).trim();
    }

    public  byte[] RawToBmpData(byte[] bydata,int X,int Y)
    {
        byte[] image = new byte [X*Y+1078];
        int i=0;
        byte head[]={
                /***************************/
                //file header
                0x42,0x4d,//file type
                //0x36,0x6c,0x01,0x00, //file size***
                0x0,0x0,0x0,0x00, //file size***
                0x00,0x00, //reserved
                0x00,0x00,//reserved
                0x36,0x4,0x00,0x00,//head byte***
                /***************************/
                //infoheader
                0x28,0x00,0x00,0x00,//struct size
                0x00,0x00,0x00,0x00,//map width***
                0x00,0x00,0x00,0x00,//map height***

                0x01,0x00,//must be 1
                0x08,0x00,//color count***
                0x00,0x00,0x00,0x00, //compression
                0x00,0x00,0x00,0x00,//data size***
                0x00,0x00,0x00,0x00, //dpix
                0x00,0x00,0x00,0x00, //dpiy
                0x00,0x00,0x00,0x00,//color used
                0x00,0x00,0x00,0x00,//color important
        };
        int num;
        //确定图象宽度数值
        num=X; 		 head[18]= (byte) (num & 0xFF);
        num=num>>8;  head[19]= (byte) (num & 0xFF);
        num=num>>8;  head[20]= (byte) (num & 0xFF);
        num=num>>8;  head[21]= (byte) (num & 0xFF);
        //确定图象高度数值
        num=Y; 		 head[22]= (byte) (num & 0xFF);
        num=num>>8;  head[23]= (byte) (num & 0xFF);
        num=num>>8;  head[24]= (byte) (num & 0xFF);
        num=num>>8;  head[25]= (byte) (num & 0xFF);

        System.arraycopy(head, 0, image, 0, head.length);
        byte bmpex[]= new byte[1024];
        int j=0;
        for (i=0;i<1024;i=i+4)
        {
            bmpex[i]=bmpex[i+1]=bmpex[i+2]=(byte) j;
            bmpex[i+3]=0;
            j++;
        }
        System.arraycopy(bmpex, 0, image, head.length, bmpex.length);
        for( i = 0; i < Y; i++ )
        {
            System.arraycopy(bydata, i * X, image, 1078 * 1 + ( Y - 1 - i ) * X, X);
        }
        return image;
    }

}

