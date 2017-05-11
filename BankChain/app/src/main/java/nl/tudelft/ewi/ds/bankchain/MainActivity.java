package nl.tudelft.ewi.ds.bankchain;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java8.util.concurrent.CompletableFuture;
import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.BankFactory;
import nl.tudelft.ewi.ds.bankchain.bank.Environment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener((view) ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
        );

        Environment v = new Environment();
        v.bank = "Bunq";
        v.url = "https://sandbox.public.api.bunq.com/";
        v.apiKey = "8f4a0a53df16da7cdb1974834d09042a88a395ad550ad61256dc49f65b04dabe";

        Bank b = new BankFactory(v).create();



        b.createSession().thenAccept(t -> Tools.runOnMainThread(() -> {
            Log.d("GUI", "Created session");
            Toast.makeText(getApplicationContext(), "Created session!", Toast.LENGTH_LONG).show();
            b.listTransactions(null);
        })).exceptionally(e -> {
            final Throwable t = b.confirmException(e);

            Tools.runOnMainThread(() -> {
                Log.d("GUI", "Failed session " + t.getMessage());
                Toast.makeText(getApplicationContext(), "Failed to create session: " + t.getMessage(), Toast.LENGTH_LONG).show();
            });

            return null;
        });



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

        return super.onOptionsItemSelected(item);
    }
}
