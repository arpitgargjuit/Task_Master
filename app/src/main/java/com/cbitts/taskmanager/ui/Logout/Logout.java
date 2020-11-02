package com.cbitts.taskmanager.ui.Logout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cbitts.taskmanager.MobileEnter;
import com.cbitts.taskmanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.onesignal.OneSignal;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Logout extends Fragment {

    Button logout,cancel;
    FirebaseAuth firebaseAuth;
    private AdView mAdView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logout = view.findViewById(R.id.logout);
        cancel = view.findViewById(R.id.cancel);
        firebaseAuth = FirebaseAuth.getInstance();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_nav_logout_to_nav_home);
                OneSignal.sendTag("id","");
            }
        });

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    private void logout() {
        firebaseAuth.signOut();
        SharedPreferences getshared;
        getshared = this.getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        getshared.edit().clear().apply();
                startActivity(new Intent(getContext(), MobileEnter.class));
    }

}