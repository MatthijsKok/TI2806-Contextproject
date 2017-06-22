package nl.tudelft.ewi.ds.bankchain.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import nl.tudelft.ewi.ds.bankchain.R;


public class RespondToChallengeActivity extends AppCompatActivity {

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
            String challenge = challengeText.getText().toString();
            String iban = challengeText.getText().toString();
            showLongToast("Verifying and respond to this challenge");
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
