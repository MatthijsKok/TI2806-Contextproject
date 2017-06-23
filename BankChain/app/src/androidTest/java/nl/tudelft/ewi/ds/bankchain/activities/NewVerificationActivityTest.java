package nl.tudelft.ewi.ds.bankchain.activities;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
//TRAVIS import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;

import nl.tudelft.ewi.ds.bankchain.BankTeller;
import nl.tudelft.ewi.ds.bankchain.Environment;
import nl.tudelft.ewi.ds.bankchain.R;
import nl.tudelft.ewi.ds.bankchain.TestRunner;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
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

    @Before
    public void setPrivateKey() throws IOException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("pref_private_key_key", "0000000000000000000000000000000000000000000000000000000000000000");
        editor.apply();
        BankTeller.getBankTeller(getInstrumentation().getTargetContext(), Environment.loadDefaults(getInputStream("res/raw/environment.xml")));
    }

    private InputStream getInputStream(String path) {
        return this.getClass().getClassLoader().getResourceAsStream(path);
    }

    @Test
    public void verifyVerify() {
        onView(ViewMatchers.withId(R.id.pk)).perform(replaceText("3b6a27bcceb6a42d62a3a8d02a6f0d73653215771de243a63ac048a18b59da29"));
        onView(withId(R.id.ibanInput)).perform(replaceText("GB82WEST12345698765432"));
        onView(withId(R.id.name)).perform(replaceText("Dummy"));
        onView(withId(R.id.createChallengeButton)).perform(click());
        assertEquals("3b6a27bcceb6a42d62a3a8d02a6f0d73653215771de243a63ac048a18b59da29", activityRule.getActivity().getPublicKey());
        assertEquals("GB82WEST12345698765432", activityRule.getActivity().getIban());
    }

    @Test
    public void verifyWrongPublicKey() {
        onView(ViewMatchers.withId(R.id.pk)).perform(replaceText(""));
        onView(withId(R.id.ibanInput)).perform(replaceText("Iban"));
        onView(withId(R.id.name)).perform(replaceText("Dummy"));
        onView(withId(R.id.createChallengeButton)).perform(click());
        assertEquals("", activityRule.getActivity().getPublicKey());
        assertEquals("Iban", activityRule.getActivity().getIban());
    }

    @Test
    public void verifyWrongIBAN() {
        onView(ViewMatchers.withId(R.id.pk)).perform(replaceText("PublicKey"));
        onView(withId(R.id.ibanInput)).perform(replaceText(""));
        onView(withId(R.id.createChallengeButton)).perform(click());
        assertEquals("PublicKey", activityRule.getActivity().getPublicKey());
        assertEquals("", activityRule.getActivity().getIban());
    }

    @Test
    public void bunqVerificationTest() {
        onView(withId(R.id.pk)).perform(replaceText("3b6a27bcceb6a42d62a3a8d02a6f0d73653215771de243a63ac048a18b59da29"));
        onView(withId(R.id.ibanInput)).perform(replaceText("GB82WEST12345698765432"));
        onView(withId(R.id.name)).perform(replaceText("Dummy"));
        onView(withId(R.id.createChallengeButton)).perform(click());
        onView(withId(R.id.verifyBunqButton)).perform(click());
    }

    @Test
    public void manualVerificationTest() {
        onView(ViewMatchers.withId(R.id.pk)).perform(replaceText("3b6a27bcceb6a42d62a3a8d02a6f0d73653215771de243a63ac048a18b59da29"));
        onView(withId(R.id.ibanInput)).perform(replaceText("GB82WEST12345698765432"));
        onView(withId(R.id.name)).perform(replaceText("Dummy"));
        onView(withId(R.id.createChallengeButton)).perform(click());
        onView(withId(R.id.verifyManualButton)).perform(click());
        ClipboardManager clipboard = (ClipboardManager) activityRule.getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        String generatedChallenge = clipboard.getText().toString();
        assertTrue(generatedChallenge.contains("CH:"));

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
