package com.wangxing.fingerprint;

import com.sun.jna.win32.StdCallLibrary.StdCallCallback;

public interface FpCallback extends StdCallCallback {

	public void FpMessageHandler(int enMsgType, FPMsg pMsgData);
}
