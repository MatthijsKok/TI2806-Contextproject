package nl.tudelft.ewi.ds.bankchain;

import android.app.Activity;
import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import nl.tudelft.ewi.ds.bankchain.activities.RecentTransactionsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
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

    private RecentTransactionsActivity activity = activityRule.getActivity();
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
