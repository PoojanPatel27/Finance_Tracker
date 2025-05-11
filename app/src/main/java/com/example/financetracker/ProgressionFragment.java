package com.example.financetracker;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class ProgressionFragment extends Fragment {

    LineChart dailyChart, cumulativeChart;
    String budgetUID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_progression, container, false);

        dailyChart = view.findViewById(R.id.dailyChart);
        cumulativeChart = view.findViewById(R.id.cumulativeChart);

        if (getArguments() != null) {
            budgetUID = getArguments().getString("budgetUID");
        }

        if (budgetUID != null) {
            loadChartData();
        } else {
            Toast.makeText(getContext(), "No budget selected", Toast.LENGTH_SHORT).show();
        }

        loadChartData();


        return  view;
    }

    private void loadChartData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || budgetUID == null) return;

        String uid = user.getUid();
        DatabaseReference planningRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .child("Budget")
                .child(budgetUID)
                .child("planningItems");

        planningRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Float> dailyTotals = new TreeMap<>(); // Sorted by date

                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    String date = itemSnap.child("date").getValue(String.class);
                    Float value = itemSnap.child("value").getValue(Float.class);

                    if (date != null && value != null) {
                        float current = dailyTotals.containsKey(date) ? dailyTotals.get(date) : 0f;
                        dailyTotals.put(date, current + value);
                    }
                }

                // Create chart entries
                List<Entry> dailyEntries = new ArrayList<>();
                List<Entry> cumulativeEntries = new ArrayList<>();
                float cumulativeTotal = 0f;
                int index = 0;

                for (Map.Entry<String, Float> entry : dailyTotals.entrySet()) {
                    cumulativeTotal += entry.getValue();
                    dailyEntries.add(new Entry(index, entry.getValue()));
                    cumulativeEntries.add(new Entry(index, cumulativeTotal));
                    index++;
                }

                // Daily Expenditure
                LineDataSet dailyDataSet = new LineDataSet(dailyEntries, "Daily Expenditure");
                dailyDataSet.setColor(Color.BLUE);
                dailyDataSet.setCircleColor(Color.BLUE);
                dailyDataSet.setValueTextSize(10f);
                LineData dailyData = new LineData(dailyDataSet);
                dailyChart.setData(dailyData);
                dailyChart.invalidate();

                // Cumulative Expenditure
                LineDataSet cumDataSet = new LineDataSet(cumulativeEntries, "Cumulative Expenditure");
                cumDataSet.setColor(Color.BLUE);
                cumDataSet.setFillAlpha(100);
                cumDataSet.setDrawFilled(true);
                cumDataSet.setCircleColor(Color.BLUE);
                cumDataSet.setValueTextSize(10f);
                LineData cumData = new LineData(cumDataSet);
                cumulativeChart.setData(cumData);
                cumulativeChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}