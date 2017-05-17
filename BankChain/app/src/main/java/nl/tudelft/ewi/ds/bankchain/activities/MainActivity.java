package nl.tudelft.ewi.ds.bankchain.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;

import nl.tudelft.ewi.ds.bankchain.Environment;
import nl.tudelft.ewi.ds.bankchain.Manager;
import nl.tudelft.ewi.ds.bankchain.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the manager
        try {
            // TODO: replace this with in-app settings on release.
            Environment environment = Environment.loadDefaults(getResources(), R.raw.environment);

            Manager.getInstance().setEnvironment(environment);
        } catch (IOException e) {
            Log.e("ENVIRONMENT", "Environment could not be loaded: " + e.getMessage());

            return;
        }

        setContentView(R.layout.activity_main);
    }

    public void startRecentTransactionActivity(View view) {
        Intent intent = new Intent(MainActivity.this, RecentTransactionsActivity.class);
        startActivity(intent);
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_settings) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
