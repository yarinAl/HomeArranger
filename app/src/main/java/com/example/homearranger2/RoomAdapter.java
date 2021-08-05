package com.example.homearranger2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private ArrayList<Room> mRoomList;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTextViewHeader;
        public TextView mTextViewDescription;
        public ImageView delete;

        public RoomViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageViewRoom);
            mTextViewHeader = itemView.findViewById(R.id.headerTextRoom);
            mTextViewDescription = itemView.findViewById(R.id.descriptionRoom);
            delete=itemView.findViewById(R.id.btnDelete);

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
        }
    }

    public RoomAdapter(ArrayList<Room> roomList) {
        mRoomList = roomList;
    };

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_services, parent, false);
        RoomViewHolder rvh = new RoomViewHolder(v,mListener);
        return rvh;
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        Room currentRoom = mRoomList.get(position);
        holder.mImageView.setImageResource(currentRoom.getImageResource());
        holder.mTextViewHeader.setText(currentRoom.getTextHeader());
        holder.mTextViewDescription.setText(currentRoom.getTextHeader());
    }

    @Override
    public int getItemCount() {
        return mRoomList.size();
    }

}
