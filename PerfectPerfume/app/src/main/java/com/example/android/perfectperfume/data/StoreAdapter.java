package com.example.android.perfectperfume.data;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.perfectperfume.ui.DetailActivity;
import com.example.android.perfectperfume.R;
import com.example.android.perfectperfume.ui.StoreActivity;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    List<Perfume> perfumes;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout layout;
        // setting up the views to avoid calling findViewById many times in onBindViewHolder
        ImageView imageView;
        TextView perfumeNameTextView;
        TextView perfumeSexTextView;
        TextView perfumePriceTextView;

        ViewHolder(RelativeLayout layout) {
            super(layout);
            this.layout = layout;
            imageView = layout.findViewById(R.id.perfume_imageview);
            perfumeNameTextView = layout.findViewById(R.id.perfume_name_textview);
            perfumeSexTextView = layout.findViewById(R.id.perfume_sex_textview);
            perfumePriceTextView = layout.findViewById(R.id.perfume_price_textview);
        }
    }

    public StoreAdapter(Context context, List<Perfume> perfumes) {
        this.context = context;
        this.perfumes = perfumes;
    }

    @Override
    public StoreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        ViewHolder vh = new ViewHolder(layout);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Perfume perfume = perfumes.get(position);
        holder.perfumeNameTextView.setText(perfume.getName() + " by " + perfume.getBrand());
        holder.perfumeSexTextView.setText("For " + perfume.getSex());
        holder.perfumePriceTextView.setText("€" + perfume.getPrice());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(StoreActivity.DETAIL_INTENT_TAG, perfume);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return perfumes.size();
    }
}
