package com.cbitts.taskmanager.ui.Task;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cbitts.taskmanager.Filter;
import com.cbitts.taskmanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    private GalleryViewModel galleryViewModel;
    RecyclerView recyclerView;
    TextView loading;
    Button all,sent,received;
    int filter=Filter.getFilter();


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
        swipeRefreshLayout = root.findViewById(R.id.refresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

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

                filter = 0;
                Filter.setFilter(filter);
                getData();
            }
        });

        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                filter = 1;
                Filter.setFilter(filter);
                getData();
            }
        });

        received.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                filter = 2;
                Filter.setFilter(filter);
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

    private void received_color() {
        received.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.white_box_round20));
        sent.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_default_color));
        all.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_default_color));
        received.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        sent.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        all.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    private void sent_color() {
        sent.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.white_box_round20));
        received.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_default_color));
        all.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_default_color));
        sent.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        all.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        received.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    private void all_color() {
        all.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.white_box_round20));
        received.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_default_color));
        sent.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_default_color));
        all.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        sent.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        received.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    private void getData() {

        switch (Filter.getFilter()) {
            case 0:
                all_color();
                break;
            case 1:
                sent_color();
                break;
            case 2:
                received_color();
                break;
        }

        Task_Adapter_dataSetter task_modelClass;
        task_modelClass = new Task_Adapter_dataSetter(getContext(),recyclerView,this.getActivity(),loading,R.id.main_task, Filter.getFilter());
        task_modelClass.getdata();

        //todo This to be disabled inside setter class
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }, 1000);


    }
}