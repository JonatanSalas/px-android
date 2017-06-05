package com.mercadopago.px_tracking.services;

import com.mercadopago.px_tracking.model.EventTrackIntent;
import com.mercadopago.px_tracking.model.PaymentIntent;
import com.mercadopago.px_tracking.model.TrackingIntent;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by vaserber on 6/5/17.
 */

public interface TrackingService {

    public static final String TRACKING_SERVICE_VERSION = "1";

    @POST("/{URI}/checkout/tracking")
    Call<Void> trackToken(@Body TrackingIntent body, @Path(value = "URI") String URI);

    @POST("/{URI}/checkout/tracking/off")
    Call<Void> trackPaymentId(@Body PaymentIntent body, @Path(value = "URI") String URI);

    @Headers("Accept-Version:" + TRACKING_SERVICE_VERSION)
    @POST("/{URI}/checkout/tracking/events")
    Call<Void> trackEvents(@Body EventTrackIntent body, @Path(value = "URI") String URI);
}
