package com.mercadopago.px_tracking.services;

import android.content.Context;

import com.mercadopago.px_tracking.model.EventTrackIntent;
import com.mercadopago.px_tracking.model.PaymentIntent;
import com.mercadopago.px_tracking.model.TrackingIntent;

/**
 * Created by vaserber on 7/3/17.
 */

public interface MPTrackingService {

    public void trackToken(TrackingIntent trackingIntent, Context context);
    public void trackPaymentId(PaymentIntent paymentIntent, Context context);
    public void trackEvent(EventTrackIntent eventTrackIntent, Context context);
}
