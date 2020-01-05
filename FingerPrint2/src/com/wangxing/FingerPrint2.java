package com.wangxing;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

public class FingerPrint2 {

    private static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;

    // NET_DVR_Login_V4参数声明
    private static NativeLong lUserID;//用户句柄
    private static HCNetSDK.NET_DVR_USER_LOGIN_INFO m_strLoginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();//设备登录信息
    private static HCNetSDK.NET_DVR_DEVICEINFO_V40 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();//设备信息
    private static String m_sDeviceIP;//已登录设备的IP地址
    private static String m_sUsername;//设备用户名
    private static String m_sPassword;//设备密码

    // NET_DVR_StartRemoteConfig参数声明
    private static HCNetSDK.NET_DVR_FINGERPRINT_COND m_strlpInBuffer = new HCNetSDK.NET_DVR_FINGERPRINT_COND(); //LpInBuffer
    private static String m_sCardNo; //关联卡的卡号
    private static NativeLong m_lGetFingerPrintCfgHandle;

    // NET_DVR_GetNextRemoteConfig函数参数
    private static HCNetSDK.NET_DVR_FINGERPRINT_RECORD m_strFingerPrintRecord = new HCNetSDK.NET_DVR_FINGERPRINT_RECORD();
    private static int m_strFingerPrintRecordLen;
    private static boolean m_bBreakFlag;
    private static NativeLong m_GetFingerPrintRecordStatus;

    // 指纹数据
    private static HCNetSDK.NET_DVR_FINGER_PRINT_CFG m_strFingerPrintCfg = new HCNetSDK.NET_DVR_FINGER_PRINT_CFG(); //指纹信息

