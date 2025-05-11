package com.example.financetracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class CreateBudget extends AppCompatActivity {
    private TextInputEditText startDate,endDate;
    private TextView next;
    private EditText budgetGoalET;
    private Button upload;

    private String budgetUID;
    private String budgetUIDIntent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_budget);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startDate = findViewById(R.id.startDateET);
        endDate = findViewById(R.id.endDateET);
        next = findViewById(R.id.nextTV);
        budgetGoalET = findViewById(R.id.budgetGoalET);
        upload = findViewById(R.id.saveBtn);

        Intent intent = getIntent();
        String startDateIntent = intent.getStringExtra("startDate");
        String endDateIntent = intent.getStringExtra("endDate");
        String totalBudgetIntent = intent.getStringExtra("totalBudget");
        budgetUIDIntent = intent.getStringExtra("budgetUID");



        if (startDateIntent!=null){
            startDate.setText(startDateIntent);
            endDate.setText(endDateIntent);
            budgetGoalET.setText(totalBudgetIntent);
            upload.setVisibility(View.INVISIBLE);
        }


        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CreateBudget.this,
                        (view1, year1, month1, dayOfMonth) -> {
                            // month is 0-indexed
                            String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                            startDate.setText(selectedDate);
                        },
                        year, month, day
                );
                datePickerDialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CreateBudget.this,
                        (view1, year1, month1, dayOfMonth) -> {
                            // month is 0-indexed
                            String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                            endDate.setText(selectedDate);
                        },
                        year, month, day
                );
                datePickerDialog.show();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtStartDate = startDate.getText().toString().trim();
                String txtEndDate = endDate.getText().toString().trim();
                String txtTotalMonthlyBudget = budgetGoalET.getText().toString().toString();

                uploadBudgetToFirebase(txtStartDate, txtEndDate, txtTotalMonthlyBudget);

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txtStartDate = startDate.getText().toString().trim();
                String txtEndDate = endDate.getText().toString().trim();
                String txtTotalMonthlyBudget = budgetGoalET.getText().toString().toString();

                if (txtTotalMonthlyBudget.isEmpty()){
                    Toast.makeText(CreateBudget.this, "Enter total Budget", Toast.LENGTH_SHORT).show();
                    budgetGoalET.requestFocus();
                } else if (txtStartDate.isEmpty()) {
                    Toast.makeText(CreateBudget.this, "Enter Start Date", Toast.LENGTH_SHORT).show();
                    startDate.requestFocus();
                } else if (txtEndDate.isEmpty()) {
                    Toast.makeText(CreateBudget.this, "Enter End Date", Toast.LENGTH_SHORT).show();
                    endDate.requestFocus();
                } else {
                    Intent intent = new Intent(getApplicationContext(),InitialPlanning.class);
                    intent.putExtra("totalBudget",txtTotalMonthlyBudget);
                    intent.putExtra("startDate",txtStartDate);
                    intent.putExtra("endDate",txtEndDate);
                    intent.putExtra("budgetUIDIntent",budgetUIDIntent);
                    intent.putExtra("budgetUID",budgetUID);
                    startActivity(intent);
                }

            }
        });
    }

    private void uploadBudgetToFirebase(String txtStartDate, String txtEndDate, String txtTotalMonthlyBudget) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Budget");

         budgetUID = dbRef.push().getKey();

        HashMap<String, Object> budgetMap = new HashMap<>();
        budgetMap.put("startDate", txtStartDate);
        budgetMap.put("endDate", txtEndDate);
        budgetMap.put("totalBudget", txtTotalMonthlyBudget);
        budgetMap.put("BudgetUID",budgetUID);

        dbRef.child(budgetUID).setValue(budgetMap).addOnSuccessListener(aVoid -> {
            Toast.makeText(CreateBudget.this, "Budget data saved", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(CreateBudget.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

}