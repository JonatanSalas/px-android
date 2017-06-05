package com.mercadopago.px_tracking.model;

/**
 * Created by vaserber on 6/5/17.
 */

public class DeviceInfo {

    private String model;
    private String os;
    private String systemVersion;
    private String resolution;

    public DeviceInfo(String model, String os, String systemVersion, String resolution) {
        this.model = model;
        this.os = os;
        this.systemVersion = systemVersion;
        this.resolution = resolution;
    }

    public String getModel() {
        return model;
    }

    public String getOs() {
        return os;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public String getResolution() {
        return resolution;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}
