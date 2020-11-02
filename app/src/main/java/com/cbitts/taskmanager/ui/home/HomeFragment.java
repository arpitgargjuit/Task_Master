package com.cbitts.taskmanager.ui.home;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.cbitts.taskmanager.Filter;
import com.cbitts.taskmanager.MobileEnter;
import com.cbitts.taskmanager.NotificationHelper;
import com.cbitts.taskmanager.R;
import com.cbitts.taskmanager.ui.Report.SlideshowFragment;
import com.cbitts.taskmanager.ui.Task.GalleryFragment;
import com.cbitts.taskmanager.verify_otp;
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
import com.onesignal.OneSignal;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.cbitts.taskmanager.R.*;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private AppBarConfiguration mAppBarConfiguration;
    String[] color = {"dd4f89","#2c32bb","#f9c040","#50ac55"};
    PieChart pieChart;
    AnyChartView anyChartView;
    String uid;
    Button logout,send,btn_sent,btn_received,btn_all;
    String Name;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    TextView name,pending,waiting,overdue,completed,Loading_pie,pending_created,waiting_created,overdue_created,completed_created;
    List<DataEntry> dataEntries = new ArrayList<>();
    SharedPreferences getshared,getshared1;
    int filter = 0;
    Long pend= 0L,wait= 0L,over= 0L,compl= 0L;
    LinearLayout pending_view, completed_view, overdue_view, waiting_view, about_view, todo_view;
    private AdView mAdView;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(layout.fragment_home, container, false);
//        logout = root.findViewById(R.id.button_logout);
        firebaseAuth = FirebaseAuth.getInstance();
        name = root.findViewById(id.text_name);
        pending = root.findViewById(id.number_pending);
        completed = root.findViewById(id.number_completed);
        overdue = root.findViewById(id.number_overdue);
        waiting = root.findViewById(id.number_waiting);
        pending_created = root.findViewById(id.number_pending_created);
        waiting_created = root.findViewById(id.number_waiting_created);
        overdue_created = root.findViewById(id.number_overdue_created);
        completed_created = root.findViewById(id.number_completed_created);
        anyChartView = root.findViewById(id.card_pie);
        Loading_pie = root.findViewById(id.Loading_pie);
        send = root.findViewById(id.send_Notification);
        btn_sent = root.findViewById(id.sent);
        btn_received = root.findViewById(id.received);
        btn_all = root.findViewById(id.all);
        pending_view = root.findViewById(id.pending_view);
        completed_view = root.findViewById(id.completed_view);
        overdue_view = root.findViewById(id.number_overdue_view);
        completed_view = root.findViewById(id.completed_view);
        waiting_view = root.findViewById(id.waiting_view);
        about_view = root.findViewById(id.aboutApp_view);
        todo_view = root.findViewById(R.id.todo_view);
        swipeRefreshLayout = root.findViewById(id.refresh);

        mAdView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getvalues();

            }
        });

        pending_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_nav_home_to_nav_slideshow);
//                getChildFragmentManager().beginTransaction().replace(id.nav_home,new SlideshowFragment()).commit();
            }
        });

        completed_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_nav_home_to_nav_slideshow);
            }
        });

        overdue_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_nav_home_to_nav_slideshow);
            }
        });

        waiting_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_nav_home_to_nav_slideshow);
            }
        });

        about_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(getParentFragment()).navigate(id.action_nav_home_to_nav_share);
            }
        });

        todo_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(getParentFragment()).navigate(id.action_nav_home_to_nav_about);
            }
        });


        btn_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                filter = 0;
                Filter.setFilter(filter);
                getvalues();
            }
        });

        btn_sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                filter = 1;
                Filter.setFilter(filter);
                getvalues();
            }
        });

        btn_received.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                btn_sent.setAlpha((float) 0.7);
                filter = 2;
                Filter.setFilter(filter);
                getvalues();
            }
        });


//        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(NotificationHelper.CHANNEL_ID, NotificationHelper.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
//            channel.setDescription(NotificationHelper.CHANNEL_DESCRIPTION);
//            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }

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
//        getshared1 = this.getActivity().getSharedPreferences("user_name", Context.MODE_PRIVATE);


