package com.mercadopago.px_tracking;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.mercadopago.px_tracking.mocks.TrackingStaticMock;
import com.mercadopago.px_tracking.model.AppInformation;
import com.mercadopago.px_tracking.model.DeviceInfo;
import com.mercadopago.px_tracking.model.Event;
import com.mercadopago.px_tracking.model.EventTrackIntent;
import com.mercadopago.px_tracking.model.ScreenViewEvent;
import com.mercadopago.px_tracking.utils.JsonConverter;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
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

    public static final String MOCKED_SCREEN_ID_1 = "id_1";
    public static final String MOCKED_SCREEN_NAME_1 = "name_1";

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.mercadopago.px_tracking.test", appContext.getPackageName());
    }

    @Test
    public void sendEventTrack() {
        // Context of the app under test.
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

        events.add(screenViewEvent);

        EventTrackIntent eventTrackIntent = MPTracker.getInstance().trackEventList(MOCKED_CLIENT_ID, appInformation, deviceInfo, events, appContext);

        assertEquals(eventTrackIntent.getApplication(), appInformation);
        assertEquals(eventTrackIntent.getDevice(), deviceInfo);
        assertEquals(eventTrackIntent.getClientId(), clientId);
    }
}
