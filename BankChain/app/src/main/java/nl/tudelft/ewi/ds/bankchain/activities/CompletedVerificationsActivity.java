package nl.tudelft.ewi.ds.bankchain.activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.tudelft.ewi.ds.bankchain.InternalStorage;
import nl.tudelft.ewi.ds.bankchain.R;
import nl.tudelft.ewi.ds.bankchain.VerifiedAccount;

public class CompletedVerificationsActivity extends AppCompatActivity {

    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_verifications);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_completed_verifications_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> showCompletedVerifications(InternalStorage.retrieveCompletedVerifications(this)));
        showCompletedVerifications(InternalStorage.retrieveCompletedVerifications(this));
    }


    public void showCompletedVerifications(List<VerifiedAccount> completedVerificationsList) {
        initializeTransactionListView();
        if (completedVerificationsList == null) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (completedVerificationsList.isEmpty()) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        for (int i = 0; i < completedVerificationsList.size(); i++) {
            listDataHeader.add(completedVerificationsList.get(i).getPublicKey());
            List<String> details = new ArrayList<>();

            if (completedVerificationsList.get(i).getIban() != null) {
                details.add(completedVerificationsList.get(i).getIban().toString());
            }
            if (completedVerificationsList.get(i).getDate() != null) {
                details.add(completedVerificationsList.get(i).getDate().toString());
            }

            listDataChild.put(listDataHeader.get(i), details);
        }
        swipeRefreshLayout.setRefreshing(false);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }


    /*
    Initialize the ListView related objects.
     */
    private void initializeTransactionListView() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        expListView = (ExpandableListView) findViewById(R.id.completedVerificationsList);
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
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
