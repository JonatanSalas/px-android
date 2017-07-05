package com.mercadopago.util;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by vaserber on 6/5/17.
 */

public class TrackingUtil {

    //Screen IDs
    public static final String SCREEN_ID_CHECKOUT = "/checkout_off/init";
    public static final String SCREEN_ID_PAYMENT_VAULT = "/checkout_off/payment_option";
    public static final String SCREEN_ID_PAYMENT_VAULT_TICKET = "/checkout_off/payment_option/ticket";
    public static final String SCREEN_ID_PAYMENT_VAULT_BANK_TRANSFER = "/checkout_off/payment_option/bank_transfer";
    public static final String SCREEN_ID_PAYMENT_VAULT_CARDS = "/checkout_off/payment_option/card";

    //Screen Names
    public static final String SCREEN_NAME_CHECKOUT = "Init checkout";
    public static final String SCREEN_NAME_PAYMENT_VAULT = "Payment method selection";
    public static final String SCREEN_NAME_PAYMENT_VAULT_TICKET = "Payment method selection Ticket";
    public static final String SCREEN_NAME_PAYMENT_VAULT_BANK_TRANSFER = "Payment method selection Bank Transfer";
    public static final String SCREEN_NAME_PAYMENT_VAULT_CARDS = "Payment method selection Cards";

    //Payment Vault Group Ids
    public static final String GROUP_TICKET = "ticket";
    public static final String GROUP_BANK_TRANSFER = "bank_transfer";
    public static final String GROUP_CARDS = "cards";
}
