package nl.tudelft.ewi.ds.bankchain.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import nl.tudelft.ewi.ds.bankchain.R;

public class NewVerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_verification);
        initializeListeners();
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
            onBackPressed();
        });

    }

    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
    }

}