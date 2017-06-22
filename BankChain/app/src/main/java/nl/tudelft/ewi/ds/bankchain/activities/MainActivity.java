package nl.tudelft.ewi.ds.bankchain.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import nl.tudelft.ewi.ds.bankchain.R;


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

        FloatingActionButton newVerificationFab = (FloatingActionButton)  findViewById(R.id.newVerification);
        newVerificationFab.setOnClickListener(this::startNewVerificationActivity);
        FloatingActionButton verifyResponseFab = (FloatingActionButton)  findViewById(R.id.verifyResponse);
        verifyResponseFab.setOnClickListener(this::startVerifyResponseAcitivity);
        FloatingActionButton respondToChallenge = (FloatingActionButton)  findViewById(R.id.respondToChallenge);
        respondToChallenge.setOnClickListener(this::startRespondToChallengeActivity);
        FloatingActionButton synchronize = (FloatingActionButton)  findViewById(R.id.synchronize);
        synchronize.setOnClickListener(this::synchronize);
    }

    private void synchronize(View view) {
        // Go Richard Go!
    }

    public void startRecentTransactionActivity(View view) {
        Intent intent = new Intent(MainActivity.this, RecentTransactionsActivity.class);
        startActivity(intent);
        overridePendingTransition  (R.anim.right_slide_in, R.anim.left_slide_out);
    }

    public void startRespondToChallengeActivity(View view) {
        Intent intent = new Intent(MainActivity.this, RespondToChallengeActivity.class);
        startActivity(intent);
        overridePendingTransition  (R.anim.right_slide_in, R.anim.left_slide_out);
    }

    public void startVerifyResponseAcitivity(View view) {
        Intent intent = new Intent(MainActivity.this, VerifyResponseActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
