package com.wangxing;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.examples.win32.W32API.HWND;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;

public class FingerPrint1 {

    private static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;

    // NET_DVR_Login_V4参数声明
    private static NativeLong lUserID;//用户句柄
    private static HCNetSDK.NET_DVR_USER_LOGIN_INFO m_strLoginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();//设备登录信息
    private static HCNetSDK.NET_DVR_DEVICEINFO_V40 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();//设备信息
    private static String m_sDeviceIP;//已登录设备的IP地址
    private static String m_sUsername;//设备用户名
    private static String m_sPassword;//设备密码

    // NET_DVR_StartRemoteConfig参数声明
    private static HCNetSDK.NET_DVR_FINGER_PRINT_INFO_COND m_strlpInBuffer = new HCNetSDK.NET_DVR_FINGER_PRINT_INFO_COND(); //LpInBuffer
    private static String m_sCardNo; //关联卡的卡号
    private static NativeLong m_lGetFingerPrintCfgHandle;
    private static FRemoteConfigCallback m_fRemoteConfigCallback;

    private static boolean m_bGetFingerPrintCfgFinish;

    // 指纹数据
    private static HCNetSDK.NET_DVR_FINGER_PRINT_CFG m_strFingerPrintCfg = new HCNetSDK.NET_DVR_FINGER_PRINT_CFG(); //指纹信息

    public static void main(String[] args) throws InterruptedException {
        // write your code here
        if(args.length != 5){
            System.out.println("usage：" );
            System.out.println("java -jar FingerPrint1.jar <IP> <Port> <UserName> <Password> <CardNO>" );
            return;
        }
        // 初始化SDK资源
        boolean rt = hCNetSDK.NET_DVR_Init();

        if (rt != true)
        {
            System.out.println("NET_DVR_Init() failed. ErrorCode:" + hCNetSDK.NET_DVR_GetLastError());
            return;
        }

        // 设置连接超时时间与重连功能
        rt = hCNetSDK.NET_DVR_SetConnectTime(2000, 1);
        if (rt != true)
        {
            System.out.println("NET_DVR_SetConnectTime() failed. ErrorCode:" + hCNetSDK.NET_DVR_GetLastError());
            hCNetSDK.NET_DVR_Cleanup();
            return;
        }
        rt = hCNetSDK.NET_DVR_SetReconnect(10000, true);
        if (rt != true)
        {
            System.out.println("NET_DVR_SetReconnect() failed. ErrorCode:" + hCNetSDK.NET_DVR_GetLastError());
            hCNetSDK.NET_DVR_Cleanup();
            return;
        }

        // 注册设备 LONG NET_DVR_Login_V40
        lUserID = new NativeLong(-1);
        //m_sDeviceIP = "192.168.160.1"; //设备ip地址
        m_sDeviceIP = args[0];
        m_strLoginInfo.sDeviceAddress = new byte[HCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN];
        System.arraycopy(m_sDeviceIP.getBytes(), 0, m_strLoginInfo.sDeviceAddress, 0, m_sDeviceIP.length());

        //m_sUsername = "admin";//设备用户名
        m_sUsername = args[2];
        m_strLoginInfo.sUserName = new byte[HCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN];
        System.arraycopy(m_sUsername.getBytes(), 0, m_strLoginInfo.sUserName, 0, m_sUsername.length());

        //m_sPassword = new String("password");//设备密码
        m_sPassword = args[3];
        m_strLoginInfo.sPassword = new byte[HCNetSDK.NET_DVR_LOGIN_PASSWD_MAX_LEN];
        System.arraycopy(m_sPassword.getBytes(), 0, m_strLoginInfo.sPassword, 0, m_sPassword.length());

        //m_strLoginInfo.wPort = (short)Integer.parseInt("8000"); // 设备端口号
        m_strLoginInfo.wPort = (short)Integer.parseInt(args[1]);

        m_strLoginInfo.bUseAsynLogin = false; //是否异步登录：0- 否，1- 是

        m_strLoginInfo.write();
        lUserID = hCNetSDK.NET_DVR_Login_V40(m_strLoginInfo, m_strDeviceInfo);
        if (lUserID.longValue() == -1) {
            System.out.println("NET_DVR_Login_V40() failed. ErrorCode:" + hCNetSDK.NET_DVR_GetLastError());
            hCNetSDK.NET_DVR_Cleanup();
            return;
        } else {
            System.out.println("NET_DVR_Login_V40() SUCCESS.");
        }

        //获取一枚指纹参数
        m_sCardNo = args[4];
        FingerPrint1 objFingerPrint = new FingerPrint1();
        if(!objFingerPrint.GetFingerPrintCfg())
        {
            hCNetSDK.NET_DVR_Logout(lUserID);
            hCNetSDK.NET_DVR_Cleanup();
            return;
        }

        // 关闭长连接
        do {
            // 等待指纹数据读取完成
            Thread.sleep(1000);
        }while(!m_bGetFingerPrintCfgFinish);

        // 若获取卡参数完成则关闭长连接
        rt = hCNetSDK.NET_DVR_StopRemoteConfig(m_lGetFingerPrintCfgHandle);
        if (rt != true)
        {
            System.out.println("NET_DVR_StopRemoteConfig() failed. ErrorCode:" + hCNetSDK.NET_DVR_GetLastError());
            hCNetSDK.NET_DVR_Logout(lUserID);
            hCNetSDK.NET_DVR_Cleanup();
            return;
        }

        // 注销
        rt =hCNetSDK.NET_DVR_Logout(lUserID);
        if (rt != true)
        {
            System.out.println("NET_DVR_Logout() failed. ErrorCode:" + hCNetSDK.NET_DVR_GetLastError());
            hCNetSDK.NET_DVR_Cleanup();
            return;
        }

        // 释放SDK资源
        rt = hCNetSDK.NET_DVR_Cleanup();
        if (rt != true)
        {
            System.out.println("NET_DVR_Cleanup() failed. ErrorCode:" + hCNetSDK.NET_DVR_GetLastError());
            return;
        }
        return;
    }

