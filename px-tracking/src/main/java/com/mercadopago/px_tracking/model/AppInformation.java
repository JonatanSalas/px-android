package com.mercadopago.px_tracking.model;

/**
 * Created by vaserber on 6/5/17.
 */

public class AppInformation {

    private String publicKey;
    private String checkoutVersion;
    private String platform;

    public AppInformation(String publicKey, String checkoutVersion, String platform) {
        this.publicKey = publicKey;
        this.checkoutVersion = checkoutVersion;
        this.platform = platform;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getCheckoutVersion() {
        return checkoutVersion;
    }

    public void setCheckoutVersion(String checkoutVersion) {
        this.checkoutVersion = checkoutVersion;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
