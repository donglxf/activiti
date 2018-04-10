package com.ht.commonactivity.common;

import java.io.Serializable;

public class RpcSenceInfo implements Serializable {

    private String senceCode;
    private String senceVersion;


    public String getSenceCode() {
        return senceCode;
    }

    public void setSenceCode(String senceCode) {
        this.senceCode = senceCode;
    }

    public String getSenceVersion() {
        return senceVersion;
    }

    public void setSenceVersion(String senceVersion) {
        this.senceVersion = senceVersion;
    }
}
