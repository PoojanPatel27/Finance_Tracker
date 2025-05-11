package com.example.financetracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class InsightsActivity extends AppCompatActivity {

   private BottomNavigationView bottomNav ;
   private String budgetUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_insights);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNav = findViewById(R.id.bottom_navigation);

        Intent intent = getIntent();
        budgetUID = intent.getStringExtra("budgetUID");

        Bundle bundle = new Bundle();
        bundle.putString("budgetUID", budgetUID);

        CashflowFragment cashflowFragment = new CashflowFragment();
        cashflowFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, cashflowFragment).commit();

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            if (item.getItemId() == R.id.nav_cashflow) {
                Bundle bundle1= new Bundle();
                bundle1.putString("budgetUID", budgetUID);
                CashflowFragment fragment = new CashflowFragment();
                fragment.setArguments(bundle1);
                selected = fragment;
            } else if (item.getItemId() == R.id.nav_progression) {
                Bundle bundle2 = new Bundle();
                bundle2.putString("budgetUID", budgetUID);
                ProgressionFragment fragment = new ProgressionFragment();
                fragment.setArguments(bundle2);
                selected = fragment;
            }

            if (selected != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selected).commit();
                return true;
            }
            return false;
        });


    }
}