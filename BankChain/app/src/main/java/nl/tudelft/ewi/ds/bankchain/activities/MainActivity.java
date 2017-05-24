package nl.tudelft.ewi.ds.bankchain.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import nl.tudelft.ewi.ds.bankchain.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getActionBar() != null) {
            Log.d("GUI", "onCreate: Actionbar found");
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        FloatingActionButton newVerificationFab = (FloatingActionButton)  findViewById(R.id.newVerification);
        newVerificationFab.setOnClickListener(v -> newVerification());
    }

    public void startRecentTransactionActivity(View view) {
        Intent intent = new Intent(MainActivity.this, RecentTransactionsActivity.class);
        startActivity(intent);
        overridePendingTransition  (R.anim.right_slide_in, R.anim.left_slide_out);
    }

    public void startSettingsActivity(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        overridePendingTransition  (R.anim.right_slide_in, R.anim.left_slide_out);
    }

    public void newVerification() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.new_verification);
        dialog.setTitle("New verification");

        // set the custom dialog components - text, image and button

        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
        Button verifyButton = (Button) dialog.findViewById(R.id.verifyButton);
        // if button is clicked, close the custom dialog
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        verifyButton.setOnClickListener(v -> {
            dialog.dismiss();
            EditText publicKeyText = (EditText) dialog.findViewById(R.id.publicKeyInput);
            EditText ibanText = (EditText) dialog.findViewById(R.id.ibanInput);
            String publicKey = publicKeyText.getText().toString();
            String iban = ibanText.getText().toString();
            Log.d("GUI", "onClick: Gonna verify, but not really...");
            Log.d("GUI", "Public key: " + publicKey);
            Log.d("GUI", "IBAN: " + iban);
        });

        dialog.show();
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }
}
