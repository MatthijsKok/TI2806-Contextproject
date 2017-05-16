package nl.tudelft.ewi.ds.bankchain;

/**
 * Created by Isha Dijcks on 16-5-2017.
 */

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import nl.tudelft.ewi.ds.bankchain.activities.MainActivity;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;

/**
 * @author Isha Dijcks
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private static final String PACKAGE_NAME = "nl.tudelft.ewi.ds.bankchain";

    /* Instantiate an IntentsTestRule object. */
    @Rule
    public IntentsTestRule<MainActivity> mIntentsRule =
            new IntentsTestRule<>(MainActivity.class);

    @Test
    public void verifyOpenRecentTransactionsActivity() {
        // Clicks a button to send an intent to another activity
        onView(withId(R.id.recentTransactionActivityButton)).perform(click());

        // Verifies that the DisplayMessageActivity received an intent
        // with the correct package name and message.
        intended(allOf(
                hasComponent(hasShortClassName(".activities.RecentTransactionsActivity")),
                toPackage(PACKAGE_NAME)));

    }


}

