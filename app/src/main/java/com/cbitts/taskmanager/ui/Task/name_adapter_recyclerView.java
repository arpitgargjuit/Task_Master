package com.cbitts.taskmanager.ui.Task;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cbitts.taskmanager.R;

import java.util.ArrayList;

public class name_adapter_recyclerView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Name_ModelClass> userData;
    Add_task_generator addTaskObj;
    Context context;
    viewHolder x;

    public name_adapter_recyclerView(Context context, ArrayList<Name_ModelClass> userData, Add_task_generator x) {
        this.userData = userData;
        addTaskObj = x;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_list,parent,false);
        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
     final viewHolder itemViewHolder = (viewHolder)holder;
     try {
         final Name_ModelClass current_user = userData.get(position);
         itemViewHolder.name.setText(current_user.getName());
         itemViewHolder.name_view.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (x != null) {
                     x.name.setTextColor(Color.BLACK);
                 }
                 addTaskObj.setUid(current_user.getUid());
                 addTaskObj.setName(current_user.getName());
                 Toast.makeText(context, current_user.getName() + " selected", Toast.LENGTH_SHORT).show();
                 itemViewHolder.name.setTextColor(Color.BLUE);
                 x = itemViewHolder;
             }
         });
     }
     catch (Exception e){
         Log.d("Exception",e.toString());
     }
    }

    @Override
    public int getItemCount() {
        Log.d("Size of",""+userData.size());
        return userData.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView name;
        LinearLayout name_view;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name =itemView.findViewById(R.id.textView_name);
            name_view = itemView.findViewById(R.id.name_layout);
        }
    }

    public void FilterList(ArrayList<Name_ModelClass> filterList){
        userData = filterList;
        notifyDataSetChanged();
    }
}
