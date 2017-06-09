package nl.tudelft.ewi.ds.bankchain.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.InvalidKeyException;
import java.security.SignatureException;

import nl.tudelft.ewi.ds.bankchain.R;
import nl.tudelft.ewi.ds.bankchain.cryptography.ChallengeResponse;
import nl.tudelft.ewi.ds.bankchain.cryptography.ED25519;

import static nl.tudelft.ewi.ds.bankchain.bank.IBANVerifier.isValidIBAN;
import static nl.tudelft.ewi.ds.bankchain.cryptography.ED25519.isValidPublicKey;

public class NewVerificationActivity extends AppCompatActivity {

    private String publicKey;
    private String iban;
    private String challenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_verification);
        initializeListeners();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("New verification");
        }
    }

    public void initializeListeners() {
        Button verifyButton = (Button) findViewById(R.id.createChallengeButton);
        // if button is clicked, close the custom dialog
        verifyButton.setOnClickListener(v -> {
            EditText publicKeyText = (EditText) findViewById(R.id.publicKeyInput);
            this.publicKey = publicKeyText.getText().toString();

            EditText ibanText = (EditText) findViewById(R.id.ibanInput);
            this.iban = ibanText.getText().toString();

            if (!isValidPublicKey(publicKey)) {
                showLongToast("Invalid Public Key!");
                return;
            }
            if (!isValidIBAN(iban)) {
                showLongToast("Invalid IBAN!");
                return;
            }

            createChallenge();
            showChallenge();
        });
    }

    public void createChallenge() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String privateKey = settings.getString("pref_private_key_key", "default_value");

        //TODO we crash if no private key is set
        try {
            this.challenge = ChallengeResponse.createChallenge(privateKey);
        } catch (SignatureException | InvalidKeyException ignored) {
            showLongToast("Your private key is not set correctly!");
        }
    }

    private void showChallenge() {
        TextView textView = (TextView) findViewById(R.id.challengeTextView);
        textView.setText(this.challenge);
        LinearLayout layout = (LinearLayout) findViewById(R.id.challengeButtonLayout);
        layout.setVisibility(View.VISIBLE);
    }

    private void showShortToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void showLongToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    public void bunqVerification(View v) {
        showLongToast("Going to send a Bunq transaction (but not really...)");
    }

    public void manualVerification(View v) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Challenge", this.challenge);
        clipboard.setPrimaryClip(clip);
        showLongToast("Challenge copied to clipboard");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getIban() {
        return iban;
    }
}
