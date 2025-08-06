package com.example.xpensetracker;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.xpensetracker.databinding.ActivityDashboardBinding;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity implements OnItemsClick{
    ActivityDashboardBinding binding;
    FirebaseAuth firebaseAuth;
    private ExpensesAdapter expensesAdapter;
    //Intent intent;
    private long income=0,expense=0;
    //private static final int logout = R.id.logout;
    //private static final int MENU_OPTION2_ID = R.id.action_option2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();

        expensesAdapter=new ExpensesAdapter(this,this);
        binding.recycler.setAdapter(expensesAdapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));



        binding.addIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this,AddExpenseActivity.class);
                intent.putExtra("type","Income");
                startActivity(intent);
            }
        });
        binding.addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this,AddExpenseActivity.class);
                intent.putExtra("type","Expense");
                startActivity(intent);
            }
        });

        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    startActivity(new Intent(Dashboard.this,MainActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.logout){
            logout();
            return true;
            // Handle Option 1 click
        } else if (id==R.id.itr) {
            itr();
            return true;
        }
        else if (id==R.id.balance) {
            balance();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void balance() {
        Intent intent=new Intent(Dashboard.this, TransactionSheetActivity.class);
        startActivity(intent);
    }

    private void itr() {
        Intent intent=new Intent(Dashboard.this, IncomeTaxReturnActivity.class);
        startActivity(intent);
    }

    private void logout() {
        AlertDialog.Builder builder=new AlertDialog.Builder(Dashboard.this);
        builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        firebaseAuth.signOut();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Please");
        progressDialog.setMessage("Wait");
        progressDialog.setCancelable(false);
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            progressDialog.show();
            FirebaseAuth.getInstance()
                    .signInAnonymously()
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            progressDialog.cancel();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.cancel();
                            Toast.makeText(Dashboard.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        income=0;expense=0;
        getData();                     //DATA RETRIVAL
    }
    ///////////////////////////////////////////////////HISTORY TABS/////////////////////////////////////////////////////////
    private void getData() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (userEmail == null) {
            // Handle the case where the user's email is null
            return;
        }

        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(userEmail)
                .collection("expenses")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    expensesAdapter.clear();
                    List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot ds : dsList) {
                        ExpenseModel expenseModel = ds.toObject(ExpenseModel.class);
                        if (expenseModel != null) {
                            if (expenseModel.getType().equals("Income")) {
                                income += expenseModel.getAmount();
                            } else {
                                expense += expenseModel.getAmount();
                            }
                            expensesAdapter.add(expenseModel);
                        }
                    }
                    setUpGraph();
                })
                .addOnFailureListener(e -> {
                    // Handle the failure, e.g., show a toast or alert dialog
                    Toast.makeText(Dashboard.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                });
    }



    private void setUpGraph() {
        List<PieEntry> pieEntryList=new ArrayList<>();
        List<Integer> colorsList=new ArrayList<>();
        if(income!=0){
            pieEntryList.add(new PieEntry(income,"Income"));
            colorsList.add(getResources().getColor(R.color.teal_200));
        }
        if(expense!=0){
            pieEntryList.add(new PieEntry(expense,"Expense"));
            colorsList.add(getResources().getColor(R.color.orange));
        }
        PieDataSet pieDataSet= new PieDataSet(pieEntryList,String.valueOf(income=expense));
        pieDataSet.setColors(colorsList);
        pieDataSet.setValueTextColor(getResources().getColor(R.color.white));
        pieDataSet.setValueTextSize(15);
        PieData pieDat= new PieData(pieDataSet);


        binding.pieChart.setData(pieDat);
        binding.pieChart.invalidate();
    }


    @Override
    public void onClick(ExpenseModel expenseModel) {
        Intent intent = new Intent(Dashboard.this,AddExpenseActivity.class);
        intent.putExtra("model",expenseModel);
        startActivity(intent);
    }
}