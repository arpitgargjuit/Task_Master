package com.cbitts.taskmanager.ui.todo;

import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cbitts.taskmanager.NotificationHelper;
import com.cbitts.taskmanager.R;
import com.cbitts.taskmanager.ui.Report.Completed_report_dataSetter;
import com.cbitts.taskmanager.ui.Task.CustomObject;
import com.cbitts.taskmanager.ui.Task.ModelClass_Task;
import com.cbitts.taskmanager.ui.Task.Task_Adapter_dataSetter;
import com.cbitts.taskmanager.ui.Task.edit_task;
import com.cbitts.taskmanager.ui.Task.recyclerView_adapter_tasks;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerView_adapter_todo extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<ModelClass_Task> task_list;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    SharedPreferences getshared;
    String uid;
    int holder_edit;
    final String TAG = "recyclerView_adapter";
    CustomObject object;
    NotificationHelper notificationHelper = new NotificationHelper();

    public RecyclerView_adapter_todo(Context context, List<ModelClass_Task> task_list, int holder_edit, CustomObject object) {
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
        return new RecyclerView_adapter_todo.viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final RecyclerView_adapter_todo.viewHolder itemViewHolder = (RecyclerView_adapter_todo.viewHolder) holder;
        final ModelClass_Task task_data = task_list.get(position);
        try {
            if (task_data.getFlag().equals("1")) {
                itemViewHolder.Image_btn.setVisibility(View.VISIBLE);
                Log.d("adapter_task", "flag is 1");
            } else {
                itemViewHolder.Image_btn.setVisibility(View.GONE);
                Log.d("adapter_task", "flag is 0");
            }
            itemViewHolder.Image_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (itemViewHolder.image_open) {
                        itemViewHolder.image.setVisibility(View.GONE);
                        itemViewHolder.Image_btn.setText("Click to see image");
                        itemViewHolder.image_open = false;
                    } else {
                        itemViewHolder.image.setVisibility(View.VISIBLE);
                        itemViewHolder.Image_btn.setText("Hide image");
                        Glide.with(context)
                                .asBitmap()
                                .load(task_data.getImage())
                                .into(itemViewHolder.image);
                        itemViewHolder.image_open = true;
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("adapter_task", "exception: " + e.toString());
        }
        try {
            Log.d("Test", "inside first first");
            itemViewHolder.title.setText(task_data.getTitle());
            itemViewHolder.description.setText(task_data.getDescription());
            itemViewHolder.date.setText(task_data.getDate());
            itemViewHolder.task_to.setVisibility(View.GONE);
            itemViewHolder.task_by.setVisibility(View.GONE);
            itemViewHolder.priority.setText(task_data.getPriority());
            itemViewHolder.create_date.setText(task_data.getCreateDate());
            String status = task_data.getStatus();
            itemViewHolder.status.setText(status);
            itemViewHolder.delete_todo.setVisibility(View.VISIBLE);
            Log.d("Data_got", task_data.getTitle());

            if (status.equals("Overdue")) {
                itemViewHolder.status.setTextColor(Color.RED);
            }
            if (!TextUtils.isEmpty(task_data.getDescription_work())) {
                itemViewHolder.work_description.setVisibility(View.VISIBLE);
                itemViewHolder.work_description.setText(task_data.getDescription_work());
                Log.d(TAG, "Value of work description are: " + task_data.getDescription_work());
            }
            if (status.equals("Pending")||status.equals("Overdue")) {
                itemViewHolder.Complete.setClickable(true);
                itemViewHolder.editText_work_description.setFocusable(true);
                itemViewHolder.editText_work_description.setCursorVisible(true);
                itemViewHolder.Complete.setVisibility(View.VISIBLE);
                itemViewHolder.editText_work_description.setVisibility(View.VISIBLE);
                itemViewHolder.delete_completed.setVisibility(View.VISIBLE);
            } else if (status.equals("Completed")) {
                itemViewHolder.status.setTextColor(Color.GREEN);
            }
            itemViewHolder.Complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String desc = itemViewHolder.editText_work_description.getText().toString();
                    if (TextUtils.isEmpty(desc)) {
                        itemViewHolder.editText_work_description.setError("description the work is required");
                    } else {
                        String Name = getshared.getString("name", null);
                        itemViewHolder.editText_work_description.setFocusable(false);
                        itemViewHolder.editText_work_description.setCursorVisible(false);
                        itemViewHolder.accept.setClickable(false);
                        itemViewHolder.Complete.setClickable(false);
                        task_data.addDescription(Name + " : " + desc);
                        markcomplete(task_data);
                    }
                }
            });
            itemViewHolder.delete_todo.setOnClickListener(new View.OnClickListener() {
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
//            itemViewHolder.edit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    itemViewHolder.accept.setClickable(false);
//                    itemViewHolder.reject.setClickable(false);
//                    itemViewHolder.delete.setClickable(false);
//                    itemViewHolder.notify.setClickable(false);
//                    itemViewHolder.edit.setClickable(false);
//                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
//                    Fragment fragment = new edit_task();
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("task", task_data);
//                    fragment.setArguments(bundle);
//
//                    activity.getSupportFragmentManager().beginTransaction().replace(holder_edit, fragment).addToBackStack(null).commit();
//                }
//            });

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
    }

    private void getUpdateData() {
        TodoAdapter_dataSetter task_modelClass;
        task_modelClass = new TodoAdapter_dataSetter(object.getContext(), object.getRecyclerView(), object.getActivity(), object.getLoading(), object.getHolder_edit());
        task_modelClass.getdata();
    }

    private void getUpdateData_complete() {
        Completed_report_dataSetter dataSetter;
        dataSetter = new Completed_report_dataSetter(object.getContext(), object.getRecyclerView(), object.getActivity(), object.getLoading(), object.getHolder_edit(), object.getFilter());
        dataSetter.getdata();
    }

    private void deleteTask(final ModelClass_Task task_data) {
        final String name = getshared.getString("name", null);
//        Map<String, Object> status = new HashMap<>();
//        status.put("status", "Deleted by: " + name);
//        status.put("timestamp_1", Timestamp.now());
        firebaseFirestore.collection("users").document(uid).collection("self_task").document(task_data.getTaskId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Task Marked as Deleted", Toast.LENGTH_SHORT).show();
                getUpdateData();
//                notificationHelper.sendNotificationTune2(task_data.getAssigned_id(), task_data.getTitle() + "\nDeleted by \n" + name);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Some Problem Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void markcomplete(final ModelClass_Task task_data) {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "Completed");
        status.put("timestamp_completed", Timestamp.now());
        status.put("description_work", task_data.getDescription_work());
        firebaseFirestore.collection("users").document(uid).collection("self_task").document(task_data.getTaskId()).update(status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Task Marked as completed", Toast.LENGTH_SHORT).show();
                getUpdateData();
//                notificationHelper.sendNotificationTune2(task_data.getCreated_id(), task_data.getTitle() + "\nCompleted by: \n" + task_data.getAssigned_name());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Some problem occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        Log.d("Size of", "" + task_list.size());

        return task_list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView title, description, date, priority, name_creator, status, assigned_name, work_description, Image_btn, create_date;
        Button Complete, reject, accept;
        ImageButton edit, delete, notify, delete_completed,delete_todo;
        EditText editText_work_description;
        LinearLayout accept_reject, task_by, task_to, admin_btn;
        ImageView image;
        Boolean image_open = false;

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
            Image_btn = itemView.findViewById(R.id.image_btn);
            delete_completed = itemView.findViewById(R.id.delete_button_completed);
            create_date = itemView.findViewById(R.id.create_date);
            delete_todo = itemView.findViewById(R.id.delete_todo_btn);
        }
    }
}