    // 获取指纹函数
    public boolean GetFingerPrintCfg()
    {
        NativeLong rt = new NativeLong(-1);
        m_strlpInBuffer.dwSize = m_strlpInBuffer.size();
        //m_sCardNo = new String("1");
        m_strlpInBuffer.byCardNo = new byte[HCNetSDK.ACS_CARD_NO_LEN];
        System.arraycopy(m_sCardNo.getBytes(), 0, m_strlpInBuffer.byCardNo,0,m_sCardNo.length());
        m_strlpInBuffer.dwFingerPrintNum = 1; //获取指纹数量
        m_strlpInBuffer.byEnableCardReader = new byte[HCNetSDK.MAX_CARD_READER_NUM_512];
        m_strlpInBuffer.byEnableCardReader[0] = 1;
        m_strlpInBuffer.byFingerPrintID = 1;

        m_strlpInBuffer.write();
        Pointer lpInBuffer = m_strlpInBuffer.getPointer();

        FingerPrint1 objFingerPrint1 = new FingerPrint1();
        m_fRemoteConfigCallback = new FRemoteConfigCallback();

        m_bGetFingerPrintCfgFinish = false;
        rt = hCNetSDK.NET_DVR_StartRemoteConfig(
                lUserID,
                hCNetSDK.NET_DVR_GET_FINGERPRINT_CFG,
                lpInBuffer,
                m_strlpInBuffer.size(),
                m_fRemoteConfigCallback,
                null);
        if(rt.longValue() == -1)
        {
            System.out.println("NET_DVR_StartRemoteConfig() failed. ErrorCode:" + hCNetSDK.NET_DVR_GetLastError());
            return false;
        }
        return true;
    }

    // 获取指纹回调函数
    public void GetFingerPrintCfgCallback(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData){
        if (dwType == hCNetSDK.NET_SDK_CALLBACK_TYPE_DATA)
        {
            System.out.println("GetFingerPrintCfgCallback NET_SDK_CALLBACK_TYPE_DATA");
            //数据信息
            m_strFingerPrintCfg.write();
            Pointer pFingerPrintCfg = m_strFingerPrintCfg.getPointer();
            pFingerPrintCfg.write(0, lpBuffer.getByteArray(0,m_strFingerPrintCfg.size()), 0, m_strFingerPrintCfg.size());
            m_strFingerPrintCfg.read();
            if(m_strFingerPrintCfg.dwsize != 0) {
                System.out.println("FingerPrintDate：" + new String(m_strFingerPrintCfg.byFingerData));
            }else{
                System.out.println("GetFingerPrintCfgCallback NET_SDK_CALLBACK_TYPE_DATA, FingerPrintDate'size = 0. ErrorCode:" + hCNetSDK.NET_DVR_GetLastError());
            }
        }else if (dwType == hCNetSDK.NET_SDK_CALLBACK_TYPE_STATUS)
        {
            //状态值
            HCNetSDK.NET_DVR_FINGER_PRINT_CFG_FAILED cfgBuf = new HCNetSDK.NET_DVR_FINGER_PRINT_CFG_FAILED();
            Pointer pcpBuff = cfgBuf.getPointer();
            pcpBuff.write(0, lpBuffer.getByteArray(0,cfgBuf.size()), 0, cfgBuf.size());
            cfgBuf.read();

            if (cfgBuf.dwstatus == HCNetSDK.NET_SDK_CALLBACK_STATUS_SUCCESS)
            {
                m_bGetFingerPrintCfgFinish = true; //获取指纹参数完成
                System.out.println("GetFingerPrintCfgCallback NET_SDK_CALLBACK_STATUS_SUCCESS");
            }else if(cfgBuf.dwstatus == HCNetSDK.NET_SDK_CALLBACK_STATUS_FAILED)
            {
                byte[] byCardNo = new byte[HCNetSDK.ACS_CARD_NO_LEN + 1];
                System.arraycopy(cfgBuf.byCardNo, 0, byCardNo, 0, HCNetSDK.NET_SDK_CALLBACK_STATUS_FAILED);
                String strCardNo = new String(byCardNo);

                System.out.println("GetFingerPrintCfgCallback NET_SDK_CALLBACK_STATUS_FAILED. ErrorCode:" + hCNetSDK.NET_DVR_GetLastError());

            }else{
                System.out.println("GetFingerPrintCfgCallback NET_SDK_CALLBACK_STATUS_PROCESSING.");
            }
        }
    }

    public class FRemoteConfigCallback implements HCNetSDK.FRemoteConfigCallback
    {
        // 获取指纹回调函数
        public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData)
        {
            GetFingerPrintCfgCallback(dwType, lpBuffer, dwBufLen, pUserData);
        }
    }
}



