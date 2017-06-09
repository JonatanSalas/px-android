package com.mercadopago.px_tracking;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.px_tracking.listeners.TracksListener;
import com.mercadopago.px_tracking.model.ActionEvent;
import com.mercadopago.px_tracking.model.AppInformation;
import com.mercadopago.px_tracking.model.DeviceInfo;
import com.mercadopago.px_tracking.model.Event;
import com.mercadopago.px_tracking.model.EventTrackIntent;
import com.mercadopago.px_tracking.model.PaymentIntent;
import com.mercadopago.px_tracking.model.ScreenViewEvent;
import com.mercadopago.px_tracking.model.TrackingIntent;
import com.mercadopago.px_tracking.services.MPTrackingService;
import com.mercadopago.px_tracking.utils.JsonConverter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

/**
 * Created by vaserber on 6/5/17.
 */

public class MPTracker {

    private static MPTracker mMPTrackerInstance;

    private TracksListener mTracksListener;

    private String mFlavor;
    private String mPublicKey;
    private String mSdkVersion;
    private String mSiteId;
    private Context mContext;

    private static final String SDK_PLATFORM = "Android";
    private static final String SDK_TYPE = "native";

    private static final String NO_SCREEN = "NO_SCREEN";
    private static final String DEFAULT_SITE = "MLA";

    private Boolean trackerInitialized = false;

    protected MPTracker() {
    }

    synchronized public static MPTracker getInstance() {
        if (mMPTrackerInstance == null) {
            mMPTrackerInstance = new MPTracker();
        }
        return mMPTrackerInstance;
    }

    public void setTracksListener(TracksListener tracksListener) {
        this.mTracksListener = tracksListener;
    }

    private void trackScreenLaunchedListener(String screenName) {
        if (this.mTracksListener != null) {
            this.mTracksListener.onScreenLaunched(screenName);
        }
    }

    private void trackEventPerformedListener(Map<String, String> eventMap) {
        if (this.mTracksListener != null) {
            this.mTracksListener.onEventPerformed(eventMap);
        }
    }

    /**
     * @param screenName      The name of the screen
     * @param action          The action that generate an event. Cannot be {@code null}. if it's
     *                        null, the track will not be sent.
     * @param paymentId       The payment id of a payment method off. Cannot be {@code null}. if
     *                        it's null, the track will not be sent.
     * @param paymentMethodId The payment method id. Cannot be {@code null}. if it's null, the track
     *                        will not be sent.
     * @param status          The payment status. Cannot be {@code null}. if it's null, the track
     *                        will not be sent.
     * @param statusDetail    The payment status detail. Cannot be {@code null}. if it's null, the
     *                        track will not be sent.
     * @param typeId          The payment type id. It have to be a card type.
     * @param installments    The installments quantity that the payment be done.
     * @param issuerId        The bank that emit the card.
     */
    public void trackPayment(String screenName, String action, Long paymentId, String paymentMethodId, String status, String statusDetail, String typeId, Integer installments, Integer issuerId) {
        if (trackerInitialized) {

            if (!isCardPaymentType(typeId)) {
                PaymentIntent paymentIntent = new PaymentIntent(mPublicKey, paymentId.toString(), mFlavor, SDK_PLATFORM, SDK_TYPE, mSdkVersion, mSiteId);
                MPTrackingService.getInstance().trackPaymentId(paymentIntent, mContext);
            }

            // NEW 1.2.0: TracksListener
            Map<String, String> eventMap = new HashMap<>();
            eventMap.put("screen_name", screenName);
            eventMap.put("payment_id", "" + paymentId);
            eventMap.put("status", status);
            eventMap.put("status_detail", statusDetail);
            eventMap.put("type_id", typeId);
            eventMap.put("payment_method", paymentMethodId);
            eventMap.put("installments", "" + installments);
            eventMap.put("issuer_id", "" + issuerId);
            trackEventPerformedListener(eventMap);
        }
    }

