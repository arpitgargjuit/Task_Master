package com.cbitts.taskmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class recyclerView_adapter_tasks extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    ArrayList<String> Title;
    ArrayList<String> Description;
    ArrayList<String> Date;
    ArrayList<String> Priority;

    public recyclerView_adapter_tasks(ArrayList<String> title, ArrayList<String> description, ArrayList<String> date,ArrayList<String> priority) {
        Title = title;
        Description = description;
        Date = date;
        Priority = priority;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_task_item,parent,false);
        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        viewHolder itemViewHolder = (viewHolder)holder;
        itemViewHolder.title.setText(Title.get(position));
        itemViewHolder.description.setText(Description.get(position));
        itemViewHolder.date.setText(Date.get(position));
    }

    @Override
    public int getItemCount() {
        return Title.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView title,description,date;
        Button Complete;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            title =itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.due_date);
            Complete = itemView.findViewById(R.id.complete);
        }
    }
}
