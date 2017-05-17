package nl.tudelft.ewi.ds.bankchain.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.tudelft.ewi.ds.bankchain.Environment;
import nl.tudelft.ewi.ds.bankchain.R;
import nl.tudelft.ewi.ds.bankchain.Tools;
import nl.tudelft.ewi.ds.bankchain.bank.Account;
import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.BankFactory;
import nl.tudelft.ewi.ds.bankchain.bank.Transaction;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqAccount;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqParty;

public class RecentTransactionsActivity extends AppCompatActivity {

    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_transactions);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        retrieveRecentTransactions();
    }

    /*
    This method will have to be moved to a singleton manager. So a new bank doesn't have to be made every time the activity is opened.
     */
    public void retrieveRecentTransactions() {
        try {
            Environment.loadDefaults(getResources(), R.raw.environment);
        } catch (IOException e) {
            Log.e("GUI", "Failed to load environment");
            return;
        }
        Environment v = Environment.getDefaults();

        Bank b = new BankFactory(v).create();

        b.createSession()
                .thenAccept(t -> Tools.runOnMainThread(() -> {
                    Log.d("GUI", "Created session");
                    Toast.makeText(getApplicationContext(),
                            "Created session!",
                            Toast.LENGTH_LONG).show();

                    b.listTransactions(new BunqAccount("AT611904300234573201",2021,new BunqParty("hello world", 2002))).thenAccept(ts -> Tools.runOnMainThread(() -> {
                        Toast.makeText(getApplicationContext(),
                                "Got list of transactions!",
                                Toast.LENGTH_LONG).show();
                        Log.i("GUI", ts.toString());
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        showRecentTransactions(ts);

                    }));

                }))
                .exceptionally(e -> {
                    final Throwable t = b.confirmException(e);

                    Tools.runOnMainThread(() -> {
                        Log.d("GUI", "Failed session " + t.getMessage());
                        Toast.makeText(getApplicationContext(),
                                "Failed to create session: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                        Log.d("GUI", "Just failed");
                        showRecentTransactions(null);
                    });
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    return null;
                });
    }

    /*
    Takes the list of recent transactions and outputs them to the UI
     */
    public void showRecentTransactions(List<? extends Transaction> transactionList) {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        expListView = (ExpandableListView) findViewById(R.id.recentTransactionsList);
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        if (transactionList == null) {
            updateNoTransactionsDisplay("Could not retrieve transactions.");
            return;
        }
        if (transactionList.size() == 0) {
            updateNoTransactionsDisplay("No transactions have been made yet.");
            return;
        }

        for (int i = 0; i < transactionList.size(); i++) {
            listDataHeader.add(transactionList.get(i).getDescription());
            List<String> details = new ArrayList<>();

            if (transactionList.get(i).getValue() != null && transactionList.get(i).getCurrency() != null) {
                details.add(transactionList.get(i).getValue().toString() + " " + transactionList.get(i).getCurrency().toString());
            }
            if(transactionList.get(i).getValue() < 0){
                details.add("OUT");
            }
            else {
                details.add("IN");
            }
//                     TODO: implement once getDate functions correctly
//                    if (transactionList.get(i).getDate() != null) {
//                        details.add(transactionList.get(i).getDate().toString());
//                    }
            if (transactionList.get(i).getCounterAccount() != null) {
                details.add("" + transactionList.get(i).getCounterAccount().getParty().toString());
            }
            listDataChild.put(listDataHeader.get(i), details);
        }
    }

    /*
    Updates the error display if the recent transactions could not be retrieved.
     */
    public void updateNoTransactionsDisplay(String message) {
        TextView textView = (TextView) findViewById(R.id.recentTransactionsError);
        textView.setText(message);
        textView.setTextSize(20);
    }


}
