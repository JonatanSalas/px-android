package com.mercadopago.px_tracking;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.mercadopago.px_tracking.listeners.TracksListener;
import com.mercadopago.px_tracking.mocks.MPMockedTrackingService;
import com.mercadopago.px_tracking.mocks.TrackingStaticMock;
import com.mercadopago.px_tracking.model.ActionEvent;
import com.mercadopago.px_tracking.model.AppInformation;
import com.mercadopago.px_tracking.model.DeviceInfo;
import com.mercadopago.px_tracking.model.ErrorEvent;
import com.mercadopago.px_tracking.model.Event;
import com.mercadopago.px_tracking.model.EventTrackIntent;
import com.mercadopago.px_tracking.model.ScreenViewEvent;
import com.mercadopago.px_tracking.model.StackTraceInfo;
import com.mercadopago.px_tracking.utils.JsonConverter;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MPTrackerTest {

    public static final String MOCKED_CLIENT_ID = "12345";
    public static final String MOCKED_CHECKOUT_VERSION = "3.0.0";
    public static final String MOCKED_PLATFORM = "native/android";
    public static final String MOCKED_PUBLIC_KEY = "public_key";
    public static final String MOCKED_MODEL = "model";
    public static final String MOCKED_OS = "android";
    public static final String MOCKED_RESOLUTION = "resolution";
    public static final String MOCKED_SCREEN_SIZE = "size";
    public static final String MOCKED_SYSTEM_VERSION = "system_version";
    public static final String MOCKED_ACTION = "action";
    public static final String MOCKED_CATEGORY = "category";
    public static final String MOCKED_LABEL = "label";
    public static final String MOCKED_VALUE = "value";

    public static final String MOCKED_SCREEN_ID_1 = "id_1";
    public static final String MOCKED_SCREEN_NAME_1 = "name_1";
    public static final String MOCKED_ERROR_CLASS_1 = "class_1";
    public static final String MOCKED_ERROR_MESSAGE_1 = "message_1";

    public static final String ACTION_EVENT_KEY_ACTION = "action";
    public static final String ACTION_EVENT_KEY_CATEGORY = "category";
    public static final String ACTION_EVENT_KEY_LABEL = "label";
    public static final String ACTION_EVENT_KEY_VALUE = "value";
    public static final String ACTION_EVENT_KEY_SCREEN_ID = "screen_id";
    public static final String ACTION_EVENT_KEY_SCREEN_NAME = "screen_name";


    @Test
    public void sendScreenViewEventTrack() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        String clientId = MOCKED_CLIENT_ID;

        AppInformation appInformation = new AppInformation.Builder()
                .setCheckoutVersion(MOCKED_CHECKOUT_VERSION)
                .setPlatform(MOCKED_PLATFORM)
                .setPublicKey(MOCKED_PUBLIC_KEY)
                .build();

        DeviceInfo deviceInfo = new DeviceInfo.Builder()
                .setModel(MOCKED_MODEL)
                .setOS(MOCKED_OS)
                .setResolution(MOCKED_RESOLUTION)
                .setScreenSize(MOCKED_SCREEN_SIZE)
                .setSystemVersion(MOCKED_SYSTEM_VERSION)
                .build();

        List<Event> events = new ArrayList<>();

        final ScreenViewEvent screenViewEvent = new ScreenViewEvent.Builder()
            .setScreenId(MOCKED_SCREEN_ID_1)
            .setScreenName(MOCKED_SCREEN_NAME_1)
            .build();

        events.add(screenViewEvent);

        MPTracker.getInstance().setMPTrackingService(new MPMockedTrackingService());

        MPTracker.getInstance().setTracksListener(new TracksListener() {
            @Override
            public void onScreenLaunched(String screenName) {
                assertEquals(screenViewEvent.getScreenName(), screenName);
            }

            @Override
            public void onEventPerformed(Map<String, String> event) {

            }
        });
        EventTrackIntent eventTrackIntent = MPTracker.getInstance().trackEventList(MOCKED_CLIENT_ID, appInformation, deviceInfo, events, appContext);

        assertEquals(eventTrackIntent.getApplication(), appInformation);
        assertEquals(eventTrackIntent.getDevice(), deviceInfo);
        assertEquals(eventTrackIntent.getClientId(), clientId);
        List<Event> sentList = eventTrackIntent.getEvents();
        assertTrue(sentList.size() == 1);
    }

    @Test
    public void sendActionEventTrack() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        String clientId = MOCKED_CLIENT_ID;

        AppInformation appInformation = new AppInformation.Builder()
                .setCheckoutVersion(MOCKED_CHECKOUT_VERSION)
                .setPlatform(MOCKED_PLATFORM)
                .setPublicKey(MOCKED_PUBLIC_KEY)
                .build();

        DeviceInfo deviceInfo = new DeviceInfo.Builder()
                .setModel(MOCKED_MODEL)
                .setOS(MOCKED_OS)
                .setResolution(MOCKED_RESOLUTION)
                .setScreenSize(MOCKED_SCREEN_SIZE)
                .setSystemVersion(MOCKED_SYSTEM_VERSION)
                .build();

        List<Event> events = new ArrayList<>();

        ActionEvent actionEvent = new ActionEvent.Builder()
                .setAction(MOCKED_ACTION)
                .setCategory(MOCKED_CATEGORY)
                .setLabel(MOCKED_LABEL)
                .setValue(MOCKED_VALUE)
                .setScreenId(MOCKED_SCREEN_ID_1)
                .setScreenName(MOCKED_SCREEN_NAME_1)
                .build();

        events.add(actionEvent);

        MPTracker.getInstance().setMPTrackingService(new MPMockedTrackingService());

        MPTracker.getInstance().setTracksListener(new TracksListener() {
            @Override
            public void onScreenLaunched(String screenName) {

            }

            @Override
            public void onEventPerformed(Map<String, String> event) {
                assertEquals(event.get(ACTION_EVENT_KEY_ACTION), MOCKED_ACTION);
                assertEquals(event.get(ACTION_EVENT_KEY_CATEGORY), MOCKED_CATEGORY);
                assertEquals(event.get(ACTION_EVENT_KEY_LABEL), MOCKED_LABEL);
                assertEquals(event.get(ACTION_EVENT_KEY_VALUE), MOCKED_VALUE);
                assertEquals(event.get(ACTION_EVENT_KEY_SCREEN_ID), MOCKED_SCREEN_ID_1);
                assertEquals(event.get(ACTION_EVENT_KEY_SCREEN_NAME), MOCKED_SCREEN_NAME_1);
            }
        });

        EventTrackIntent eventTrackIntent = MPTracker.getInstance().trackEventList(MOCKED_CLIENT_ID, appInformation, deviceInfo, events, appContext);

        assertEquals(eventTrackIntent.getApplication(), appInformation);
        assertEquals(eventTrackIntent.getDevice(), deviceInfo);
        assertEquals(eventTrackIntent.getClientId(), clientId);
        List<Event> sentList = eventTrackIntent.getEvents();
        assertTrue(sentList.size() == 1);
    }

    @Test
    public void sendErrorEventTrack() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        String clientId = MOCKED_CLIENT_ID;

        AppInformation appInformation = new AppInformation.Builder()
                .setCheckoutVersion(MOCKED_CHECKOUT_VERSION)
                .setPlatform(MOCKED_PLATFORM)
                .setPublicKey(MOCKED_PUBLIC_KEY)
                .build();

        DeviceInfo deviceInfo = new DeviceInfo.Builder()
                .setModel(MOCKED_MODEL)
                .setOS(MOCKED_OS)
                .setResolution(MOCKED_RESOLUTION)
                .setScreenSize(MOCKED_SCREEN_SIZE)
                .setSystemVersion(MOCKED_SYSTEM_VERSION)
                .build();

        List<Event> events = new ArrayList<>();

        ErrorEvent errorEvent = new ErrorEvent.Builder()
                .setErrorClass(MOCKED_ERROR_CLASS_1)
                .setErrorMessage(MOCKED_ERROR_MESSAGE_1)
                .setStackTraceList(new ArrayList<StackTraceInfo>())
                .build();

        events.add(errorEvent);

        MPTracker.getInstance().setMPTrackingService(new MPMockedTrackingService());

        EventTrackIntent eventTrackIntent = MPTracker.getInstance().trackEventList(MOCKED_CLIENT_ID, appInformation, deviceInfo, events, appContext);

        assertEquals(eventTrackIntent.getApplication(), appInformation);
        assertEquals(eventTrackIntent.getDevice(), deviceInfo);
        assertEquals(eventTrackIntent.getClientId(), clientId);
        List<Event> sentList = eventTrackIntent.getEvents();
        assertTrue(sentList.size() == 1);
    }

    @Test
    public void  sendMultipleEventsTrack() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        String clientId = MOCKED_CLIENT_ID;

        AppInformation appInformation = new AppInformation.Builder()
                .setCheckoutVersion(MOCKED_CHECKOUT_VERSION)
                .setPlatform(MOCKED_PLATFORM)
                .setPublicKey(MOCKED_PUBLIC_KEY)
                .build();

        DeviceInfo deviceInfo = new DeviceInfo.Builder()
                .setModel(MOCKED_MODEL)
                .setOS(MOCKED_OS)
                .setResolution(MOCKED_RESOLUTION)
                .setScreenSize(MOCKED_SCREEN_SIZE)
                .setSystemVersion(MOCKED_SYSTEM_VERSION)
                .build();

        List<Event> events = new ArrayList<>();

        ScreenViewEvent screenViewEvent = new ScreenViewEvent.Builder()
                .setScreenId(MOCKED_SCREEN_ID_1)
                .setScreenName(MOCKED_SCREEN_NAME_1)
                .build();

        ActionEvent actionEvent = new ActionEvent.Builder()
                .setAction(MOCKED_ACTION)
                .setCategory(MOCKED_CATEGORY)
                .setLabel(MOCKED_LABEL)
                .setValue(MOCKED_VALUE)
                .setScreenId(MOCKED_SCREEN_ID_1)
                .setScreenName(MOCKED_SCREEN_NAME_1)
                .build();

        ErrorEvent errorEvent = new ErrorEvent.Builder()
                .setErrorClass(MOCKED_ERROR_CLASS_1)
                .setErrorMessage(MOCKED_ERROR_MESSAGE_1)
                .setStackTraceList(new ArrayList<StackTraceInfo>())
                .build();

        events.add(screenViewEvent);
        events.add(actionEvent);
        events.add(errorEvent);

        MPTracker.getInstance().setMPTrackingService(new MPMockedTrackingService());

        EventTrackIntent eventTrackIntent = MPTracker.getInstance().trackEventList(MOCKED_CLIENT_ID, appInformation, deviceInfo, events, appContext);

        assertEquals(eventTrackIntent.getApplication(), appInformation);
        assertEquals(eventTrackIntent.getDevice(), deviceInfo);
        assertEquals(eventTrackIntent.getClientId(), clientId);
        List<Event> sentList = eventTrackIntent.getEvents();
        assertTrue(sentList.size() == 3);
    }
}
