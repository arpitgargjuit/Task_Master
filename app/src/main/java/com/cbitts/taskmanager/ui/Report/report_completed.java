package com.cbitts.taskmanager.ui.Report;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cbitts.taskmanager.Filter;
import com.cbitts.taskmanager.R;

public class report_completed extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    TextView loading;
    Button all,sent,received;
    int filter=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_report_completed, container, false);
        recyclerView = root.findViewById(R.id.report_list);
        all = root.findViewById(R.id.all);
        sent = root.findViewById(R.id.sent);
        received = root.findViewById(R.id.received);
        loading = root.findViewById(R.id.loading_text);
        swipeRefreshLayout = root.findViewById(R.id.refresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getData();

            }
        });

        getData();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                all.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.white_box_round20));
//                received.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_default_color));
//                sent.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_default_color));
//                all.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
//                sent.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
//                received.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                filter = 0;
                Filter.setFilter(filter);
                getData();
            }
        });

        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                sent.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.white_box_round20));
//                received.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_default_color));
//                all.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_default_color));
//                sent.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
//                all.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
//                received.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                filter = 1;
                Filter.setFilter(filter);
                getData();
            }
        });

        received.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                received.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.white_box_round20));
//                sent.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_default_color));
//                all.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_default_color));
//                received.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
//                sent.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
//                all.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                filter = 2;
                Filter.setFilter(filter);
                getData();
            }
        });

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

        Completed_report_dataSetter modelClass_pending_report = new Completed_report_dataSetter(getContext(),recyclerView, this.getActivity(), loading,R.id.main_task, Filter.getFilter());
        modelClass_pending_report.getdata();
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

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
}