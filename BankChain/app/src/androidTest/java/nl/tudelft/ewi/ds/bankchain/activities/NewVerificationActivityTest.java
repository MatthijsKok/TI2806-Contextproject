package nl.tudelft.ewi.ds.bankchain.activities;

import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import nl.tudelft.ewi.ds.bankchain.R;
import nl.tudelft.ewi.ds.bankchain.TestRunner;
import nl.tudelft.ewi.ds.bankchain.activities.NewVerificationActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


/**
 * @author Isha Dijcks
 */

@RunWith(AndroidJUnit4.class)
//TRAVIS @Ignore
public class NewVerificationActivityTest extends TestRunner {

    /* Instantiate an IntentsTestRule object. */
    @Rule
    public ActivityTestRule<NewVerificationActivity> activityRule =
            new ActivityTestRule<>(NewVerificationActivity.class);

    @Test
    public void verifyVerify() {
        onView(ViewMatchers.withId(R.id.publicKeyInput)).perform(replaceText("PublicKey"));
        onView(withId(R.id.ibanInput)).perform(replaceText("Iban"));
        onView(withId(R.id.createChallengeButton)).perform(click());
        assertEquals(activityRule.getActivity().getPublicKey(), "PublicKey");
        assertEquals(activityRule.getActivity().getIban(), "Iban");
    }

    @Test
    @UiThreadTest
    public void verifyCreateChallenge() {
        activityRule.getActivity().createChallenge("","");
        assertEquals(activityRule.getActivity().getPublicKey(), "");
        assertEquals(activityRule.getActivity().getIban(), "");
        activityRule.getActivity().createChallenge("Public key","IBAN");
        assertEquals(activityRule.getActivity().getPublicKey(), "Public key");
        assertEquals(activityRule.getActivity().getIban(), "IBAN");
    }

    @Test
    public void activateBunqVerification() {

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
