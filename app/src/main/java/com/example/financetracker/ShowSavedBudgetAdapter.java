package com.example.financetracker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class ShowSavedBudgetAdapter extends RecyclerView.Adapter<ShowSavedBudgetAdapter.BudgetViewHolder> {

    List<ShowSavedBudgetModel> budgetList;

    public ShowSavedBudgetAdapter(List<ShowSavedBudgetModel> budgetList) {
        this.budgetList = budgetList;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_show_saved_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        ShowSavedBudgetModel budget = budgetList.get(position);
        holder.startDate.setText("Start: " + budget.getStartDate());
        holder.endDate.setText("End: " + budget.getEndDate());
        holder.totalBudget.setText("Budget: ₹" + budget.getTotalBudget());



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, CreateBudget.class);
                intent.putExtra("startDate", budget.getStartDate());
                intent.putExtra("endDate", budget.getEndDate());
                intent.putExtra("totalBudget", budget.getTotalBudget());
                intent.putExtra("budgetUID",budget.getBudgetUID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return budgetList.size();
    }

    public static class BudgetViewHolder extends RecyclerView.ViewHolder{

        TextView startDate, endDate, totalBudget;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);

            startDate = itemView.findViewById(R.id.startDateTV_item);
            endDate = itemView.findViewById(R.id.endDateTV_item);
            totalBudget = itemView.findViewById(R.id.totalBudgetTV_item);
        }
    }

}
