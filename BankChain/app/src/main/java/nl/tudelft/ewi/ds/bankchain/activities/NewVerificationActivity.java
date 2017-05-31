package nl.tudelft.ewi.ds.bankchain.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import nl.tudelft.ewi.ds.bankchain.R;
import nl.tudelft.ewi.ds.bankchain.bank.IBANVerifier;

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
            Log.d("GUI", "onCreate: Actionbar found");
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
            EditText ibanText = (EditText) findViewById(R.id.ibanInput);
            String publicKey = publicKeyText.getText().toString();
            String iban = ibanText.getText().toString();
            if (publicKey.length() == 0) {
                Toast.makeText(getApplicationContext(),
                        "Public Key can not be empty",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (!validIBAN(iban)) {
                Toast.makeText(getApplicationContext(),
                        "IBAN is not valid",
                        Toast.LENGTH_LONG).show();
            }
            createChallenge(publicKey, iban);
        });
    }

    public void createChallenge(String publicKey, String iban) {
        this.publicKey = publicKey;
        this.iban = iban;
        this.challenge = publicKey + ":" + iban;
        showChallenge();
    }

    public boolean validIBAN(String iban) {
        return IBANVerifier.verify(iban);
    }

    public void showChallenge() {
        TextView textView = (TextView) findViewById(R.id.challengeTextView);
        textView.setText(this.challenge);
        LinearLayout layout = (LinearLayout) findViewById(R.id.challengeButtonLayout);
        layout.setVisibility(View.VISIBLE);
    }

    public void bunqVerification(View v) {
        Toast.makeText(getApplicationContext(),
                "Going to send a Bunq transaction (but not really...)",
                Toast.LENGTH_LONG).show();
    }

    public void manualVerification(View v) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Challenge", this.challenge);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(),
                "Challenge copied to clipboard",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
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