package com.cbitts.taskmanager.ui.notifications;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cbitts.taskmanager.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class Notifications extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    ArrayList<String> notification_title = new ArrayList<>();
    ArrayList<String> notification_date = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.list_notifications);
        swipeRefreshLayout = view.findViewById(R.id.refresh);
        getData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });




//        ArrayAdapter<String> itemsAdapter =
//                new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,  notification_array);
//        listView.setAdapter(itemsAdapter);

//        ArrayAdapterNotifications adapter = new ArrayAdapterNotifications(getContext(),R.layout.notification_adapter_layout,notification_title
//       );
//
//        listView.setAdapter(adapter);


    }

    private void getData(){
        notification_title.clear();
        notification_date.clear();
        firebaseFirestore.collection("notifications").whereEqualTo("id",firebaseAuth.getCurrentUser().getUid()).orderBy("timestamp_1", Query.Direction.DESCENDING).limit(20).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d("Notification","data received");
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Log.d("count","test"+notification_title.size());

                    notification_date.add(documentSnapshot.getString("date"));
                    notification_title.add(documentSnapshot.getString("message"));
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                RecyclerViewNotifications adapter = new RecyclerViewNotifications(notification_title,notification_date);
                recyclerView.setAdapter(adapter);
                Log.d("count", String.valueOf(notification_title.size()));
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                Log.d("error",e.toString());
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
        });
    }
}