package nl.tudelft.ewi.ds.bankchain.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import nl.tudelft.ewi.ds.bankchain.R;
import nl.tudelft.ewi.ds.bankchain.cryptography.ChallengeResponse;
import nl.tudelft.ewi.ds.bankchain.cryptography.ED25519;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initializeListeners();
        FloatingActionButton newVerificationFab = (FloatingActionButton)  findViewById(R.id.newVerification);
        newVerificationFab.setOnClickListener(this::startNewVerificationActivity);
    }

    public void startRecentTransactionActivity(View view) {
        Intent intent = new Intent(MainActivity.this, RecentTransactionsActivity.class);
        startActivity(intent);
        overridePendingTransition  (R.anim.right_slide_in, R.anim.left_slide_out);
    }

    public void startNewVerificationActivity(View view) {
        Intent intent = new Intent(MainActivity.this, NewVerificationActivity.class);
        startActivity(intent);
        overridePendingTransition  (R.anim.right_slide_in, R.anim.left_slide_out);
    }

    public void startSettingsActivity(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
    }

    public void initializeListeners() {
        Button verifyButton = (Button) findViewById(R.id.manualVerificationButton);
        // if button is clicked, close the custom dialog
        verifyButton.setOnClickListener(v -> {
            EditText challengeText = (EditText) findViewById(R.id.challengeInput);
            EditText ibanText = (EditText) findViewById(R.id.ibanInput);
            String challenge = challengeText.getText().toString();
            String iban = challengeText.getText().toString();
            showLongToast("Verifying and respond to this challenge");
        });

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
