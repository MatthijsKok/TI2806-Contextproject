package nl.tudelft.ewi.ds.bankchain;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import nl.tudelft.ewi.ds.bankchain.activities.SettingsActivity;

import static junit.framework.Assert.assertTrue;


/**
 * @author Isha Dijcks
 */

@RunWith(AndroidJUnit4.class)
//TRAVIS @Ignore
public class SettingsActivityTest {
    @Rule
    public ActivityTestRule<SettingsActivity> activityRule =
            new ActivityTestRule<>(SettingsActivity.class);

    @Test
    public void onBackPressedTest() {
        activityRule.getActivity().onBackPressed();
        assertTrue(activityRule.getActivity().isFinishing());
    }


}
