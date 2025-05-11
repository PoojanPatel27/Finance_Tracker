package com.example.financetracker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class ChartView extends AppCompatActivity {

    private PieChart pieChartSaving, pieChartSpending;
    private TextView incomeSpent,netDisposableIncome,totalExpenditure,remainingToSpend,provisionalBalance,totalBudget;
    private Button showInsights;
    private String budgetUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chart_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        budgetUID = intent.getStringExtra("budgetUID");
        if (budgetUID!=null){
//            Toast.makeText(this, ""+budgetUID, Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(this, "UID NULL", Toast.LENGTH_SHORT).show();
        }

        pieChartSaving = findViewById(R.id.pieChartSaving);
        pieChartSpending = findViewById(R.id.pieChartSpending);

        incomeSpent = findViewById(R.id.incomeSpentDataTV);
        netDisposableIncome = findViewById(R.id.netDisposableIncomeDataTV);
        totalExpenditure = findViewById(R.id.totalExpenditureDataTV);
        remainingToSpend = findViewById(R.id.remainingToSpendDataTV);
        provisionalBalance = findViewById(R.id.provisionalBalanceDataTV);
        totalBudget = findViewById(R.id.totalBudgetDataTV);
        showInsights = findViewById(R.id.showInsightsBtn);

        float totalBudget = getIntent().getFloatExtra("totalBudget", 0f);
        ArrayList<String> titles = getIntent().getStringArrayListExtra("expenseTitles");
        ArrayList<Integer> values = getIntent().getIntegerArrayListExtra("expenseValues");

        float totalSpent = 0f;
        for (int value : values) {
            totalSpent += value;
        }

        float saving = totalBudget - totalSpent;
        float spentPercent = (totalSpent / totalBudget) * 100f;

        setupSavingChart(saving, totalSpent);
        setupSpendingChart(spentPercent);

        showInsights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), InsightsActivity.class);
                intent1.putExtra("budgetUID",budgetUID);
                startActivity(intent1);
            }
        });
    }

    private void setupSavingChart(float saving, float spent) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(saving, "Saving"));
        entries.add(new PieEntry(spent, "Spent"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{R.color.green, R.color.grey}, getApplicationContext());
        PieData data = new PieData(dataSet);

        pieChartSaving.setData(data);
        pieChartSaving.setUsePercentValues(true);
        Description description = new Description();
        description.setText("Saving Chart");
        pieChartSaving.setDescription(description);
        pieChartSaving.invalidate();
        pieChartSaving.setDrawCenterText(true);
        pieChartSaving.setCenterText("Saving\n+₹" + saving);
        pieChartSaving.setCenterTextColor(ContextCompat.getColor(this, R.color.green));
        pieChartSaving.setCenterTextSize(16f);
        pieChartSaving.setCenterTextTypeface(Typeface.DEFAULT_BOLD);

    }

    private void setupSpendingChart(float spentPercent) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(spentPercent, "Spent"));
        entries.add(new PieEntry(100 - spentPercent, "Remaining"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{R.color.pink, R.color.grey}, getApplicationContext());
        PieData data = new PieData(dataSet);

        pieChartSpending.setData(data);
        pieChartSpending.setUsePercentValues(true);
        Description description = new Description();
        description.setText("Budget Spent");
        pieChartSpending.setDescription(description);
        pieChartSpending.invalidate();
        pieChartSpending.setDrawCenterText(true);
        pieChartSpending.setCenterText("Budget spent\n" + spentPercent + "%");
        pieChartSpending.setCenterTextColor(ContextCompat.getColor(this, R.color.pink));
        pieChartSpending.setCenterTextSize(16f);
        pieChartSpending.setCenterTextTypeface(Typeface.DEFAULT_BOLD);

//        float saving = getIntent().getFloatExtra("totalBudget", 0f) - spentPercent;
//        float spent = spentPercent;
        float totalBudget = getIntent().getFloatExtra("totalBudget", 0f);
        ArrayList<Integer> values = getIntent().getIntegerArrayListExtra("expenseValues");
        float totalSpent = 0f;
        for (int value : values) {
            totalSpent += value;
        }

        // Calculate saving and pass values
        float saving = totalBudget - totalSpent;



        // Pass the values as parameters to the method
        calculateAndDisplaySummary(saving, totalSpent);


    }

    private void calculateAndDisplaySummary(float saving, float spent) {
        float netIncome = saving + spent;
        float incomeSpentPercent = (spent / (saving + spent)) * 100;
        float totalbudgeted = spent / 0.77f;
        float remainingTospend = totalbudgeted - spent;
        float provisionalbalance = totalbudgeted - netIncome;

        float netDisposableincome = getIntent().getFloatExtra("totalBudget", 0f) - spent;

        incomeSpent.setText(String.format("%.2f",incomeSpentPercent)+"%");
        netDisposableIncome.setText(String.format("%.2f",netDisposableincome)+"₹");
        totalExpenditure.setText(String.format("%.2f",spent)+"₹");
        remainingToSpend.setText(String.format("%.2f",remainingTospend)+"₹");
        provisionalBalance.setText(String.format("%.2f",provisionalbalance));
        totalBudget.setText(String.format("%.2f",totalbudgeted));


    }
}