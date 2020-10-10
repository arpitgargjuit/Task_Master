package com.cbitts.taskmanager.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cbitts.taskmanager.R;

import java.util.ArrayList;

public class RecyclerViewNotifications extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    ArrayList<String> notifications;
    ArrayList<String> date;

    public RecyclerViewNotifications(ArrayList<String> notifications, ArrayList<String> date) {
        this.notifications = notifications;
        this.date = date;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_adapter_layout,parent,false);

        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        viewHolder itemViewHolder = (viewHolder)holder;
        try {
            itemViewHolder.title.setText(notifications.get(position));
            itemViewHolder.date.setText(date.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView title,date;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notification_text);
            date = itemView.findViewById(R.id.date);
        }
    }
}
