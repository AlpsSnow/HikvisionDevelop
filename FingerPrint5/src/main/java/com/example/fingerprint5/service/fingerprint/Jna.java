package com.example.fingerprint5.service.fingerprint;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface Jna extends StdCallLibrary
{
    /** @func   : FPModule_OpenDevice
     *  @brief  : 连接设备
     *  @param  : None
     *  @return : 0->连接成功 1->通信失败
     */
    public abstract int FPModule_OpenDevice();


    /** @func   : FPModule_CloseDevice
     *  @brief  : 断开设备
     *  @param  : None
     *  @return : 0->断开成功 1->通信失败
     */
    public abstract int FPModule_CloseDevice();


    /** @func   : FPModule_DetectFinger
     *  @brief  : 检测指纹输入状态
     *  @param  : pdwFpstatus[out] -> 0:无指纹输入  1:有指纹输入
     *  @return : 0->执行成功 1->通信失败
     */
    public abstract int FPModule_DetectFinger(IntByReference pdwFpstatus);


    /** @func   : FPModule_CaptureImage
     *  @brief  : 采集指纹图像
     *  @param  : pbyImageData[out] -> 指纹图像数据（数据长度为 图像宽度 x 图像高度）
    pdwWidth[out]     -> 指纹图像宽度
    pdwHeight[out]    -> 指纹图像高度
     *  @return : 0->执行成功 1->通信失败
     */
    public abstract int FPModule_CaptureImage(byte[]pbyImageData, IntByReference pdwWidth, IntByReference pdwHeight);


    /** @func   : FPModule_SetTimeout
     *  @brief  : 设置采集超时时间
     *  @param  : dwSecond[in] -> 超时时间(单位：秒) 可设置值：1秒至60秒
     *  @return : 0->执行成功 1->通信失败
     */
    public abstract int FPModule_SetTimeout(int dwSecond);


    /** @func   : FPModule_GetTimeout
     *  @brief  : 获取采集超时时间
     *  @param  : pdwSecond[out] -> 超时时间 单位：秒
     *  @return : 0->执行成功 1->通信失败
     */
    public abstract int FPModule_GetTimeout(IntByReference pdwSecond);

    /** @func   : FPModule_SetCollectTimes
     *  @brief  : 设置采集次数
     *  @param  : dwTimes[in] -> 0~4,0默认模式（2~4次），1~3采集次数
     *  @return : 0->执行成功 1->通信失败
     */
    public abstract int FPModule_SetCollectTimes(int dwTimes);


    /** @func   : FPModule_GetCollectTimes
     *  @brief  : 获取采集次数
     *  @param  : pdwTimes[out] -> 采集次数
     *  @return : 0->执行成功 1->通信失败
     */
    public abstract int FPModule_GetCollectTimes(IntByReference pdwTimes);
    /** @func   : FPModule_InstallMessageHandler
     *  @brief  : 设置消息回调函数
     *  @param  : msgHandler[in] -> 消息处理函数
     *  @return : 0->执行成功
     */
    public abstract int FPModule_InstallMessageHandler(FpCallback msgHandler);

    /** @func   : FPModule_FpEnroll
     *  @brief  : 录入指纹
     *  @param  : pbyFpTemplate[out] -> 指纹模板(512字节)
     *  @return : 0->执行成功 1->通信失败 2->采集超时 3->录入失败
     */
    public abstract int FPModule_FpEnroll(byte[]pbyFpTemplate);


    /** @func   : FPModule_GetQuality
     *  @brief  : 获取指纹模板质量分数
     *  @param  : pbyFpTemplate[in] -> 指纹模板(512字节)
     *  @return : 指纹模板分数(0~100) 分数越高，表示模板的质量越好
     */
    public abstract int FPModule_GetQuality(byte[]pbyFpTemplate);


    /** @func   : FPModule_GetDeviceInfo
     *  @brief  : 获取指纹采集仪版本信息
     *  @param  : pbyDeviceInfo[out] -> 指纹采集仪版本信息(64字节)
     *  @return : 0->执行成功 1->通信失败
     */
    public abstract int FPModule_GetDeviceInfo(byte[]pbyDeviceInfo);


    /** @func   : FPModule_GetSDKVersion
     *  @brief  : 获取指纹采集仪SDK版本信息
     *  @param  : pbySDKVersion[out] -> 指纹采集仪SDK版本信息(64字节)
     *  @return : 0->执行成功
     */
    public abstract int FPModule_GetSDKVersion(byte[]pbySDKVersion);

    /** @func   : FPModule_MatchTemplate
     *  @brief  : 比对两枚指纹模板
     *  @param  : pbyFpTemplate1[in] -> 指纹模板1(512字节)
    pbyFpTemplate2[in] -> 指纹模板2(512字节)
    dwSecurityLevel[in] -> 安全等级（1~5）
     *  @return : 0->比对成功 6->比对失败 4->参数错误
     */
    public abstract int FPModule_MatchTemplate(byte[]pbyFpTemplate1, byte[]pbyFpTemplate2, int dwSecurityLevel);

}
