package com.cbitts.taskmanager.ui.home;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.cbitts.taskmanager.MobileEnter;
import com.cbitts.taskmanager.NotificationHelper;
import com.cbitts.taskmanager.R;
import com.cbitts.taskmanager.ui.Task.GalleryFragment;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private AppBarConfiguration mAppBarConfiguration;
    String[] color = {"dd4f89","#2c32bb","#f9c040","#50ac55"};
    PieChart pieChart;
    AnyChartView anyChartView;
    String uid;
    Button logout,send,btn_sent,btn_received;
    String Name;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    TextView name,pending,waiting,overdue,completed,Loading_pie,pending_created,waiting_created,overdue_created,completed_created;
    List<DataEntry> dataEntries = new ArrayList<>();
    SharedPreferences getshared;
    int filter = 1;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        logout = root.findViewById(R.id.button_logout);
        firebaseAuth = FirebaseAuth.getInstance();
        name = root.findViewById(R.id.text_name);
        pending = root.findViewById(R.id.number_pending);
        completed = root.findViewById(R.id.number_completed);
        overdue = root.findViewById(R.id.number_overdue);
        waiting = root.findViewById(R.id.number_waiting);
        pending_created = root.findViewById(R.id.number_pending_created);
        waiting_created = root.findViewById(R.id.number_waiting_created);
        overdue_created = root.findViewById(R.id.number_overdue_created);
        completed_created = root.findViewById(R.id.number_completed_created);
//        pieChart = root.findViewById(R.id.pieChart);
        anyChartView = root.findViewById(R.id.card_pie);
        Loading_pie = root.findViewById(R.id.Loading_pie);
        send = root.findViewById(R.id.send_Notification);
        btn_sent = root.findViewById(R.id.sent);
        btn_received = root.findViewById(R.id.received);

        btn_sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_sent.setAlpha(1);
                btn_received.setAlpha((float) 0.7);
                filter = 2;
                getvalues();
            }
        });

        btn_received.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_received.setAlpha(1);
                btn_sent.setAlpha((float) 0.7);
                filter = 1;
                getvalues();
            }
        });


        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotificationHelper.CHANNEL_ID, NotificationHelper.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(NotificationHelper.CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String,Object> noti = new HashMap<>();
                noti.put("message","test message");

//                firebaseFirestore.collection("notifications").document(uid).collection("notifications").add(noti)
            }
        });

        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
//                getChildFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,new GalleryFragment()).commit();
            }
        });

        getshared = this.getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);


//        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(getContext(),MobileEnter.class));
            }
        });


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
//        Toast.makeText(getContext(), "test home resume", Toast.LENGTH_SHORT).show();
        firebaseFirestore = FirebaseFirestore.getInstance();
        Name = getshared.getString("name","Not available");
        uid = getshared.getString("uid",null);
        getvalues();
        if(firebaseAuth.getCurrentUser()!=null) {
            String uid = firebaseAuth.getCurrentUser().getUid();
            DocumentReference documentReference = firebaseFirestore.collection("tasks").document(uid);
            documentReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            name.setText("Welcome!\n" + Name);
//                            setValue(documentSnapshot.getLong("pending"),documentSnapshot.getLong("waiting"),documentSnapshot.getLong("overdue"),
//                                    documentSnapshot.getLong("completed"),documentSnapshot.getString("name"));
//                            dataEntries.add(new ValueDataEntry("Pending",documentSnapshot.getLong("pending")));
//                            dataEntries.add(new ValueDataEntry("Completed",documentSnapshot.getLong("completed")));
//                            dataEntries.add(new ValueDataEntry("Overdue",documentSnapshot.getLong("Overdue")));
//                            dataEntries.add(new ValueDataEntry("Waiting",documentSnapshot.getLong("waiting")));
//                            Loading_pie.setVisibility(View.GONE);
//                            setUpPieChart();
                        }
                    });

        }
    }

    private void getvalues() {
        if (TextUtils.isEmpty(uid)) {
            uid = firebaseAuth.getCurrentUser().getUid();
            getvalues();
        }
        else {
            if (filter == 1){
                getvalues_received();
            }
            else if (filter == 2){
                getvalues_sent();
            }
        }
    }

    public void getvalues_received(){
        firebaseFirestore.collection("tasks").whereEqualTo("assigned_to",uid).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Long pend= Long.valueOf(0),wait= Long.valueOf(0),over= Long.valueOf(0),compl= Long.valueOf(0);
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            String temp;
                            temp = documentSnapshot.getString("status");
                            if (temp.equals("Waiting acceptance")||temp.equals("pending")||temp.startsWith("Rejected work")){
                                pend++;
                            }
                            else if (temp.equals("Waiting confirmation")){
                                wait++;
                            }
                            else  if (temp.equals("Completed")){
                                compl++;
                            }
                        }
                        pending.setText(""+pend);
                        completed.setText(""+compl);
                        waiting.setText(""+wait);
                        overdue.setText(""+over);
//                            setValue(pend,wait,over,compl,Name);
                        dataEntries.clear();
                        dataEntries.add(new ValueDataEntry("Pending",pend));
                        dataEntries.add(new ValueDataEntry("Completed",compl));
                        dataEntries.add(new ValueDataEntry("Overdue",over));
                        dataEntries.add(new ValueDataEntry("Waiting",wait));
                        Loading_pie.setVisibility(View.GONE);
                        setUpPieChart();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getvalues_sent(){
        firebaseFirestore.collection("tasks").whereEqualTo("created_by_uid",uid).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int pend=0,wait=0,over=0,compl=0;
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            String temp;
                            temp = documentSnapshot.getString("status");
                            if (temp.equals("Waiting acceptance")||temp.equals("pending")||temp.startsWith("Rejected work")){
                                pend++;
                            }
                            else if (temp.equals("Waiting confirmation")){
                                wait++;
                            }
                            else  if (temp.equals("Completed")){
                                compl++;
                            }
                        }
                        pending_created.setText(""+pend);
                        completed_created.setText(""+compl);
                        waiting_created.setText(""+wait);
                        overdue_created.setText(""+over);


                        pending.setText(""+pend);
                        completed.setText(""+compl);
                        waiting.setText(""+wait);
                        overdue.setText(""+over);
//                            setValue(pend,wait,over,compl,Name);
                        dataEntries.clear();
                        dataEntries.add(new ValueDataEntry("Pending",pend));
                        dataEntries.add(new ValueDataEntry("Completed",compl));
                        dataEntries.add(new ValueDataEntry("Overdue",over));
                        dataEntries.add(new ValueDataEntry("Waiting",wait));
                        Loading_pie.setVisibility(View.GONE);
                        setUpPieChart();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpPieChart() {
        Pie pie = AnyChart.pie();

//        for(int i=0;i<2;i++){
//            dataEntries.add(new ValueDataEntry(month[i],earnings[i]));
//        }
        pie.data(dataEntries);
//        pie.fill(color);
//        pie.palette(["#dd4f89","#2c32bb","#f9c040","#50ac55"]);
        anyChartView.setChart(pie);
    }

    private void setValue(Long pending, Long waiting, Long overdue, Long completed,String name) {
        pieChart.setUsePercentValues(true);
        List<PieEntry> value = new ArrayList<>();
        value.add(new PieEntry(pending,"Pending"));
        value.add(new PieEntry(waiting,"Waiting"));
        value.add(new PieEntry(overdue,"Overdue"));
        value.add(new PieEntry(completed,"Completed"));
        PieDataSet pieDataSet = new PieDataSet(value,"test");
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
    }
}