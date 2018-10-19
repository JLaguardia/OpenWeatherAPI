package com.prismsoftworks.openweatherapitest;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ApplicationInstrumentationTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.prismsoftworks.openweatherapitest", appContext.getPackageName());
    }
}