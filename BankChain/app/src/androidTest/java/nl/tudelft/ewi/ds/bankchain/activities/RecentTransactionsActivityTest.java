package nl.tudelft.ewi.ds.bankchain.activities;


import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.ewi.ds.bankchain.TestRunner;
import nl.tudelft.ewi.ds.bankchain.bank.Transaction;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import nl.tudelft.ewi.ds.bankchain.R;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


/**
 * @author Isha Dijcks
 */
@RunWith(AndroidJUnit4.class)
//TRAVIS @Ignore
public class RecentTransactionsActivityTest extends TestRunner {

    /* Instantiate an IntentsTestRule object. */
    @Rule
    public ActivityTestRule<RecentTransactionsActivity> activityRule =
            new ActivityTestRule<>(RecentTransactionsActivity.class);

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

    @Test
    @UiThreadTest
    public void verifyNullTransactions() {
        activityRule.getActivity().showRecentTransactions(null);
        TextView textView = (TextView) activityRule.getActivity().findViewById(R.id.recentTransactionsError);
        assertEquals("Display should have updated to 'Could not retrieve transactions.'", "Could not retrieve transactions.", textView.getText());
    }

    @Test
    @UiThreadTest
    public void verifyNoTransactions() {
        List<? extends Transaction> emptyList = new ArrayList<>();
        activityRule.getActivity().showRecentTransactions(emptyList);
        // Check that the text was changed.
        TextView textView = (TextView) activityRule.getActivity().findViewById(R.id.recentTransactionsError);
        assertEquals("Display should have updated to 'No transactions have been made yet.'", "No transactions have been made yet.", textView.getText());
    }

    @Test
    public void onBackPressedTest() {
        activityRule.getActivity().onBackPressed();
        assertTrue(activityRule.getActivity().isFinishing());
    }

    @Test
    public void onBackPressedMenuItemTest() {
        onView(withContentDescription("Navigate up")).perform(click());
        assertTrue(activityRule.getActivity().isFinishing());
    }

}
