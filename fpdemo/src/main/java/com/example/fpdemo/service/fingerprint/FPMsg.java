package com.example.fpdemo.service.fingerprint;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class FPMsg extends Structure {

    public int dwArg1;
    public int dwArg2;
    public Pointer pbyImage;
    //结构体传指针
    public static class ByReference extends FPMsg implements Structure.ByReference { }
    //结构体传值
    public static class ByValue extends FPMsg implements Structure.ByValue{ }
}