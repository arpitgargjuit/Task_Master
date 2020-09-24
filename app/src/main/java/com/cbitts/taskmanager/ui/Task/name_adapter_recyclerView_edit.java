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

public class name_adapter_recyclerView_edit extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<String> Name;
    ArrayList<String> Uid;
    Add_task_generator addTaskObj;
    Context context;
    viewHolder x;
    String init_id, init_name;

    public name_adapter_recyclerView_edit(Context context, ArrayList<String> name, ArrayList<String> uid, Add_task_generator x, String init_id, String init_name) {
        Name = name;
        Uid = uid;
        addTaskObj = x;
        this.context = context;
        this.init_id = init_id;
        this.init_name = init_name;
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
            itemViewHolder.name.setText(Name.get(position));
            if (Uid.get(position).equals(init_id)){
                addTaskObj.setUid(init_id);
                itemViewHolder.name.setTextColor(Color.BLUE);
                x = itemViewHolder;

            }
            else {
                itemViewHolder.name.setText(Name.get(position));
            }
                itemViewHolder.name_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (x != null) {
                            x.name.setTextColor(Color.BLACK);
                        }
                        addTaskObj.setUid(Uid.get(position));
                        Toast.makeText(context, Name.get(position) + " selected", Toast.LENGTH_SHORT).show();
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
        Log.d("Size of",""+Name.size());
        return Name.size();
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
}
