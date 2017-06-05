package com.mercadopago.px_tracking.model;

import java.sql.Timestamp;

/**
 * Created by vaserber on 6/5/17.
 */

public class ActionEvent extends Event {

    private @ScreenId String screenId;
    private @ScreenName String screenName;
    private String action;
    private String category;
    private String label;
    private String value;

    public ActionEvent() {
        super();
        setType(TYPE_ACTION);
        setTimestamp(new Timestamp(System.currentTimeMillis()));
    }

    public @ScreenId String getScreenId() {
        return screenId;
    }

    public void setScreenId(@ScreenId String screenId) {
        this.screenId = screenId;
    }

    public @ScreenName String getScreenName() {
        return screenName;
    }

    public void setScreenName(@ScreenName String screenName) {
        this.screenName = screenName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
