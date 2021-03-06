package nl.tudelft.ewi.ds.bankchain.activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import nl.tudelft.ewi.ds.bankchain.Environment;
import nl.tudelft.ewi.ds.bankchain.R;
import nl.tudelft.ewi.ds.bankchain.Tools;
import nl.tudelft.ewi.ds.bankchain.bank.Account;
import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.BankFactory;
import nl.tudelft.ewi.ds.bankchain.bank.Party;
import nl.tudelft.ewi.ds.bankchain.bank.Transaction;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqAccount;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqParty;

public class RecentTransactionsActivity extends AppCompatActivity {

    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_transactions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            Log.d("GUI", "onCreate: Actionbar found");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_recent_transactions_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::retrieveRecentTransactions);
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
        Bank bank = new BankFactory(v, this.getApplicationContext()).create();
        bank.createSession()
                .thenAccept(t -> Tools.runOnMainThread(() -> {
                    Log.d("GUI", "Created session");
                    showLongToast("Created session!");

                    Party p = new BunqParty("hello world", 2002);
                    Account account;
                    try {
                        List<Account> accounts = bank.listAccount(p).get();
                        if (accounts.isEmpty()) {
                            account = new BunqAccount("error", -1, p);
                        }
                        account = bank.listAccount(p).get().get(0);

                    } catch (InterruptedException | ExecutionException e) {
                        Log.e("CRYPTO", e.getMessage());
                        return;
                    }

                    bank.listTransactions(account).thenAccept(ts -> Tools.runOnMainThread(() -> {
                        showLongToast("Got list of transactions!");
                        Log.i("GUI", ts.toString());
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        showRecentTransactions(ts);

                    }));

                }))
                .exceptionally(e -> {
                    final Throwable t = bank.confirmException(e);

                    Tools.runOnMainThread(() -> {
                        showLongToast("Failed to create session: " + t.getMessage());
                        Log.d("GUI", "Failed session " + t.getMessage());
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
        initializeTransactionListView();
        if (transactionList == null) {
            updateNoTransactionsDisplay("Could not retrieve transactions.");
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (transactionList.isEmpty()) {
            updateNoTransactionsDisplay("No transactions have been made yet.");
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        for (int i = 0; i < transactionList.size(); i++) {
            Transaction transaction = transactionList.get(i);
            listDataHeader.add(transaction.getDescription());

            List<String> details = new ArrayList<>();
            details.add(transaction.getValue() + " " + transactionList.get(i).getCurrency().toString());
            details.add(transaction.getDate().toString());
            details.add(transaction.getCounterAccount().getParty().toString());

            listDataChild.put(listDataHeader.get(i), details);
        }
        swipeRefreshLayout.setRefreshing(false);
    }


    /*
    Initialize the ListView related objects.
     */
    private void initializeTransactionListView() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        expListView = (ExpandableListView) findViewById(R.id.recentTransactionsList);
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
    }

    private void showLongToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    /*
    Updates the error display if the recent transactions could not be retrieved.
     */
    public void updateNoTransactionsDisplay(String message) {
        TextView textView = (TextView) findViewById(R.id.recentTransactionsError);
        textView.setText(message);
        textView.setTextSize(20);
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
}
