package nl.tudelft.ewi.ds.bankchain.activities;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import nl.tudelft.ewi.ds.bankchain.R;
import nl.tudelft.ewi.ds.bankchain.TestRunner;

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
//TRAVIS @Ignore
public class MainActivityTest extends TestRunner {

    private static final String PACKAGE_NAME = "nl.tudelft.ewi.ds.bankchain";

    @Rule
    public IntentsTestRule<MainActivity> mIntentsRule =
            new IntentsTestRule<>(MainActivity.class);

    /* Instantiate an IntentsTestRule object. */

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

