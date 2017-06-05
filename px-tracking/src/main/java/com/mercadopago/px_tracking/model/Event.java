package com.mercadopago.px_tracking.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.sql.Timestamp;

/**
 * Created by vaserber on 6/5/17.
 */

public abstract class Event {

    //Event Types
    public static final String TYPE_SCREEN_VIEW = "screenview";
    public static final String TYPE_ACTION = "action";
    public static final String TYPE_ERROR = "error";

    @StringDef({TYPE_SCREEN_VIEW, TYPE_ACTION, TYPE_ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EventType {}

    //Screen IDs
    public static final String SCREEN_ID_CARD_FORM = "/CARD_FORM";
    public static final String SCREEN_ID_PAYMENT_METHODS = "/PAYMENT_METHODS";
    public static final String SCREEN_ID_INSTALLMENTS = "/INSTALLMENTS";

    @StringDef({SCREEN_ID_CARD_FORM, SCREEN_ID_PAYMENT_METHODS, SCREEN_ID_INSTALLMENTS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScreenId {}

    //Screen Names
    public static final String SCREEN_NAME_CARD_FORM = "Card Form";
    public static final String SCREEN_NAME_PAYMENT_METHODS = "Payment Methods";
    public static final String SCREEN_NAME_INSTALLMENTS = "Installments";

    @StringDef({SCREEN_NAME_CARD_FORM, SCREEN_NAME_PAYMENT_METHODS, SCREEN_NAME_INSTALLMENTS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScreenName {}


    private Timestamp timestamp;
    private @EventType String type;


    public Timestamp getTimestamp() {
        return timestamp;
    }

    protected void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public @EventType String getType() {
        return type;
    }

    protected void setType(@EventType String type) {
        this.type = type;
    }
}
