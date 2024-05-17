package com.example.lostandfound;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class LostItemAdapter extends RecyclerView.Adapter<LostItemAdapter.LostItemsViewHolder> {

    public ArrayList<LostItem> lostItems;

    private Context context;

    public LostItemAdapter(Context context, ArrayList<LostItem> items) {
        this.lostItems = items;
        this.context = context;
    }


    @NonNull
    @Override
    public LostItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lost_item_fragment, parent, false);
        return new LostItemsViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull LostItemAdapter.LostItemsViewHolder holder, int position) {
        LostItem lostItem = lostItems.get(position);
        holder.bind(lostItem);
    }

    @Override
    public int getItemCount() {
        return lostItems.size();
    }

    public class LostItemsViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlTaskView;

        private TextView tvTimeReported;
        private TextView tvTitle;
        private TextView tvDescription;
        private TextView tvLocation;

        private Button btnViewReport;


        private Context context;

        public LostItemsViewHolder(Context context, @NonNull View itemView) {
            super(itemView);
            tvTimeReported = itemView.findViewById(R.id.tvTimeReported);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnViewReport = itemView.findViewById(R.id.btnViewReport);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            this.context = context;
        }

        public void bind(LostItem lostItem) {
            tvTimeReported.setText(lostItem.formatTimeAgo());
            tvTitle.setText(lostItem.getItemName());
            tvDescription.setText(lostItem.getDescription());
            tvLocation.setText(lostItem.getLocation().getLocationName());
            btnViewReport.setOnClickListener(view ->{
                Intent intent = new Intent(view.getContext(), ViewLostItemActivity.class);
                intent.putExtra(ViewLostItemActivity.EXTRA_LOST_ITEM_ID, lostItem.getId());
                context.startActivity(intent);
                ((Activity)context).finish();
            });
        }

    }

}