package nl.tudelft.ewi.ds.bankchain.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import nl.tudelft.ewi.ds.bankchain.R;
import nl.tudelft.ewi.ds.bankchain.cryptography.ChallengeResponse;
import nl.tudelft.ewi.ds.bankchain.cryptography.ED25519;

public class VerifyResponseActivity extends AppCompatActivity {

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
            String response = responseText.getText().toString();
            EditText publicKeyText = (EditText) findViewById(R.id.publicKeyInput);
            String publicKey = publicKeyText.getText().toString();
            showLongToast("Valid response: " + ChallengeResponse.isValidResponse(response, ED25519.getPublicKey(publicKey)));
        });
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
