package com.example.financetracker;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowSavedBudget extends AppCompatActivity {
   private RecyclerView recyclerView;
    DatabaseReference budgetRef;
    List<ShowSavedBudgetModel> budgetList = new ArrayList<>();
    ShowSavedBudgetAdapter adapter;
    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_saved_budget);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        budgetRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Budget");

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                budgetList.clear();
                for (DataSnapshot budgetSnap : snapshot.getChildren()) {
                    String startDate = budgetSnap.child("startDate").getValue(String.class);
                    String endDate = budgetSnap.child("endDate").getValue(String.class);
                    String totalBudget = budgetSnap.child("totalBudget").getValue(String.class);
                    String budgetUID = budgetSnap.child("BudgetUID").getValue(String.class);

                    ShowSavedBudgetModel budgetModel = new ShowSavedBudgetModel(startDate, endDate, totalBudget,budgetUID);
                    budgetList.add(budgetModel);
                }
                adapter = new ShowSavedBudgetAdapter(budgetList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowSavedBudget.this, "error retriving data "+error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}