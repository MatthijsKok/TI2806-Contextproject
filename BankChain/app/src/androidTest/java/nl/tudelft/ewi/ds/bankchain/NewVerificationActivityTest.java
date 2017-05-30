package nl.tudelft.ewi.ds.bankchain;


import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class NewVerificationActivityTest extends TestRunner{

    /* Instantiate an IntentsTestRule object. */
    @Rule
    public ActivityTestRule<NewVerificationActivity> activityRule =
            new ActivityTestRule<>(NewVerificationActivity.class);

    @Test
    public void verifyVerify() {
        onView(withId(R.id.publicKeyInput)).perform(replaceText("PublicKey"));
        onView(withId(R.id.ibanInput)).perform(replaceText("Iban"));
        onView(withId(R.id.verifyButton)).perform(click());
        assertEquals(activityRule.getActivity().getPublicKey(), "PublicKey");
        assertEquals(activityRule.getActivity().getIban(), "Iban");
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
