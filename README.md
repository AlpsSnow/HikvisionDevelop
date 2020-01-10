# HikvisionDevelop
海康威视二次开发

海康威视开放平台提供针对两类产品的开发：  
1. 软件产品  
利用海康平台统一控制海康设备的获取和下发参数等处理。
2. 硬件产品  
通过设备网络SDK开发，基于设备私有网络通信协议开发的，为嵌入式网络硬盘录像机、 NVR、视频服务器、网络摄像机、网络球机、解码器、报警主机、网络存储等网络产品服务的配套模块，用于远程访问和控制设备软件的二次开发。一句话，通过SDK开发，我们可以控制私有网络中的海康设备参数获取和下发等处理。

### 设备网络SDK开发
1. [设备网络SDK下载](https://open.hikvision.com/download/5cda567cf47ae80dd41a54b3?type=10)
2. 根据Demo说明文件`SDK库文件拷贝到该目录下.txt`文件设置后，运行Demo示例。  
Java的Demo遇到坑：  
2.1 说明文件中没有，`AudioRender.dll`库也必须拷贝，否则回报`PlayCtrl`找不到。  
2.2 `PlayCtrl`库loadLibrary的时候需要制定后缀`.dll`否则报`PlayCtrl`找不到。
```java
PlayCtrl INSTANCE = (PlayCtrl) Native.loadLibrary("D:\\haikangweishi\\SDK\\PlayCtrl.dll",PlayCtrl.class);
```

### 关于本次开发
#### 技能
本次主要开发指纹信息取得和下发功能。  

#### 参考式样书：  
1. 件产品>门禁产品>人员管理 : https://open.hikvision.com/docs/858d37e41ae8966c6c6c378de2612293

2. 设备网络SDK编程指南（明眸）.pdf

#### 设计
由于参考的两份式样书中对于指纹信息取得的流程有差异，因此开发两个demo进行尝试。

1. 件产品>门禁产品>人员管理：通过回调函数的方式取得指纹信息
Demo1：FingerPrint1

2. 设备网络SDK编程指南（明眸）.pdf ：不通过回调方式，直接引入新函数NET_DVR_GetNextRemoteConfig，逐条取得指纹信息。
Demo2：FingerPrint2

#### DS-K1F820-F 指纹录入仪开发
1. 指纹图像采集（指纹图像数据，数据长度为图像高度 X 图像宽度，最大不超过 90KB）  
Demo3：FingerPrint3

2. 录入指纹并获取指纹模板（数据长度为 512 字节）  
Demo4: FingerPrint4  


