package com.prismsoftworks.openweatherapitest;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.LinearLayout;

import com.prismsoftworks.openweatherapitest.fragments.MapFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentationTest {
    private MainActivity activity;
    private View viewById;

    // Start MainActivity before each test
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup() {
        activity = activityRule.getActivity();
    }

    @Test
    public void didMainActivityLaunch() {
        MainActivity activity = activityRule.getActivity();
        assertEquals("MainActivity", activity.getLocalClassName());
    }

    @Test
    public void mainContainerExists() {
        viewById = activity.findViewById(R.id.mainContainer);
        assertNotNull(viewById);
        assertTrue(viewById instanceof LinearLayout);
    }

    @Test
    public void mainContainerIsVertical() {
        viewById = activity.findViewById(R.id.mainContainer);
        LinearLayout linearLayout = (LinearLayout) viewById;
        int orientation = linearLayout.getOrientation();
        assertEquals(orientation, LinearLayout.VERTICAL);
    }

    @Test
    public void mapFragmentExists() {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(MainActivity.MAP_FRAGMENT_TAG);

        assertNotNull(fragment);
        assertTrue(fragment instanceof MapFragment);
    }

    @Test
    public void bottomContainerExists() {
        viewById = activity.findViewById(R.id.bottomContainer);
        assertNotNull(viewById);
        assertTrue(viewById instanceof LinearLayout);
    }

    @Test
    public void bottomContainerIsVertical() {
        viewById = activity.findViewById(R.id.bottomContainer);
        LinearLayout linearLayout = (LinearLayout) viewById;
        int orientation = linearLayout.getOrientation();
        assertEquals(orientation, LinearLayout.VERTICAL);
    }
}
