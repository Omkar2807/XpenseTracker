package com.example.xpensetracker;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class TransactionSheetActivity extends AppCompatActivity {

    // ...
    private static final String TAG = "BalanceSheetActivity";

    private FirebaseFirestore db;
    private TableLayout balanceSheetTable;
    private EditText searchEditText;
    private TableRow headerRow;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_sheet);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize TableLayout
        balanceSheetTable = findViewById(R.id.balanceSheetTable);

        // Initialize search bar
        searchEditText = findViewById(R.id.searchEditText);

        // Check if the header row has already been added
        if (headerRow == null) {
            headerRow = new TableRow(this);

            // Create TextViews for each column in the header
            TextView dateHeaderTextView = createHeaderTextView("Date");
            TextView categoryHeaderTextView = createHeaderTextView("Category");
            TextView typeHeaderTextView = createHeaderTextView("Type");
            TextView amountHeaderTextView = createHeaderTextView("Amount");

            // Add TextViews to the header row
            headerRow.addView(dateHeaderTextView);
            headerRow.addView(categoryHeaderTextView);
            headerRow.addView(typeHeaderTextView);
            headerRow.addView(amountHeaderTextView);

            // Add the header row to the table
            balanceSheetTable.addView(headerRow);
        }

        // Fetch data from Firestore (if needed)
        if (balanceSheetTable.getChildCount() == 1) {
            fetchDataFromFirestore();
        }

        // Set up search functionality
        setupSearch();
    }




    private void fetchDataFromFirestore() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (userEmail == null) {
            Log.e(TAG, "User email is null");
            return;
        }

        db.collection("users")
                .document(userEmail)
                .collection("expenses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Extract data from the document
                                String date = document.getString("date");
                                String category = document.getString("category");
                                String type = document.getString("type");
                                long amount = document.getLong("amount");

                                // Process the data as needed (e.g., update UI, calculate balance)
                                updateTable(date, category, type, amount);
                            }
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void updateTable(String date, String category, String type, long amount) {
        // Create a new row
        TableRow row = new TableRow(this);

        // Create TextViews for each column
        TextView dateTextView = createTextView(date);
        TextView categoryTextView = createTextView(category);
        TextView typeTextView = createTextView(type);
        TextView amountTextView = createTextView(String.valueOf(amount));

        // Add TextViews to the row
        row.addView(dateTextView);
        row.addView(categoryTextView);
        row.addView(typeTextView);
        row.addView(amountTextView);

        // Add the row to the table
        balanceSheetTable.addView(row);
    }
    private TextView createHeaderTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setBackgroundColor(getResources().getColor(R.color.teal_700));
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextSize(18);
        textView.setPadding(10, 10, 10, 10);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        return textView;
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        return textView;
    }

    private void setupSearch() {
        // Set an action listener on the searchEditText
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Perform search when "Done" button is pressed
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (userEmail == null) {
            Log.e(TAG, "User email is null");
            return;
        }

        // Get the entered date from the searchEditText
        String searchDate = searchEditText.getText().toString().trim();

        // Clear the table before displaying search results
        balanceSheetTable.removeViews(1,balanceSheetTable.getChildCount() -1);

        // Fetch data from Firestore for the entered date and user's email
        db.collection("users")
                .document(userEmail)
                .collection("expenses")
                .whereEqualTo("date", searchDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Extract data from the document
                                String date = document.getString("date");
                                String category = document.getString("category");
                                String type = document.getString("type");
                                long amount = document.getLong("amount");

                                // Process the data as needed (e.g., update UI, calculate balance)
                                updateTable(date, category, type, amount);
                            }
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    // ...
}