    public static int main(String[] args) throws InterruptedException {
        // write your code here

        // 初始化SDK资源
        boolean rt = hCNetSDK.NET_DVR_Init();

        if (rt != true)
        {
            System.out.println("NET_DVR_Init(),初始化SDK资源失败，错误号:" + hCNetSDK.NET_DVR_GetLastError());
            return 1;
        }

        // 设置连接超时时间与重连功能
        rt = hCNetSDK.NET_DVR_SetConnectTime(2000, 1);
        if (rt != true)
        {
            System.out.println("NET_DVR_SetConnectTime(),设置连接超时失败，错误号:" + hCNetSDK.NET_DVR_GetLastError());
            hCNetSDK.NET_DVR_Cleanup();
            return 1;
        }
        rt = hCNetSDK.NET_DVR_SetReconnect(10000, true);
        if (rt != true)
        {
            System.out.println("NET_DVR_SetReconnect(),设置重连失败，错误号:" + hCNetSDK.NET_DVR_GetLastError());
            hCNetSDK.NET_DVR_Cleanup();
            return 1;
        }

        // 注册设备 LONG NET_DVR_Login_V40
        lUserID = new NativeLong(-1);
        m_sDeviceIP = "192.168.160.1"; //设备ip地址
        m_strLoginInfo.sDeviceAddress = new byte[HCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN];
        System.arraycopy(m_sDeviceIP.getBytes(), 0, m_strLoginInfo.sDeviceAddress, 0, m_sDeviceIP.length());

        m_sUsername = "admin";//设备用户名
        m_strLoginInfo.sUserName = new byte[HCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN];
        System.arraycopy(m_sUsername.getBytes(), 0, m_strLoginInfo.sUserName, 0, m_sUsername.length());

        m_sPassword = new String("password");//设备密码
        m_strLoginInfo.sPassword = new byte[HCNetSDK.NET_DVR_LOGIN_PASSWD_MAX_LEN];
        System.arraycopy(m_sPassword.getBytes(), 0, m_strLoginInfo.sPassword, 0, m_sPassword.length());

        m_strLoginInfo.wPort = (short)Integer.parseInt("8000"); // 设备端口号

        m_strLoginInfo.bUseAsynLogin = false; //是否异步登录：0- 否，1- 是

        lUserID = hCNetSDK.NET_DVR_Login_V40(m_strLoginInfo, m_strDeviceInfo);
        if (lUserID.longValue() == -1) {
            System.out.println("NET_DVR_Login_V40(),注册失败，错误号:" + hCNetSDK.NET_DVR_GetLastError());
            hCNetSDK.NET_DVR_Cleanup();
            return 1;
        } else {
            System.out.println("注册成功");
        }

        //启动远程配置。
        m_strlpInBuffer.dwSize = m_strlpInBuffer.size();
        m_strlpInBuffer.dwFingerprintNum = 1; //获取指纹数量
        m_sCardNo = new String("1");    //指纹关联的卡号
        m_strlpInBuffer.byCardNo = new byte[HCNetSDK.ACS_CARD_NO_LEN];
        System.arraycopy(m_sCardNo.getBytes(), 0, m_strlpInBuffer.byCardNo,0,m_sCardNo.length());
        m_strlpInBuffer.dwEnableReaderNo = 1;   //指纹读卡器编号
        m_strlpInBuffer.byFingerPrintID = 1; //指纹编号

        m_strlpInBuffer.write();
        Pointer lpInBuffer = m_strlpInBuffer.getPointer();

        //m_bGetFingerPrintCfgFinish = false;
        m_lGetFingerPrintCfgHandle = new NativeLong(-1);
        m_lGetFingerPrintCfgHandle = hCNetSDK.NET_DVR_StartRemoteConfig(
                lUserID,
                hCNetSDK.NET_DVR_GET_FINGERPRINT,
                lpInBuffer,
                m_strlpInBuffer.size(),
                null,
                null);
        if(m_lGetFingerPrintCfgHandle.longValue() == -1)
        {
            System.out.println("NET_DVR_StartRemoteConfig(),启动远程配置失败，错误号:" + hCNetSDK.NET_DVR_GetLastError());
            hCNetSDK.NET_DVR_Logout(lUserID);
            hCNetSDK.NET_DVR_Cleanup();
            return 1;
        }

        //获取指纹参数
        m_strFingerPrintRecordLen = m_strFingerPrintRecord.size();
        m_strFingerPrintRecord.write();
        Pointer lpOutBuff = m_strFingerPrintRecord.getPointer();
        m_bBreakFlag = true;
        m_GetFingerPrintRecordStatus = new NativeLong(-1);
        while (m_bBreakFlag)
        {
            m_GetFingerPrintRecordStatus = hCNetSDK.NET_DVR_GetNextRemoteConfig(m_lGetFingerPrintCfgHandle, lpOutBuff, m_strFingerPrintRecordLen);
            if(m_GetFingerPrintRecordStatus.longValue() == hCNetSDK.NET_SDK_GET_NEXT_STATUS_SUCCESS)
            {
                //成功读取到数据，处理完本次数据后需调用next
                System.out.println("NET_DVR_GetNextRemoteConfig NET_SDK_GET_NEXT_STATUS_SUCCESS, 成功读取到数据，处理完本次数据后需调用next");
                //数据信息
                m_strFingerPrintRecord.read();
                if(m_strFingerPrintRecord.dwSize != 0) {
                    System.out.println("NET_DVR_GetNextRemoteConfig NET_SDK_GET_NEXT_STATUS_SUCCESS, 取得指纹成功");
                    System.out.println("指纹数据：" + new String(m_strFingerPrintRecord.byFingerData));
                }else{
                    System.out.println("NET_DVR_GetNextRemoteConfig NET_SDK_GET_NEXT_STATUS_SUCCESS, 取得指纹失败");
                }
            }else if(m_GetFingerPrintRecordStatus.longValue() == hCNetSDK.NET_SDK_GET_NETX_STATUS_NEED_WAIT)
            {
                // 需等待设备发送数据，继续调用
                System.out.println("NET_DVR_GetNextRemoteConfig NET_SDK_GET_NEXT_STATUS_SUCCESS, 需等待设备发送数据，继续调用next");
            }else if(m_GetFingerPrintRecordStatus.longValue() == hCNetSDK.NET_SDK_GET_NEXT_STATUS_FINISH)
            {
                //数据全部取完
                System.out.println("NET_DVR_GetNextRemoteConfig NET_SDK_GET_NEXT_STATUS_FINISH, 数据全部取完，可调用stop");
                m_bBreakFlag = false;
                //数据信息
                m_strFingerPrintRecord.read();
                if(m_strFingerPrintRecord.dwSize != 0) {
                    System.out.println("NET_DVR_GetNextRemoteConfig NET_SDK_GET_NEXT_STATUS_SUCCESS, 取得指纹成功");
                    System.out.println("指纹数据：" + new String(m_strFingerPrintRecord.byFingerData));
                }else{
                    System.out.println("NET_DVR_GetNextRemoteConfig NET_SDK_GET_NEXT_STATUS_SUCCESS, 取得指纹失败");
                }
            }else if(m_GetFingerPrintRecordStatus.longValue() == hCNetSDK.NET_SDK_GET_NEXT_STATUS_FAILED)
            {
                // 出现异常
                System.out.println("NET_DVR_GetNextRemoteConfig NET_SDK_GET_NEXT_STATUS_FAILED, 出现异常，读取失败。可调用stop");
                m_bBreakFlag = false;
            }else
            {
                // 未知问题
                System.out.println("NET_DVR_GetNextRemoteConfig NET_SDK_GET_NEXT_STATUS_UNKOWN, 出现异常，读取失败。可调用stop");
                m_bBreakFlag = false;
            }
        }

        // 若获取卡参数完成则关闭长连接
        rt = hCNetSDK.NET_DVR_StopRemoteConfig(m_lGetFingerPrintCfgHandle);
        if (rt != true)
        {
            System.out.println("NET_DVR_StopRemoteConfig(),关闭远程配置失败，错误号:" + hCNetSDK.NET_DVR_GetLastError());
            hCNetSDK.NET_DVR_Logout(lUserID);
            hCNetSDK.NET_DVR_Cleanup();
            return 1;
        }

        // 注销
        rt =hCNetSDK.NET_DVR_Logout(lUserID);
        if (rt != true)
        {
            System.out.println("NET_DVR_Logout(),注销失败，错误号:" + hCNetSDK.NET_DVR_GetLastError());
            hCNetSDK.NET_DVR_Cleanup();
            return 1;
        }

        // 释放SDK资源
        rt = hCNetSDK.NET_DVR_Cleanup();
        if (rt != true)
        {
            System.out.println("NET_DVR_Cleanup(),释放SDK资源失败，错误号:" + hCNetSDK.NET_DVR_GetLastError());
            return 1;
        }
        return 0;
    }
}
