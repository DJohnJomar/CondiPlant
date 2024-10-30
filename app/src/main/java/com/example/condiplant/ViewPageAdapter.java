package com.example.condiplant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ViewPageAdapter extends RecyclerView.Adapter<ViewPageAdapter.ViewHolder> {

    ArrayList<ViewPageItem> viewPageItemArrayList;

    public ViewPageAdapter(ArrayList<ViewPageItem> viewPageItemArrayList) {
        this.viewPageItemArrayList = viewPageItemArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.guide_page_format, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewPageItem viewPageItem = viewPageItemArrayList.get(position);

        holder.imageView.setImageResource(viewPageItem.image);
        holder.tvTitle.setText(viewPageItem.title);
        holder.tvDescription.setText(viewPageItem.description);
    }

    @Override
    public int getItemCount() {
        return viewPageItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView tvTitle, tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgGuide);
            tvTitle = itemView.findViewById(R.id.txtStepTitle);
            tvDescription = itemView.findViewById(R.id.txtGuideDesc);
        }
    }
}
