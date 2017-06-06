package com.mercadopago.util;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by vaserber on 6/5/17.
 */

public class TrackingUtil {

    //Screen IDs
    public static final String SCREEN_ID_NO_SCREEN = "/";
    public static final String SCREEN_ID_CARD_FORM = "/CARD_FORM";
    public static final String SCREEN_ID_PAYMENT_METHODS = "/PAYMENT_METHODS";
    public static final String SCREEN_ID_REVIEW_AND_CONFIRM = "/REVIEW_AND_CONFIRM";
    public static final String SCREEN_ID_CONGRATS = "/CONGRATS";
    public static final String SCREEN_ID_INSTALLMENTS = "/INSTALLMENTS";

    @StringDef({SCREEN_ID_CARD_FORM, SCREEN_ID_PAYMENT_METHODS, SCREEN_ID_REVIEW_AND_CONFIRM,
            SCREEN_ID_CONGRATS, SCREEN_ID_INSTALLMENTS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScreenId {}

    //Screen Names
    public static final String SCREEN_NAME_NO_SCREEN = "No Screen";
    public static final String SCREEN_NAME_CARD_FORM = "Card Form";
    public static final String SCREEN_NAME_PAYMENT_METHODS = "Payment Methods";
    public static final String SCREEN_NAME_REVIEW_AND_CONFIRM = "Review and Confirm";
    public static final String SCREEN_NAME_CONGRATS = "Congrats";
    public static final String SCREEN_NAME_INSTALLMENTS = "Installments";

    @StringDef({SCREEN_NAME_CARD_FORM, SCREEN_NAME_PAYMENT_METHODS, SCREEN_NAME_REVIEW_AND_CONFIRM,
            SCREEN_NAME_CONGRATS, SCREEN_NAME_INSTALLMENTS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScreenName {}
}
