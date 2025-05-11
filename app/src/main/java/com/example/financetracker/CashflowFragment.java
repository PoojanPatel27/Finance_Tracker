package com.example.financetracker;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CashflowFragment extends Fragment {

    private PieChart pieChart;
    private DatabaseReference budgetRef;
    private String userId;
    private String budgetUID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cashflow,container,false);
        pieChart = view.findViewById(R.id.pieChart);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();



        budgetRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("Budget");

        if (getArguments() != null) {
            budgetUID = getArguments().getString("budgetUID");
        }

        if (budgetUID != null) {
            loadPieChart2(); // Only load if budgetUID is not null
        } else {
            Toast.makeText(getContext(), "No budget selected", Toast.LENGTH_SHORT).show();
        }

//        loadPieChart();
//        loadPieChart2();

        return view;
    }

    private void loadPieChart2() {
        budgetRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("Budget")
                .child(budgetUID);  // Use the selected budgetUID

        // Fetch data for the selected budget
        budgetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot planningItemsSnap = snapshot.child("planningItems");

                    ArrayList<PieEntry> entries = new ArrayList<>();
                    float total = 0f;
                    Map<String, Float> categoryMap = new HashMap<>();

                    // Process each planning item under the selected budget
                    for (DataSnapshot itemSnap : planningItemsSnap.getChildren()) {
                        String title = itemSnap.child("title").getValue(String.class);
                        Float value = itemSnap.child("value").getValue(Float.class);
                        if (title != null && value != null) {
                            categoryMap.put(title, value);
                            total += value;
                        }
                    }

                    // Add entries to the chart
                    for (Map.Entry<String, Float> entry : categoryMap.entrySet()) {
                        entries.add(new PieEntry(entry.getValue(), entry.getKey()));
                    }

                    // Create PieDataSet and apply to the PieChart
                    PieDataSet dataSet = new PieDataSet(entries, "Expenditures");
                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    dataSet.setValueTextColor(Color.WHITE);
                    dataSet.setValueTextSize(14f);

                    PieData data = new PieData(dataSet);
                    pieChart.setData(data);
                    pieChart.setUsePercentValues(true);
                    pieChart.setCenterText("Expenditures");
                    pieChart.animateY(1000, Easing.EaseInOutCubic);
                    pieChart.invalidate();
                } else {
                    Toast.makeText(getContext(), "No data available for the selected budget", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load chart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPieChart() {
        budgetRef.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot budgetSnap : snapshot.getChildren()) {
                    DataSnapshot planningItemsSnap = budgetSnap.child("planningItems");

                    ArrayList<PieEntry> entries = new ArrayList<>();
                    float total = 0f;
                    Map<String, Float> categoryMap = new HashMap<>();

                    for (DataSnapshot itemSnap : planningItemsSnap.getChildren()) {
                        String title = itemSnap.child("title").getValue(String.class);
                        Float value = itemSnap.child("value").getValue(Float.class);
                        if (title != null && value != null) {
                            categoryMap.put(title, value);
                            total += value;
                        }
                    }

                    for (Map.Entry<String, Float> entry : categoryMap.entrySet()) {
                        entries.add(new PieEntry(entry.getValue(), entry.getKey()));
                    }

                    PieDataSet dataSet = new PieDataSet(entries, "Expenditures");
                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    dataSet.setValueTextColor(Color.WHITE);
                    dataSet.setValueTextSize(14f);

                    PieData data = new PieData(dataSet);
                    pieChart.setData(data);
                    pieChart.setUsePercentValues(true);
                    pieChart.setCenterText("Expenditures");
                    pieChart.animateY(1000, Easing.EaseInOutCubic);
                    pieChart.invalidate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load chart", Toast.LENGTH_SHORT).show();
            }
        });
    }
}