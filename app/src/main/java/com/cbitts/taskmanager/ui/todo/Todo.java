package com.cbitts.taskmanager.ui.todo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cbitts.taskmanager.Filter;
import com.cbitts.taskmanager.R;
import com.cbitts.taskmanager.ui.Task.GalleryViewModel;
import com.cbitts.taskmanager.ui.Task.Task_Adapter_dataSetter;
import com.cbitts.taskmanager.ui.Task.add_task;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class Todo extends Fragment {

    RecyclerView recyclerView;
    TextView loading;
    FloatingActionButton add_task;
    Button all,pending,complete;
    private AdView mAdView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_todo, container, false);

        add_task = root.findViewById(R.id.add_task);
        recyclerView = root.findViewById(R.id.task_list);
        loading = root.findViewById(R.id.loading_text);
        all = root.findViewById(R.id.all);
        pending = root.findViewById(R.id.pending);
        complete = root.findViewById(R.id.complete);

        mAdView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChildFragmentManager().beginTransaction().add(R.id.main_task,new AddTask_self(),"addTask").addToBackStack(null).commit();
//                fab.setVisibility(View.GONE);
//                Black_layer.setVisibility(View.GONE);
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoAdapter_dataSetter.filter = 1;
                all_color();
                getData();
            }
        });

        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoAdapter_dataSetter.filter = 2;
                pending_color();
                getData();
            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoAdapter_dataSetter.filter = 3;
                complete_color();
                getData();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();

        switch (TodoAdapter_dataSetter.filter){
            case 1:
                all_color();
                break;
            case 2:
                pending_color();
                break;
            case 3:
                complete_color();
                break;
        }
    }

    private void pending_color() {
        pending.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.white_box_round20));
        complete.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_default_color));
        all.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_default_color));
        pending.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        complete.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        all.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    private void complete_color() {
        complete.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.white_box_round20));
        pending.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_default_color));
        all.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_default_color));
        complete.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        all.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        pending.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    private void all_color() {
        all.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.white_box_round20));
        pending.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_default_color));
        complete.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_default_color));
        all.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        complete.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        pending.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    private void getData() {
        TodoAdapter_dataSetter task_modelClass;
        task_modelClass = new TodoAdapter_dataSetter(getContext(),recyclerView,this.getActivity(),loading,R.id.main_task);
        task_modelClass.getdata();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}