package com.example.condiplant;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class Reports_RecyclerViewAdapter extends RecyclerView.Adapter<Reports_RecyclerViewAdapter.MyViewHolder> {



    Context context;
    ArrayList<ReportsModel> reportsModelsList;

    public Reports_RecyclerViewAdapter(Context context, ArrayList<ReportsModel> reportsModelList){
        this.context = context;
        this.reportsModelsList = reportsModelList;
    }



    @NonNull
    @Override
    public Reports_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This is where you inflate the layout (Giving a look to your rows)

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_report, parent, false);

        return new Reports_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Reports_RecyclerViewAdapter.MyViewHolder holder, int position) {
        //Assigning values to the views we created in the recycler_view_row layout file
        //based on the position of the recyclerview

        holder.txtPlantName.setText(reportsModelsList.get(position).getPlantName());
        holder.txtDiseaseName.setText(reportsModelsList.get(position).getDiseaseName());
        //holder.txtCount.setText(String.valueOf(reportsModelsList.get(position).getCount()));
        holder.txtCount.setText("0"); //Temporarily Zero


    }

    @Override
    public int getItemCount() {
        //the recyclerview just want to know the number of items you want to be displayed
        //return 0;
        return reportsModelsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        //Grabbing the views from our recycler_view_row layout file
        //kinda like in the onCreate method

        TextView txtPlantName;
        TextView txtDiseaseName;
        TextView txtCount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPlantName = itemView.findViewById(R.id.txtPlantName);
            txtDiseaseName = itemView.findViewById(R.id.txtDiseaseName);
            txtCount = itemView.findViewById(R.id.txtCount);
        }
    }
}