//        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                firebaseAuth.signOut();
//                startActivity(new Intent(getContext(),MobileEnter.class));
//            }
//        });


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
//        Toast.makeText(getContext(), "test home resume", Toast.LENGTH_SHORT).show();
        firebaseFirestore = FirebaseFirestore.getInstance();
        Name = getshared.getString("name","Not available");
        uid = getshared.getString("uid",null);

//        OneSignal.startInit(getContext())
//                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//                .unsubscribeWhenNotificationsAreDisabled(true)
//                .init();

//        OneSignal.sendTag("id",uid);
        getvalues();

        if (!internetConnectionAvailable(10000)){
            name.setText(string.internet_error_name);
        }

        if(firebaseAuth.getCurrentUser()!=null) {
            String uid = firebaseAuth.getCurrentUser().getUid();
            DocumentReference documentReference = firebaseFirestore.collection("tasks").document(uid);
            documentReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            name.setText(/*"Welcome!\n" +*/ Name);
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

        if (!internetConnectionAvailable(10000)){
            Toast.makeText(getContext(), string.internet_error_toast, Toast.LENGTH_SHORT).show();
            pending.setText("-");
            completed.setText("-");
            overdue.setText("-");
            waiting.setText("-");
        }
        if (TextUtils.isEmpty(uid)) {
            uid = firebaseAuth.getCurrentUser().getUid();
            getvalues();
        }
        else {
            if (Filter.getFilter() == 2){
                color_received();
                getvalues_received();
            }
            else if (Filter.getFilter() == 1){
                color_sent();
                getvalues_sent();
            }
            else if(Filter.getFilter() == 0){
                color_all();
                getvalues_all();
            }
        }
    }

    private void color_all() {
        btn_all.setBackground(ContextCompat.getDrawable(getContext(), drawable.white_box_round20));
        btn_received.setBackground(ContextCompat.getDrawable(getContext(), drawable.rounded_default_color));
        btn_sent.setBackground(ContextCompat.getDrawable(getContext(), drawable.rounded_default_color));
        btn_all.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        btn_sent.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        btn_received.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    private void color_sent() {
        btn_sent.setBackground(ContextCompat.getDrawable(getContext(), drawable.white_box_round20));
        btn_received.setBackground(ContextCompat.getDrawable(getContext(), drawable.rounded_default_color));
        btn_all.setBackground(ContextCompat.getDrawable(getContext(), drawable.rounded_default_color));
        btn_sent.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        btn_all.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        btn_received.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    private void color_received() {
        btn_received.setBackground(ContextCompat.getDrawable(getContext(), drawable.white_box_round20));
        btn_sent.setBackground(ContextCompat.getDrawable(getContext(), drawable.rounded_default_color));
        btn_all.setBackground(ContextCompat.getDrawable(getContext(), drawable.rounded_default_color));
        btn_received.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        btn_sent.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        btn_all.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    private void getvalues_all() {
        firebaseFirestore.collection("tasks").whereEqualTo("assigned_to",uid).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        pend= 0L;wait= 0L;over= 0L;compl= 0L;
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
                            else if (temp.equals("Overdue")){
                                over++;
                            }
                        }
                        firebaseFirestore.collection("tasks").whereEqualTo("created_by_uid",uid).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
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
                                            else if (temp.equals("Overdue")){
                                                over++;
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
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getvalues_received(){
        firebaseFirestore.collection("tasks").whereEqualTo("assigned_to",uid).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Long pend= 0L,wait= 0L,over= 0L,compl= 0L;
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
                            else if (temp.equals("Overdue")){
                                over++;
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
            }
        });
    }

    public void getvalues_sent(){
        firebaseFirestore.collection("tasks").whereEqualTo("created_by_uid",uid).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Long pend= 0L,wait= 0L,over= 0L,compl= 0L;
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
                            else if (temp.equals("Overdue")){
                                over++;
                            }
                        }
                        pending.setText(""+pend);
                        completed.setText(""+compl);
                        waiting.setText(""+wait);
                        overdue.setText(""+over);


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
        anyChartView.clear();
        anyChartView.setChart(pie);
    }

    private boolean internetConnectionAvailable(int timeOut) {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("google.com");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        return inetAddress!=null && !inetAddress.equals("");
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