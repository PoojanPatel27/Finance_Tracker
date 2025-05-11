package com.example.financetracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitialPlanning extends AppCompatActivity {
    private SeekBar seekBar;
    private EditText seekBarValue,seekbarTitle,DateET;
    private TextView incomeGoal,date,next;
    private Button buttonSave;
    private RecyclerView recyclerView;
    private List<InitialPlanningDataModel> dataList;
    private InitialPlaningListDataAdapter dataAdapter;
    private Button upload;
    private String budgetUID3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_initial_planning);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        

        date = findViewById(R.id.dateTV);
        seekBar = findViewById(R.id.seekBar);
        seekBarValue = findViewById(R.id.seekBarValue);
        seekbarTitle = findViewById(R.id.editTextTitle);
        DateET = findViewById(R.id.editTextDate2);
        next = findViewById(R.id.nextTV);
        incomeGoal = findViewById(R.id.incomeGoalTV);
        buttonSave = findViewById(R.id.buttonSave);
        recyclerView = findViewById(R.id.recyclerView);
        upload = findViewById(R.id.saveBtn);

        dataList = new ArrayList<>();
        dataAdapter = new InitialPlaningListDataAdapter(dataList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dataAdapter);

        Intent intent1 = getIntent();
        incomeGoal.setText("Total Income Goal: "+intent1.getStringExtra("totalBudget")+" ₹");
        date.setText("From: "+intent1.getStringExtra("startDate")+" TO "+intent1.getStringExtra("endDate"));

        seekBar.setProgress(1);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 1) {
                    seekBar.setProgress(1); // Minimum 1
                }
                seekBarValue.setText(String.valueOf(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        seekBarValue.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int value = Integer.parseInt(s.toString());
                    if (value >= 1 && value <= 100000) {
                        seekBar.setProgress(value);
                    }
                } catch (NumberFormatException ignored) {
                }
            }

            @Override public void afterTextChanged(Editable s) {}
        });


        buttonSave.setOnClickListener(v -> {
            String title = seekbarTitle.getText().toString().trim();
            String selectedDate = DateET.getText().toString().trim();
            int value = seekBar.getProgress();

            if (title.isEmpty()) {
                Toast.makeText(InitialPlanning.this, "Please enter a title", Toast.LENGTH_SHORT).show();
            } else if (selectedDate.isEmpty()) {
                Toast.makeText(InitialPlanning.this, "Please select a date", Toast.LENGTH_SHORT).show();
            } else {
                dataList.add(new InitialPlanningDataModel(title, value,selectedDate));
                dataAdapter.notifyItemInserted(dataList.size() - 1);
                seekbarTitle.setText("");
                seekBar.setProgress(1);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String budgetUID = intent1.getStringExtra("budgetUID");

                if (budgetUID != null) {
                    uploadInitialPlanningData(budgetUID);
                } else {
                    Toast.makeText(InitialPlanning.this, "Budget ID missing", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        DateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(InitialPlanning.this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                            DateET.setText(selectedDate);
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String budgetStr = intent1.getStringExtra("totalBudget");


                float totalBudget = 0f;
                if (budgetStr != null) {
                    totalBudget = Float.parseFloat(budgetStr);
                }


                ArrayList<String> titles = new ArrayList<>();
                ArrayList<Integer> values = new ArrayList<>();
                for (InitialPlanningDataModel item : dataList) {
                    titles.add(item.getTitle());
                    values.add(item.getValue());
                }

                Intent chartIntent = new Intent(InitialPlanning.this, ChartView.class);
                chartIntent.putExtra("totalBudget", totalBudget);
                chartIntent.putStringArrayListExtra("expenseTitles", titles);
                chartIntent.putIntegerArrayListExtra("expenseValues", values);
                chartIntent.putExtra("budgetUID",budgetUID3);


                startActivity(chartIntent);
            }
        });


    }

    private void retrieveInitialPlanningData(String uid, String budgetUID) {
        DatabaseReference planningRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Budget").child(budgetUID).child("planningItems");

        planningRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                dataList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String title = itemSnapshot.child("title").getValue(String.class);
                    Long valueLong = itemSnapshot.child("value").getValue(Long.class);
                    String txtDate = itemSnapshot.child("date").getValue(String.class);

                    if (title != null && valueLong != null) {
                        int value = valueLong.intValue();
                        dataList.add(new InitialPlanningDataModel(title, value,txtDate));

                    }
                }
                dataAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "No planning data found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to retrieve data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadInitialPlanningData(String budgetUID) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference planningRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .child("Budget")
                .child(budgetUID)
                .child("planningItems");

        for (InitialPlanningDataModel item : dataList) {
            String key = planningRef.push().getKey(); // Unique key for each item
            if (key != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("title", item.getTitle());
                map.put("value", item.getValue());
                map.put("date",item.getDate());

                planningRef.child(key).setValue(map)
                        .addOnSuccessListener(aVoid -> {
                            // Success - you can log or show a Toast
                            Toast.makeText(this, "Data Uploaded Success", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            // Failure - handle the error
                            Toast.makeText(this, "Data Uploaad Failed !!"+e, Toast.LENGTH_SHORT).show();
                        });
            }

        }




    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        budgetUID3 = intent.getStringExtra("budgetUIDIntent");

        if (uid != null && budgetUID3 != null) {
            retrieveInitialPlanningData(uid, budgetUID3);
//            Toast.makeText(this, ""+budgetUID3, Toast.LENGTH_SHORT).show();
        }
        else
        {
//            Toast.makeText(this, "UID NULL", Toast.LENGTH_SHORT).show();
        }
        
    }
}