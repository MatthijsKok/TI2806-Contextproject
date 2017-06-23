package nl.tudelft.ewi.ds.bankchain.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.PublicKey;

import nl.tudelft.ewi.ds.bankchain.BankTeller;
import nl.tudelft.ewi.ds.bankchain.blockchain.Blockchain;
import nl.tudelft.ewi.ds.bankchain.R;
import nl.tudelft.ewi.ds.bankchain.cryptography.ChallengeResponse;

import static nl.tudelft.ewi.ds.bankchain.bank.IBANVerifier.isValidIBAN;
import static nl.tudelft.ewi.ds.bankchain.cryptography.ED25519.getPublicKey;
import static nl.tudelft.ewi.ds.bankchain.cryptography.ED25519.isValidPublicKey;

public class VerifyResponseActivity extends AppCompatActivity {

    String response;
    String iban;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_response);
        initializeListeners();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Verify Response");
        }
    }

    public void initializeListeners() {
        Button responseButton = (Button) findViewById(R.id.manualResponseButton);
        // if button is clicked, close the custom dialog
        responseButton.setOnClickListener(v -> {
            EditText responseText = (EditText) findViewById(R.id.response);
            response = responseText.getText().toString();
            EditText publicKeyText = (EditText) findViewById(R.id.iban);
            iban = publicKeyText.getText().toString();
            if (!isValidIBAN(iban)) {
                showLongToast("Invalid IBAN!");
                return;
            }
            if (response.isEmpty()) {
                isValid();
                return;
            }
            validate();

        });
    }

    private boolean isValid() {
        Blockchain bl = BankTeller.getBankTeller().getBlockchain();
        if (bl.isValidated(iban)) {
            showLongToast("IBAN validated");
            return true;
        }
        showLongToast("IBAN! not validated");
        return false;
    }

    private void validate() {

        Blockchain bl = BankTeller.getBankTeller().getBlockchain();
        if (bl.isValidated(iban) && 1 == 2) {
            showLongToast("IBAN already validated");
            return;
        }
        PublicKey pk = bl.getPublicKeyForIBAN(iban);
        if (ChallengeResponse.isValidResponse(response, pk)) {
            bl.setIbanVerified(pk, iban, "unused");
            showLongToast("IBAN validated");
            return;
        }
        showLongToast("Invalid response for IBAN");

    }

    private void showLongToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
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
