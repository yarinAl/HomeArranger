package com.example.homearranger2;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private ArrayList<Product> mItemList;
    private ItemAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
        void onFavouritesClick(int position);
    }
    public void setOnItemClickListener(ItemAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {


        public TextView nameView;
        public TextView amountView;
        public TextView locationView;
        public TextView DateView;
        public ImageView delete;
        public ImageView favourites;
        public ItemViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            favourites=itemView.findViewById(R.id.btnImps);
            delete=itemView.findViewById(R.id.btnDelete2);
            nameView = itemView.findViewById(R.id.item_name);
            amountView = itemView.findViewById(R.id.item_amount);
            locationView= itemView.findViewById(R.id.item_location);
            DateView= itemView.findViewById(R.id.item_date);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
            favourites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onFavouritesClick(position);
                        }
                    }
                }
            });
        }
    }


    public ItemAdapter(ArrayList<Product> itemList) {
        mItemList = itemList;
    };

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ItemAdapter.ItemViewHolder rvh = new ItemAdapter.ItemViewHolder(v,mListener);
        return rvh;
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ItemViewHolder holder, int position) {
        Product currentItem = mItemList.get(position);
        holder.nameView.setText(currentItem.getName());
        holder.amountView.setText(String.valueOf(currentItem.getAmount()));
        holder.locationView.setText(currentItem.getLocation());
        holder.DateView.setText(currentItem.getDate());


    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }
}