package com.cbitts.taskmanager.ui.Report;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.cbitts.taskmanager.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SlideshowFragment extends Fragment {

    String TAG = "Reports";

    private SlideshowViewModel slideshowViewModel;
    private BottomNavigationView navigationView;
    private AdView mAdView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        navigationView = root.findViewById(R.id.Navigation_type);
//        final TextView textView = root.findViewById(R.id.text_slideshow);

        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });

        navigationView.setOnNavigationItemSelectedListener(bottomNavigation);
        getChildFragmentManager().beginTransaction().replace(R.id.report_container,new report_pending()).commit();

        mAdView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        return root;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigation = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Log.d(TAG,""+item.getItemId());
                    Fragment fragment = null;
                    switch (item.getItemId()){
                        case R.id.pending:
                            fragment = new report_pending();
                            break;
                        case R.id.complete:
                            fragment = new report_completed();
                            break;
                        case R.id.waiting:
                            fragment = new report_waiting();
                            break;
//                        case R.id.overdue:
//                            fragment = new report_overdue();
//                            break;
                    }
                    getChildFragmentManager().beginTransaction().replace(R.id.report_container,fragment).commit();

                    return true;
                }
            };
}