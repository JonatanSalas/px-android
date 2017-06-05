package com.mercadopago.px_tracking.model;

import java.sql.Timestamp;

/**
 * Created by vaserber on 6/5/17.
 */

public class ScreenViewEvent extends Event {

    private @ScreenId String screenId;
    private @ScreenName String screenName;

    public ScreenViewEvent() {
        super();
        setType(TYPE_SCREEN_VIEW);
        setTimestamp(new Timestamp(System.currentTimeMillis()));
    }

    public @ScreenName String getScreenName() {
        return screenName;
    }

    public void setScreenName(@ScreenName String screenName) {
        this.screenName = screenName;
    }

    public @ScreenId String getScreenId() {
        return screenId;
    }

    public void setScreenId(@ScreenId String screenId) {
        this.screenId = screenId;
    }
}
