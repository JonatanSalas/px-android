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
