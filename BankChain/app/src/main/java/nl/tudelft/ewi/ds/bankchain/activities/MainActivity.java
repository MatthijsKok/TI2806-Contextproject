package nl.tudelft.ewi.ds.bankchain.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.List;

import nl.tudelft.ewi.ds.bankchain.BankTeller;
import nl.tudelft.ewi.ds.bankchain.Environment;
import nl.tudelft.ewi.ds.bankchain.R;
import nl.tudelft.ewi.ds.bankchain.bank.Transaction;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqTransactionParser;
import nl.tudelft.ewi.ds.bankchain.cryptography.ED25519;


public class MainActivity extends AppCompatActivity {

    private long lastSynchronized = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        try {
            Environment.loadDefaults(getResources(), R.raw.environment);
        } catch (IOException e) {
            Log.e("GUI", "Failed to load environment");
            return;
        }
        BankTeller.getBankTeller(this.getApplicationContext(), Environment.getDefaults());

        FloatingActionButton newVerificationFab = (FloatingActionButton) findViewById(R.id.newVerification);

        newVerificationFab.setOnClickListener(this::startNewVerificationActivity);
        FloatingActionButton verifyResponseFab = (FloatingActionButton) findViewById(R.id.verifyResponse);
        verifyResponseFab.setOnClickListener(this::startVerifyResponseAcitivity);
        FloatingActionButton respondToChallenge = (FloatingActionButton) findViewById(R.id.respondToChallenge);
        respondToChallenge.setOnClickListener(this::startRespondToChallengeActivity);
        FloatingActionButton synchronize = (FloatingActionButton) findViewById(R.id.synchronize);
        synchronize.setOnClickListener(this::synchronize);
    }

    private void synchronize(View view) {
        showLongToast("Synchronize started!");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String privateKeyString = settings.getString("pref_private_key_key", "default_value");
        PrivateKey privateKey = ED25519.getPrivateKey(privateKeyString);

        List<Transaction> transactions = BankTeller.getBankTeller().getTransactions();
        BunqTransactionParser bunqTransactionParser = new BunqTransactionParser();

        bunqTransactionParser.respondToPendingChallenges(privateKey,transactions, lastSynchronized);
        lastSynchronized = System.currentTimeMillis();

        showLongToast("Synchronize successful!");
    }

    private void showLongToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    public void startRecentTransactionActivity(View view) {
        Intent intent = new Intent(MainActivity.this, RecentTransactionsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
    }

    public void startRespondToChallengeActivity(View view) {
        Intent intent = new Intent(MainActivity.this, RespondToChallengeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
    }

    public void startVerifyResponseAcitivity(View view) {
        Intent intent = new Intent(MainActivity.this, VerifyResponseActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
    }

    public void startNewVerificationActivity(View view) {
        Intent intent = new Intent(MainActivity.this, NewVerificationActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
    }

    public void startSettingsActivity(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
