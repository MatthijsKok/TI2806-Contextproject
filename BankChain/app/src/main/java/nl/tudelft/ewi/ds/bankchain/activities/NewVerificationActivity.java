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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Currency;
import java.util.concurrent.ExecutionException;

import nl.tudelft.ewi.ds.bankchain.Environment;
import nl.tudelft.ewi.ds.bankchain.R;
import nl.tudelft.ewi.ds.bankchain.bank.Account;
import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.BankFactory;
import nl.tudelft.ewi.ds.bankchain.bank.Party;
import nl.tudelft.ewi.ds.bankchain.bank.Transaction;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqAccount;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqParty;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqTransaction;
import nl.tudelft.ewi.ds.bankchain.cryptography.ChallengeResponse;

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
            EditText ibanText = (EditText) findViewById(R.id.ibanInput);
            this.iban = ibanText.getText().toString();

            if (!isValidIBAN(iban)) {
                showLongToast("Invalid IBAN!");
                return;
            }
            hideSoftKeyBoard();
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

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void bunqVerification(View v) {
        Environment env = Environment.getDefaults();
        Bank bank = new BankFactory(env, this.getApplicationContext()).create();
        bank.createSession();
        try {
            Party user = bank.listUsers().get().get(0);
            Account ac = bank .listAccount(user).get().get(0);
            Party chp = new BunqParty("Anonimus",-1);
            Account cha =  new BunqAccount(iban,-1,chp);
            Transaction trans = new BunqTransaction(0.01f,ac,cha, Currency.getInstance("EUR"),challenge);
            bank.sendTransaction(trans);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

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
