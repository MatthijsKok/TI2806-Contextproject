package nl.tudelft.ewi.ds.bankchain.activities;


import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import nl.tudelft.ewi.ds.bankchain.R;
import static junit.framework.Assert.assertEquals;


/**
 * @author Isha Dijcks
 */

@RunWith(AndroidJUnit4.class)
public class RecentTransactionsActivityTest {

    private static final String PACKAGE_NAME = "nl.tudelft.ewi.ds.bankchain";
    /* Instantiate an IntentsTestRule object. */
    @Rule
    public ActivityTestRule<RecentTransactionsActivity> activityRule =
            new ActivityTestRule<>(RecentTransactionsActivity.class);

    @Before
    public void unlockScreen() {
        final RecentTransactionsActivity activity = activityRule.getActivity();
        activity.runOnUiThread(() -> {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        });
    }

    @Test
    @UiThreadTest
    public void verifyUpdateNoTransactionsDisplay() {
        // Clicks a button to send an intent to another activity

        activityRule.getActivity().updateNoTransactionsDisplay("Error");
        // Check that the text was changed.
        TextView textView = (TextView) activityRule.getActivity().findViewById(R.id.recentTransactionsError);
        assertEquals("Display should have updated to 'Error'", "Error", textView.getText());

        activityRule.getActivity().updateNoTransactionsDisplay("");
        assertEquals("Display should have updated to ''", "", textView.getText());
    }


}
