package com.cbitts.taskmanager.ui.Task;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentController;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cbitts.taskmanager.MainActivity;
import com.cbitts.taskmanager.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class recyclerView_adapter_tasks extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<ModelClass_Task> task_list;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    SharedPreferences getshared;
    String uid;
    int holder_edit;
    final String TAG = "recyclerView_adapter";
    CustomObject object;

    public recyclerView_adapter_tasks(Context context, List<ModelClass_Task> task_list, int holder_edit, CustomObject object) {
        this.context = context;
        this.task_list = task_list;
        this.holder_edit = holder_edit;
        getshared = context.getSharedPreferences("user_details", Context.MODE_PRIVATE);
        uid = getshared.getString("uid", null);
        this.object = object;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_task_item, parent, false);
        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final viewHolder itemViewHolder = (viewHolder) holder;
        final ModelClass_Task task_data = task_list.get(position);
//        if (task_data.getFlag().equals("0")){
//            itemViewHolder.image.setVisibility(View.GONE);
//        }
//        else {
//            try {
//                itemViewHolder.image.setVisibility(View.VISIBLE);
//                Glide.with(context)
//                        .asBitmap()
//                        .load(task_data.getImage())//todo display image here
//                        .into(itemViewHolder.image);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        if (!task_data.getCreated_id().equals(uid)) {
            try {
                Log.d("Test","inside first first");
                itemViewHolder.title.setText(task_data.getTitle());
                itemViewHolder.description.setText(task_data.getDescription());
                itemViewHolder.date.setText(task_data.getDate());
                itemViewHolder.task_to.setVisibility(View.GONE);
                itemViewHolder.task_by.setVisibility(View.VISIBLE);
                itemViewHolder.priority.setText(task_data.getPriority());
                itemViewHolder.name_creator.setText(task_data.getCreated_name());
                String status = task_data.getStatus();
                itemViewHolder.status.setText(status);
                if (!TextUtils.isEmpty(task_data.getDescription_work())){
                    itemViewHolder.work_description.setVisibility(View.VISIBLE);
                    itemViewHolder.work_description.setText(task_data.getDescription_work());
                    Log.d(TAG,"Value of work description are: "+task_data.getDescription_work());
                }
                if (status.equals("Waiting acceptance")) {
                    itemViewHolder.accept_reject.setVisibility(View.VISIBLE);
                    itemViewHolder.Complete.setVisibility(View.GONE);
                    itemViewHolder.accept.setClickable(true);
                    itemViewHolder.reject.setClickable(true);
                } else if (status.equals("pending") || status.equals("overdue")||status.startsWith("Rejected work")) {
                    itemViewHolder.accept.setClickable(true);
                    itemViewHolder.editText_work_description.setFocusable(true);
                    itemViewHolder.editText_work_description.setCursorVisible(true);
                    itemViewHolder.accept_reject.setVisibility(View.GONE);
                    itemViewHolder.Complete.setVisibility(View.VISIBLE);
                    itemViewHolder.editText_work_description.setVisibility(View.VISIBLE);
                }
                itemViewHolder.Complete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String desc = itemViewHolder.editText_work_description.getText().toString();
                        if (TextUtils.isEmpty(desc)){
                            itemViewHolder.editText_work_description.setError("description the work is required");
                        }
                        else {
                            String Name = getshared.getString("name",null);
                            itemViewHolder.editText_work_description.setFocusable(false);
                            itemViewHolder.editText_work_description.setCursorVisible(false);
                            itemViewHolder.accept.setClickable(false);
                            itemViewHolder.Complete.setClickable(false);
                            task_data.addDescription(Name + " : " + desc);
                            markcomplete(task_data);
                        }
                    }
                });
                itemViewHolder.accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemViewHolder.accept.setClickable(false);
                        itemViewHolder.reject.setClickable(false);
                        accept(task_data);
                    }
                });
                itemViewHolder.reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemViewHolder.accept.setClickable(false);
                        itemViewHolder.reject.setClickable(false);
                        markreject(task_data);
                    }
                });
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
        } else {
            Log.d("Data_got",task_data.getTitle());
            final String name = getshared.getString("name", null);
            itemViewHolder.title.setText(task_data.getTitle());
            itemViewHolder.description.setText(task_data.getDescription());
            itemViewHolder.date.setText(task_data.getDate());
            itemViewHolder.priority.setText(task_data.getPriority());
            String status = task_data.getStatus();
            itemViewHolder.status.setText(status);
            itemViewHolder.task_to.setVisibility(View.VISIBLE);
            itemViewHolder.task_by.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(task_data.getDescription_work())){
                itemViewHolder.work_description.setVisibility(View.VISIBLE);
                itemViewHolder.work_description.setText(task_data.getDescription_work());
                Log.d(TAG,"Value of work description are: "+task_data.getDescription_work());
            }
            if (task_data.getAssigned_id().equals(task_data.getAssigned_name())) {
                firebaseFirestore.collection("users").document(task_data.getAssigned_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String temp_name = documentSnapshot.getString("name");
                        itemViewHolder.assigned_name.setText(temp_name);
                        Map<String,Object> name_update = new HashMap<>();
                        name_update.put("assigned_to_name",temp_name);
                        firebaseFirestore.collection("tasks").document(task_data.getTaskId()).update(name_update);
                    }
                });
            }
            else {
                itemViewHolder.assigned_name.setText(task_data.getAssigned_name());
            }
            itemViewHolder.admin_btn.setVisibility(View.VISIBLE);
            if (status.equals("Waiting confirmation")){
                itemViewHolder.accept_reject.setVisibility(View.VISIBLE);
                itemViewHolder.edit.setVisibility(View.GONE);
                itemViewHolder.accept.setClickable(true);
                itemViewHolder.reject.setClickable(true);
                itemViewHolder.delete.setClickable(true);
                itemViewHolder.notify.setClickable(true);
                itemViewHolder.editText_work_description.setFocusable(true);
                itemViewHolder.editText_work_description.setCursorVisible(true);
                itemViewHolder.editText_work_description.setVisibility(View.VISIBLE);
                itemViewHolder.editText_work_description.setHint("Comment");
            }

            if (status.equals("Deleted by: "+name)){
                itemViewHolder.accept_reject.setVisibility(View.GONE);
                itemViewHolder.admin_btn.setVisibility(View.GONE);
                itemViewHolder.status.setTextColor(Color.RED);
            }
            else if (status.equals("Completed")){
                itemViewHolder.accept_reject.setVisibility(View.GONE);
                itemViewHolder.admin_btn.setVisibility(View.GONE);
                itemViewHolder.status.setTextColor(Color.GREEN);
            }
            itemViewHolder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Name = getshared.getString("name",null);
                    itemViewHolder.accept.setClickable(false);
                    itemViewHolder.reject.setClickable(false);
                    itemViewHolder.delete.setClickable(false);
                    itemViewHolder.notify.setClickable(false);
                    itemViewHolder.editText_work_description.setFocusable(false);
                    itemViewHolder.editText_work_description.setCursorVisible(false);
                    task_data.addDescription(Name + " : " + itemViewHolder.editText_work_description.getText().toString());
                    markcompleteAdmin(task_data);
                }
            });
            itemViewHolder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String temp_entry = itemViewHolder.editText_work_description.getText().toString();
                    if (TextUtils.isEmpty(temp_entry)){
                        itemViewHolder.editText_work_description.setError("Reason for rejection is required");
                    }
                    else {
                        String Name = getshared.getString("name",null);
                        itemViewHolder.accept.setClickable(false);
                        itemViewHolder.reject.setClickable(false);
                        itemViewHolder.delete.setClickable(false);
                        itemViewHolder.notify.setClickable(false);
                        itemViewHolder.edit.setClickable(false);
                        itemViewHolder.editText_work_description.setFocusable(false);
                        itemViewHolder.editText_work_description.setCursorVisible(false);
                        task_data.addDescription(Name + " : " + temp_entry);
                        reject_admin(task_data);
                    }
                }
            });
            itemViewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemViewHolder.accept.setClickable(false);
                    itemViewHolder.reject.setClickable(false);
                    itemViewHolder.delete.setClickable(false);
                    itemViewHolder.notify.setClickable(false);
                    itemViewHolder.edit.setClickable(false);
                    deleteTask(task_data);
                }
            });
            itemViewHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemViewHolder.accept.setClickable(false);
                    itemViewHolder.reject.setClickable(false);
                    itemViewHolder.delete.setClickable(false);
                    itemViewHolder.notify.setClickable(false);
                    itemViewHolder.edit.setClickable(false);
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment fragment = new edit_task();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("task",task_data);
                    fragment.setArguments(bundle);

                    activity.getSupportFragmentManager().beginTransaction().replace(holder_edit,fragment).addToBackStack(null).commit();
                }
            });
        }
    }

    private void reject_admin(ModelClass_Task task_data) {
        String name = getshared.getString("name", null);
        Map<String, Object> status = new HashMap<>();
        status.put("status","Rejected work by: "+name);
        status.put("description_work",task_data.getDescription_work());
        firebaseFirestore.collection("tasks").document(task_data.getTaskId()).update(status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Task marked as rejected", Toast.LENGTH_SHORT).show();
                        getUpdateData();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Some problem Occurred", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void getUpdateData() {
        Task_Adapter_dataSetter task_modelClass;
        task_modelClass = new Task_Adapter_dataSetter(object.getContext(),object.getRecyclerView(), object.getActivity(), object.getLoading(), object.getHolder_edit(), object.getFilter());
        task_modelClass.getdata();
    }

    private void deleteTask(ModelClass_Task task_data) {
        String name = getshared.getString("name", null);
        Map<String,Object> status = new HashMap<>();
        status.put("status","Deleted by: "+name);
        firebaseFirestore.collection("tasks").document(task_data.getTaskId()).update(status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Task Marked as Deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Some Problem Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markcompleteAdmin(ModelClass_Task task_data) {
        Map<String,Object> status = new HashMap<>();
        status.put("status","Completed");
        status.put("description_work",task_data.getDescription_work());
        firebaseFirestore.collection("tasks").document(task_data.getTaskId()).update(status)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Task marked as Completed", Toast.LENGTH_SHORT).show();
                getUpdateData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Some problem occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markreject(ModelClass_Task task_data) {
        String name = getshared.getString("name", null);
        Map<String, Object> status = new HashMap<>();
        status.put("status", "Rejected by " + name);
        firebaseFirestore.collection("tasks").document(task_data.getTaskId()).update(status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Task marked as rejected", Toast.LENGTH_SHORT).show();
                getUpdateData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Some problem Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markcomplete(ModelClass_Task task_data) {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "Waiting confirmation");
        status.put("description_work",task_data.getDescription_work());
        firebaseFirestore.collection("tasks").document(task_data.getTaskId()).update(status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Task Marked as completed", Toast.LENGTH_SHORT).show();
                getUpdateData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Some problem occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void accept(ModelClass_Task task_data) {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "pending");
        firebaseFirestore.collection("tasks").document(task_data.getTaskId()).update(status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Task Marked as accepted", Toast.LENGTH_SHORT).show();
                getUpdateData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Something went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        Log.d("Size of", "" + task_list.size());

        return task_list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView title, description, date, priority, name_creator, status,assigned_name,work_description;
        Button Complete, reject, accept;
        EditText editText_work_description;
        LinearLayout accept_reject,task_by,task_to,admin_btn;
        ImageView edit,delete,notify,image;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.due_date);
            priority = itemView.findViewById(R.id.priority);
            name_creator = itemView.findViewById(R.id.task_assigned_by);
            Complete = itemView.findViewById(R.id.btn_complete);
            status = itemView.findViewById(R.id.status);
            reject = itemView.findViewById(R.id.btn_reject);
            accept = itemView.findViewById(R.id.btn_accept);
            accept_reject = itemView.findViewById(R.id.accept_reject);
            task_by = itemView.findViewById(R.id.task_by);
            task_to = itemView.findViewById(R.id.task_to);
            assigned_name = itemView.findViewById(R.id.name_task_assigned_to);
            admin_btn = itemView.findViewById(R.id.admin_buttons);
            edit = itemView.findViewById(R.id.edit_button);
            delete = itemView.findViewById(R.id.delete_button);
            notify = itemView.findViewById(R.id.notify_button);
            editText_work_description = itemView.findViewById(R.id.editText_work);
            work_description = itemView.findViewById(R.id.work_history);
            image = itemView.findViewById(R.id.image);
        }
    }
}
