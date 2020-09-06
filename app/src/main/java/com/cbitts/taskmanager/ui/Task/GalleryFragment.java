package com.cbitts.taskmanager.ui.Task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cbitts.taskmanager.R;
import com.cbitts.taskmanager.recyclerView_adapter_tasks;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    RecyclerView recyclerView;
    ArrayList<String> Title = new ArrayList<>();
    ArrayList<String> Description = new ArrayList<>();
    ArrayList<String> Date = new ArrayList<>();
    ArrayList<String> Priority = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
//        final TextView textView = root.findViewById(R.id.text_gallery);

        final FloatingActionButton fab = root.findViewById(R.id.fab);
        recyclerView = root.findViewById(R.id.task_list);

//        Black_layer= root.findViewById(R.id.taskList_fragment_black);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChildFragmentManager().beginTransaction().replace(R.id.main_task,new add_task()).addToBackStack(null).commit();
//                fab.setVisibility(View.GONE);
//                Black_layer.setVisibility(View.GONE);
            }
        });

        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getContext(), "tst resume", Toast.LENGTH_SHORT).show();
        getData();
        recyclerView_adapter_tasks adapter = new recyclerView_adapter_tasks(Title,Description,Date,Priority);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void getData() {
        Title.clear();
        Description.clear();
        Date.clear();
        Priority.clear();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore.collection("tasks").document("pending").collection(firebaseAuth.getCurrentUser().getUid()).orderBy("due_date").whereEqualTo("priority","high").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshots : queryDocumentSnapshots){
                            Title.add(documentSnapshots.getString("title"));
                            Description.add(documentSnapshots.getString("description"));
                            Date.add(documentSnapshots.getString("due_date"));
                            Priority.add("High");
                        }
                    }
                });
        firebaseFirestore.collection("tasks").document("pending").collection(firebaseAuth.getCurrentUser().getUid()).orderBy("due_date").whereEqualTo("priority","Normal").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshots : queryDocumentSnapshots){
                            Title.add(documentSnapshots.getString("title"));
                            Description.add(documentSnapshots.getString("description"));
                            Date.add(documentSnapshots.getString("due_date"));
                            Priority.add("Normal");
                        }
                    }
                });

    }
}