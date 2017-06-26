package nl.tudelft.ewi.ds.bankchain;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

//TRAVIS import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import nl.tudelft.ewi.ds.bankchain.activities.MainActivity;
import nl.tudelft.ewi.ds.bankchain.activities.NewVerificationActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


/**
 * @author Isha Dijcks
 */

@RunWith(AndroidJUnit4.class)
//TRAVIS @Ignore
public class DemoTest extends TestRunner {

    @Rule
    public IntentsTestRule<MainActivity> mIntentsRule =
            new IntentsTestRule<>(MainActivity.class);
    @Rule
    public ActivityTestRule<NewVerificationActivity> newVerificationActivityRule =
            new ActivityTestRule<>(NewVerificationActivity.class);


    @Test
    public void verifyOpenNewVerificationActivity() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.newVerification)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.ibanInput)).perform(replaceText("GB82WEST12345698765432"));
        Thread.sleep(1000);
        onView(ViewMatchers.withId(R.id.pk)).perform(replaceText("3b6a27bcceb6a42d62a3a8d02a6f0d73653215771de243a63ac048a18b59da29"));
        Thread.sleep(1000);
        onView(withId(R.id.name)).perform(replaceText("Dummy Dum"));
        onView(withId(R.id.createChallengeButton)).perform(click());
        onView(withId(R.id.verifyManualButton)).perform(click());

        Thread.sleep(5000);

        onView(withId(R.id.respondToChallenge)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.ibanInput)).perform(replaceText("GB82WEST12345698765432"));
        Thread.sleep(1000);
        onView(ViewMatchers.withId(R.id.challengeInput)).perform(replaceText(
                "CH:a091b8af:381283d24486f95db050e061fdce765299734f9a6f8e74136a32b0b45fc66625ed9158c4463fd132a7f77dbd9132d686a37b466bb1ca2c3abe03257f7d3fb707"));
        Thread.sleep(1000);
        onView(withId(R.id.manualVerificationButton)).perform(click());
        Thread.sleep(5000);

        onView(withId(R.id.verifyResponse)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.iban)).perform(replaceText("GB82WEST12345698765432"));
        Thread.sleep(1000);
        onView(ViewMatchers.withId(R.id.response)).perform(replaceText(
                "RE:a091b8af:381283d24486f95db050e061fdce765299734f9a6f8e74136a32b0b45fc66625ed9158c4463fd132a7f77dbd9132d686a37b466bb1ca2c3abe03257f7d3fb707"));
        Thread.sleep(1000);
        onView(withId(R.id.manualResponseButton)).perform(click());
        Thread.sleep(20000);

    }

}

