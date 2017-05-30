package nl.tudelft.ewi.ds.bankchain.activities;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import nl.tudelft.ewi.ds.bankchain.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

/**
 * @author Isha Dijcks
 */

@RunWith(AndroidJUnit4.class)
@Ignore
public class MainActivityTest {

    private static final String PACKAGE_NAME = "nl.tudelft.ewi.ds.bankchain";

    /* Instantiate an IntentsTestRule object. */
    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void unlockScreen() {

        final MainActivity activity = activityRule.getActivity();
        Runnable wakeUpDevice = () -> activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Test
    public void verifyOpenRecentTransactionsActivity() {
        // Clicks a button to send an intent to another activity
        onView(ViewMatchers.withId(R.id.recentTransactionActivityButton)).perform(click());
        // Verifies that the DisplayMessageActivity received an intent
        // with the correct package name and message.
        intended(allOf(
                hasComponent(hasShortClassName(".activities.RecentTransactionsActivity")),
                toPackage(PACKAGE_NAME)));
    }

    @Test
    public void verifyOpenSettingsActivity() {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText("Settings")).perform(click());
        intended(allOf(
                hasComponent(hasShortClassName(".activities.SettingsActivity")),
                toPackage(PACKAGE_NAME)));
    }

    @Test
    public void verifyOpenNewVerificationActivity() {
        onView(withId(R.id.newVerification)).perform(click());
        intended(allOf(
                hasComponent(hasShortClassName(".activities.NewVerificationActivity")),
                toPackage(PACKAGE_NAME)));
    }
}

