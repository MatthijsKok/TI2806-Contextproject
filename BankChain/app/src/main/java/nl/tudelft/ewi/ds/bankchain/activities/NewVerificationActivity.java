package nl.tudelft.ewi.ds.bankchain.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import nl.tudelft.ewi.ds.bankchain.R;

public class NewVerificationActivity extends AppCompatActivity {

    private String publicKey;
    private String iban;

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
        Button verifyButton = (Button) findViewById(R.id.verifyButton);
        // if button is clicked, close the custom dialog
        verifyButton.setOnClickListener(v -> {
            EditText publicKeyText = (EditText) findViewById(R.id.publicKeyInput);
            EditText ibanText = (EditText) findViewById(R.id.ibanInput);
            String publicKey = publicKeyText.getText().toString();
            String iban = ibanText.getText().toString();
            Log.d("GUI", "onClick: Gonna verify, but not really...");
            Log.d("GUI", "Public key: " + publicKey);
            Log.d("GUI", "IBAN: " + iban);
            this.publicKey = publicKey;
            this.iban = iban;
        });
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