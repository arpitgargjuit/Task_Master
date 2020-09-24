package com.cbitts.taskmanager.ui.Task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.cbitts.taskmanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    RecyclerView recyclerView;
    TextView loading;
    Button all,sent,received;
    int filter=0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
//        final TextView textView = root.findViewById(R.id.text_gallery);

        final FloatingActionButton fab = root.findViewById(R.id.fab);
        recyclerView = root.findViewById(R.id.task_list);
        loading = root.findViewById(R.id.loading_text);
        all = root.findViewById(R.id.all);
        sent = root.findViewById(R.id.sent);
        received = root.findViewById(R.id.received);

//        Black_layer= root.findViewById(R.id.taskList_fragment_black);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChildFragmentManager().beginTransaction()/*.replace(R.id.main_task,new add_task())*/.add(R.id.main_task,new add_task(),"addTask").addToBackStack(null).commit();
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

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all.setAlpha(1);
                sent.setAlpha((float) 0.7);
                received.setAlpha((float) 0.7);
                filter = 0;
                getData();
            }
        });

        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all.setAlpha((float) 0.7);
                sent.setAlpha(1);
                received.setAlpha((float) 0.7);
                filter = 1;
                getData();
            }
        });

        received.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all.setAlpha((float) 0.7);
                sent.setAlpha((float) 0.7);
                received.setAlpha(1);
                filter = 2;
                getData();
            }
        });


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
//        Toast.makeText(getContext(), "tst resume", Toast.LENGTH_SHORT).show();
        getData();

    }

    private void getData() {
        Task_Adapter_dataSetter task_modelClass;
        task_modelClass = new Task_Adapter_dataSetter(getContext(),recyclerView,this.getActivity(),loading,R.id.main_task, filter);
        task_modelClass.getdata();


    }
}