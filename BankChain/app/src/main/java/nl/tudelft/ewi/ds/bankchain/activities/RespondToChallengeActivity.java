package nl.tudelft.ewi.ds.bankchain.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.i2p.crypto.eddsa.EdDSAPublicKey;

import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.SignatureException;

import nl.tudelft.ewi.ds.bankchain.BankTeller;
import nl.tudelft.ewi.ds.bankchain.Blockchain.Blockchain;
import nl.tudelft.ewi.ds.bankchain.R;
import nl.tudelft.ewi.ds.bankchain.cryptography.ChallengeResponse;
import nl.tudelft.ewi.ds.bankchain.cryptography.ED25519;

import static nl.tudelft.ewi.ds.bankchain.bank.IBANVerifier.isValidIBAN;


public class RespondToChallengeActivity extends AppCompatActivity {


    String iban;
    String challenge;
    String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond_to_challenge);
        initializeListeners();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Respond to challenge");
        }
    }

    public void initializeListeners() {
        Button verifyButton = (Button) findViewById(R.id.manualVerificationButton);
        // if button is clicked, close the custom dialog
        verifyButton.setOnClickListener(v -> {
            EditText challengeText = (EditText) findViewById(R.id.challengeInput);
            EditText ibanText = (EditText) findViewById(R.id.ibanInput);
            challenge = challengeText.getText().toString();
            iban = ibanText.getText().toString();
            if (!isValidIBAN(iban)) {
                showLongToast("Invalid IBAN!");
                return;
            }
            if (respond()) {
                showLongToast("Verifying and respond to this challenge");
            }
        });

    }


    private Boolean respond() {
        Blockchain bl = BankTeller.getBankTeller().getBlockchain();
        PublicKey pk = bl.getPublicKeyForIBAN(iban);
        if (pk == null) {
            showLongToast("unknown bank account");
            return false;
        }
        if (ChallengeResponse.isValidChallenge(challenge, (EdDSAPublicKey) pk)) {
            bl.setIbanVerified(pk, iban, "ignored");
            createResponse();
            return true;
        }
        showLongToast("invalid challenge");
        return false;
    }

    private void showLongToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    public void createResponse() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String privateKey = settings.getString("pref_private_key_key", "default_value");

        try {
            this.response = ChallengeResponse.createResponse(challenge, ED25519.getPrivateKey(privateKey));
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("response", this.response);
            clipboard.setPrimaryClip(clip);
            showLongToast("response validated and copied to clipboard");
        } catch (SignatureException | InvalidKeyException ignored) {
            showLongToast("Your private key is not set correctly!");
        }
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
}