    /**
     * @param token The card token id of a payment. Cannot be {@code null}. if it's null, the track
     *              will not be sent.
     */
    public void trackToken(String token) {
        if (trackerInitialized && !isEmpty(token)) {
            TrackingIntent trackingIntent = new TrackingIntent(mPublicKey, token, mFlavor, SDK_PLATFORM, SDK_TYPE, mSdkVersion, mSiteId);
            MPTrackingService.getInstance().trackToken(trackingIntent, mContext);
        }
    }

    /**
     * @param screenName The screen name where the event happens.
     * @param action     The action that generate an event. Cannot be {@code null}. if it's null,
     *                   the track will not be sent.
     * @param result     The result indicates if an action is success or failure. Cannot be {@code
     *                   null}. if it's null, the track will not be sent.
     * @param flavor     The flavor that the merchant has integrated. Cannot be {@code null}. if
     *                   it's null, the track will not be sent.
     * @param publicKey  The public key of the merchant. Cannot be {@code null}. if it's null, the
     *                   track will not be sent.
     * @param siteId     The country where the preference come. Cannot be {@code null}. if it's
     *                   null, the track will not be sent.
     * @param sdkVersion The Mercado Pago sdk version. Cannot be {@code null}. if it's null, the
     *                   track will not be sent.
     * @param context    Reference to Android Context. Cannot be {@code null}.
     */
    public void trackEvent(String screenName, String action, String result, String flavor, String publicKey, String siteId, String sdkVersion, Context context) {
        if (publicKey == null) {
            publicKey = mPublicKey;
        }
        if (siteId == null) {
            siteId = getSiteId();
        }
        if (sdkVersion == null) {
            sdkVersion = mSdkVersion;
        }
        if (context == null) {
            context = mContext;
        }

        initTracker(flavor, publicKey, siteId, sdkVersion, context);

        if (trackerInitialized) {
            // NEW 1.2.0: TracksListener
            Map<String, String> eventMap = new HashMap<>();
            if (screenName != null) {
                eventMap.put("screen_name", screenName);
            }
            if (action != null) {
                eventMap.put("action", action);
            }
            if (result != null) {
                eventMap.put("result", result);
            }
            trackEventPerformedListener(eventMap);
        }

    }

    /**
     * @param name       The screen name where an event happens.
     * @param flavor     The flavor that the merchant has integrated. Cannot be {@code null}. if
     *                   it's null, the track will not be sent.
     * @param publicKey  The public key of the merchant. Cannot be {@code null}. if it's null, the
     *                   track will not be sent.
     * @param siteId     The country where the preference come. Cannot be {@code null}. if it's
     *                   null, the track will not be sent.
     * @param sdkVersion The Mercado Pago sdk version. Cannot be {@code null}. if it's null, the
     *                   track will not be sent.
     * @param context    Reference to Android Context. Cannot be {@code null}.
     */
    public void trackScreen(String name, String flavor, String publicKey, String siteId, String sdkVersion, Context context) {
        if (siteId == null) {
            siteId = getSiteId();
        }

        initTracker(flavor, publicKey, siteId, sdkVersion, context);

        if (trackerInitialized) {
            // NEW 1.2.0: TracksListener
            trackScreenLaunchedListener(name);
        }
    }

