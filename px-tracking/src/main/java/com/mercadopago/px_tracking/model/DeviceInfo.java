package com.mercadopago.px_tracking.model;

/**
 * Created by vaserber on 6/5/17.
 */

public class DeviceInfo {

    private String model;
    private String os;
    private String systemVersion;
    private String resolution;
    private String screenSize;

    private DeviceInfo(Builder builder) {
        this.model = builder.model;
        this.os = builder.os;
        this.systemVersion = builder.systemVersion;
        this.resolution = builder.resolution;
        this.screenSize = builder.screenSize;
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

    public String getScreenSize() {
        return screenSize;
    }

    public static class Builder {

        private String model;
        private String os;
        private String systemVersion;
        private String resolution;
        private String screenSize;

        public Builder setModel(String model) {
            this.model = model;
            return this;
        }

        public Builder setOS(String os) {
            this.os = os;
            return this;
        }

        public Builder setSystemVersion(String systemVersion) {
            this.systemVersion = systemVersion;
            return this;
        }

        public Builder setResolution(String resolution) {
            this.resolution = resolution;
            return this;
        }

        public Builder setScreenSize(String screenSize) {
            this.screenSize = screenSize;
            return this;
        }

        public DeviceInfo build() {
            return new DeviceInfo(this);
        }
    }
}
