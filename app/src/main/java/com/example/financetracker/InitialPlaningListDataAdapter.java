package com.example.financetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InitialPlaningListDataAdapter extends RecyclerView.Adapter<InitialPlaningListDataAdapter.ViewHolder> {

    private List<InitialPlanningDataModel> dataList;

    public InitialPlaningListDataAdapter(List<InitialPlanningDataModel> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_initial_planning_data,parent,false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        InitialPlanningDataModel dataModel = dataList.get(position);
        holder.title.setText(dataModel.getTitle());
        holder.value.setText(String.valueOf(dataModel.getValue()));
        holder.date.setText(String.valueOf(dataModel.getDate()));

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (pos!=RecyclerView.NO_POSITION){
                    dataList.remove(pos);
                    notifyItemRemoved(pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView title,value,date;
        ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textTitle);
            value = itemView.findViewById(R.id.textValue);
            delete = itemView.findViewById(R.id.buttonDelete);
            date = itemView.findViewById(R.id.textDate_item);

        }
    }
}