    /**
     * This method tracks a list of events in one request
     *
     * @param clientId Id that identifies the client that is using the SDK
     * @param appInformation Info about this application and SDK integration
     * @param deviceInfo Info about the device that is using the app
     * @param events List of events to track
     * @param context Application context
     */
    public EventTrackIntent trackEventList(String clientId, AppInformation appInformation, DeviceInfo deviceInfo, List<Event> events, Context context) {
        EventTrackIntent eventTrackIntent = new EventTrackIntent(clientId, appInformation, deviceInfo, events);
        MPTrackingService.getInstance().trackEvent(eventTrackIntent, context);

        for (Event event: eventTrackIntent.getEvents()) {
            if (event.getType().equals(Event.TYPE_ACTION)) {
                ActionEvent actionEvent = (ActionEvent) event;
                Map<String, String> eventMap = createEventMap(actionEvent);
                trackEventPerformedListener(eventMap);
            } else if (event.getType().equals(Event.TYPE_SCREEN_VIEW)) {
                ScreenViewEvent screenViewEvent = (ScreenViewEvent) event;
                trackScreenLaunchedListener(screenViewEvent.getScreenName());
            }
        }
        return eventTrackIntent;
    }

    private Map<String, String> createEventMap(ActionEvent actionEvent) {
        Map<String, String> eventMap = new HashMap<>();

        String eventJson = JsonConverter.getInstance().toJson(actionEvent);
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> actionEventDataMap = new Gson().fromJson(eventJson, type);

        eventMap.putAll(actionEventDataMap);

        return eventMap;
    }


    /**
     * @param flavor     The flavor that the merchant has integrated. Cannot be {@code null}. if
     *                   it's null, the track will not be sent.
     * @param publicKey  The public key of the merchant. Cannot be {@code null}. if it's null, the
     *                   track will not be sent.
     * @param siteId     The country where the preference come. Cannot be {@code null}. if it's
     *                   null, the track will not be sent.
     * @param sdkVersion The Mercado Pago sdk version. Cannot be {@code null}. if it's null, the
     *                   track will not be sent.
     * @param context    Reference to Android Context. Cannot be {@code null}.
     */
    private void initTracker(String flavor, String publicKey, String siteId, String sdkVersion, Context context) {
        if (!isTrackerInitialized()) {
            if (areInitParametersValid(flavor, publicKey, siteId, sdkVersion, context)) {
                trackerInitialized = true;

                this.mFlavor = flavor;
                this.mPublicKey = publicKey;
                this.mSiteId = siteId;
                this.mSdkVersion = sdkVersion;
                this.mContext = context;

            }
        }
    }

    /**
     * Validate all init parameters
     *
     * @param flavor     The flavor that the merchant has integrated. Cannot be {@code null}. if
     *                   it's null, the track will not be sent.
     * @param publicKey  The public key of the merchant. Cannot be {@code null}. if it's null, the
     *                   track will not be sent.
     * @param siteId     The country where the preference come. Cannot be {@code null}. if it's
     *                   null, the track will not be sent.
     * @param sdkVersion The Mercado Pago sdk version. Cannot be {@code null}. if it's null, the
     *                   track will not be sent.
     * @param context    Reference to Android Context. Cannot be {@code null}.
     * @return True if all parameters are valid. False if any parameter is invalid
     */
    private boolean areInitParametersValid(String flavor, String publicKey, String siteId, String sdkVersion, Context context) {
        return !isEmpty(flavor) && !isEmpty(publicKey) && !isEmpty(sdkVersion) && !isEmpty(siteId) && context != null;
    }

    /**
     * Check if MPTracker is initialized
     *
     * @return True if is initialized. False if is not initialize.
     */
    private boolean isTrackerInitialized() {
        return this.mFlavor != null && this.mPublicKey != null && this.mSdkVersion != null && this.mSiteId != null && this.mContext != null;
    }

    /**
     * Get the set site
     *
     * @return The site that set the first track. if it is null return a site by default
     */
    private String getSiteId() {
        return mSiteId == null ? DEFAULT_SITE : mSiteId;
    }

    /**
     * Indicates if a payment was done by card or not
     *
     * @param paymentTypeId The payment type id of the payment tracked
     * @return True if it is a card payment. False if not a card payment.
     */
    private Boolean isCardPaymentType(String paymentTypeId) {
        return paymentTypeId.equals("credit_card") || paymentTypeId.equals("debit_card") || paymentTypeId.equals("prepaid_card");
    }
}
