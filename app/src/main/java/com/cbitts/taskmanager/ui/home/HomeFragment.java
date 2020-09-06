package com.cbitts.taskmanager.ui.home;

import android.content.Intent;
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

import com.cbitts.taskmanager.MobileEnter;
import com.cbitts.taskmanager.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    Button logout;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    TextView name,pending,waiting,overdue,completed;

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

        firebaseFirestore = FirebaseFirestore.getInstance();

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

        if(firebaseAuth.getCurrentUser()!=null) {
            String uid = firebaseAuth.getCurrentUser().getUid();
            DocumentReference documentReference = firebaseFirestore.collection("users").document(uid);
            documentReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            name.setText("Welcome!\n" + documentSnapshot.getString("name"));
                            pending.setText(documentSnapshot.get("pending").toString());
                            waiting.setText(documentSnapshot.get("waiting").toString());
                            overdue.setText(documentSnapshot.get("overdue").toString());
                            completed.setText(documentSnapshot.get("completed").toString());
                        }
                    });

        }
        return root;
    }
}