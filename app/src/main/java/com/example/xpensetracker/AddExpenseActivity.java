package com.example.xpensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.xpensetracker.databinding.ActivityAddExpenseBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AddExpenseActivity extends AppCompatActivity {
    ActivityAddExpenseBinding binding;
    private String type;

    private ExpenseModel expenseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        type=getIntent().getStringExtra("type");
        expenseModel=(ExpenseModel) getIntent().getSerializableExtra("model");

        if(type==null){
            type=expenseModel.getType();
            binding.amount.setText(String.valueOf(expenseModel.getAmount()));
            binding.category.setText(expenseModel.getCategory());
            binding.note.setText(expenseModel.getNote());

        }

        if(type.equals("Income")){
            binding.incomeRadio.setChecked(true);
        }else{
            binding.expenseRadio.setChecked(true);
        }

        binding.incomeRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type="Income";
            }
        });
        binding.expenseRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type="Expense";
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        if(expenseModel==null){
            menuInflater.inflate(R.menu.add_menu, menu);
        }else{
            menuInflater.inflate(R.menu.update_menu, menu);
        }
        return true;
    }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.saveExpense){                                      //Save Key
            if(type!=null) {
                createExpense();
            }else{
                updateExpense();
            }
            return true;
        }
        if(id==R.id.deleteExpense){
            deleteExpense();
        }
        return false;
    }

    private void deleteExpense() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (userEmail == null) {
            Log.e("Firestore", "User email is null");
            return;
        }
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userEmail)
                .collection("expenses")
                .document(expenseModel.getExpenseId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Expense deleted successfully");
                    // Finish the activity or perform any other actions after successful deletion
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error deleting expense", e);
                    // Handle the error, e.g., show a toast or alert dialog
                });
        finish();

    }

    private void createExpense() {
        String expenseId = UUID.randomUUID().toString();
        String amount = binding.amount.getText().toString();
        String note = binding.note.getText().toString();
        String category = binding.category.getText().toString();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String type;

        boolean incomeChecked = binding.incomeRadio.isChecked();
        if (incomeChecked) {
            type = "Income";
        } else {
            type = "Expense";
        }

        if (amount.trim().length() == 0) {
            binding.amount.setError("Empty");
            return;
        }

        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (userEmail == null) {
            Log.e("Firestore", "User email is null");
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userEmail)
                .collection("expenses")
                .document(expenseId)
                .set(new ExpenseModel(expenseId, note, category, type, Long.parseLong(amount), currentDate, userEmail))
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Expense created successfully");
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error creating expense", e);
                    // Handle the error, e.g., show a toast or alert dialog
                });
    }

    private void updateExpense() {
        String expenseId = expenseModel.getExpenseId();
        String amount = binding.amount.getText().toString();
        String note = binding.note.getText().toString();
        String category = binding.category.getText().toString();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String type;

        boolean incomeChecked = binding.incomeRadio.isChecked();
        if (incomeChecked) {
            type = "Income";
        } else {
            type = "Expense";
        }

        if (amount.trim().length() == 0) {
            binding.amount.setError("Empty");
            return;
        }

        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (userEmail == null) {
            Log.e("Firestore", "User email is null");
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userEmail)
                .collection("expenses")
                .document(expenseId)
                .set(new ExpenseModel(expenseId, note, category, type, Long.parseLong(amount), currentDate, userEmail))
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Expense updated successfully");
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error updating expense", e);
                    // Handle the error, e.g., show a toast or alert dialog
                });
    }



}