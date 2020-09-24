package com.cbitts.taskmanager.ui.Report;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cbitts.taskmanager.R;

public class report_pending extends Fragment {

    TextView loading;
    Button all,sent,received;
    int filter=0;
    RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_report_pending, container, false);
        recyclerView = root.findViewById(R.id.report_list);
        all = root.findViewById(R.id.all);
        sent = root.findViewById(R.id.sent);
        received = root.findViewById(R.id.received);
        loading = root.findViewById(R.id.loading_text);
//        getData();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
    }

    private void getData() {
        Pending_report_dataSetter modelClass_pending_report = new Pending_report_dataSetter(getContext(),recyclerView, this.getActivity(), loading,R.id.main_task, filter);
        modelClass_pending_report.getdata();
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
}