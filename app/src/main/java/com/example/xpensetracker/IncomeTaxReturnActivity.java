package com.example.xpensetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.xpensetracker.databinding.ActivityIncomeTaxReturnBinding;

public class IncomeTaxReturnActivity extends AppCompatActivity {
    ActivityIncomeTaxReturnBinding binding;
    private EditText editTextIncome;
    private Button buttonCalculateReturn;
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_tax_return);

        editTextIncome = findViewById(R.id.editTextIncome);
        buttonCalculateReturn = findViewById(R.id.Calculatebtn);
        textViewResult = findViewById(R.id.textViewResult);

        buttonCalculateReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateTaxReturn();
            }
        });
    }

    private void calculateTaxReturn() {
        try {
            double annualIncome = Double.parseDouble(editTextIncome.getText().toString());
            // Perform your income tax return calculation logic here
            double taxReturn = calculateIncomeTaxReturn(annualIncome);
            textViewResult.setText("Tax Return: INR:  " + taxReturn);
        } catch (NumberFormatException e) {
            textViewResult.setText("Please enter a valid annual income.");
        }
    }

    private double calculateIncomeTaxReturn(double annualIncome) {

        double bracket1Max = 250000;  // 0% tax
        double bracket2Max = 500000;  // 5% tax
        double bracket3Max = 750000;  // 10% tax
        double bracket4Max = 1000000; // 15% tax
        double bracket5Max = 1250000; // 20% tax
        double bracket6Max = 1500000; // 25% tax
        double bracket7Max = Double.MAX_VALUE; // 30% tax

        double taxRateBracket1 = 0.0;
        double taxRateBracket2 = 0.05;
        double taxRateBracket3 = 0.10;
        double taxRateBracket4 = 0.15;
        double taxRateBracket5 = 0.20;
        double taxRateBracket6 = 0.25;
        double taxRateBracket7 = 0.30;

        double taxPaid = 0.0;

        if (annualIncome <= bracket1Max) {
            // No tax in the first bracket
            taxPaid = 0.0;
        } else if (annualIncome <= bracket2Max) {
            // Calculate tax in the second bracket
            taxPaid = (annualIncome - bracket1Max) * taxRateBracket2;
        } else if (annualIncome <= bracket3Max) {
            // Calculate tax in the third bracket
            taxPaid = (bracket2Max - bracket1Max) * taxRateBracket2 +
                    (annualIncome - bracket2Max) * taxRateBracket3;
        } else if (annualIncome <= bracket4Max) {
            // Calculate tax in the fourth bracket
            taxPaid = (bracket2Max - bracket1Max) * taxRateBracket2 +
                    (bracket3Max - bracket2Max) * taxRateBracket3 +
                    (annualIncome - bracket3Max) * taxRateBracket4;
        } else if (annualIncome <= bracket5Max) {
            // Calculate tax in the fifth bracket
            taxPaid = (bracket2Max - bracket1Max) * taxRateBracket2 +
                    (bracket3Max - bracket2Max) * taxRateBracket3 +
                    (bracket4Max - bracket3Max) * taxRateBracket4 +
                    (annualIncome - bracket4Max) * taxRateBracket5;
        } else if (annualIncome <= bracket6Max) {
            // Calculate tax in the sixth bracket
            taxPaid = (bracket2Max - bracket1Max) * taxRateBracket2 +
                    (bracket3Max - bracket2Max) * taxRateBracket3 +
                    (bracket4Max - bracket3Max) * taxRateBracket4 +
                    (bracket5Max - bracket4Max) * taxRateBracket5 +
                    (annualIncome - bracket5Max) * taxRateBracket6;
        } else if(annualIncome >= bracket6Max){
            // Calculate tax in the seventh bracket
            taxPaid = (bracket2Max - bracket1Max) * taxRateBracket2 +
                    (bracket3Max - bracket2Max) * taxRateBracket3 +
                    (bracket4Max - bracket3Max) * taxRateBracket4 +
                    (bracket5Max - bracket4Max) * taxRateBracket5 +
                    (bracket6Max - bracket5Max) * taxRateBracket6 +
                    (annualIncome - bracket6Max) * taxRateBracket7;
        }
        double returnRate = 1;
        double taxReturn = taxPaid * returnRate;
        return taxReturn;
    }
}
