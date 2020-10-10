package com.cbitts.taskmanager.ui.Task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cbitts.taskmanager.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Custondialog_userSelect extends DialogFragment {

    EditText search;
    RecyclerView recyclerView;
    Button confirm, cancel;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    Add_task_generator addTaskGenerator = new Add_task_generator();
    ArrayList<Name_ModelClass> Assign_name = new ArrayList<>();
    final String TAG = "UserSelectDialog";
    SharedPreferences sharedPreferences;
    name_adapter_recyclerView adapter;


    public interface OnInputSelected{
        void sendInput(Add_task_generator add_task_generator);
    }
    private OnInputSelected onInputSelected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.user_show_dialog, container, false);

        cancel = view.findViewById(R.id.cancel_action);
        confirm = view.findViewById(R.id.confirm_action);
        recyclerView = view.findViewById(R.id.person_list);
        search = view.findViewById(R.id.search);

        sharedPreferences = getActivity().getSharedPreferences("user_details",Context.MODE_PRIVATE);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterdata(editable.toString());
                        
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                add_task fragment = (add_task) getActivity().getSupportFragmentManager().findFragmentByTag("addTask");
                String name = addTaskGenerator.getName();
                if (!TextUtils.isEmpty(name)) {
                    onInputSelected.sendInput(addTaskGenerator);
                    getDialog().dismiss();
                }
                else
                    Toast.makeText(getContext(), "Select the User", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void filterdata(String Text) {
        ArrayList<Name_ModelClass> filterName = new ArrayList<>();

        for (Name_ModelClass item : Assign_name){
            if (item.getName().toLowerCase().contains(Text.toLowerCase())){
                filterName.add(item);
            }
            adapter.FilterList(filterName);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onInputSelected = (OnInputSelected) getTargetFragment();
        }
        catch (ClassCastException e){
            Log.d(TAG,"onAttach: Class Cast Exception "+e.getMessage());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getdata();
    }

    private void getdata() {
        Assign_name.clear();
        final String curr_uid = sharedPreferences.getString("uid",null);
        firebaseFirestore.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String tempName, tempUid;
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            tempName = documentSnapshot.getString("name");
                            tempUid = documentSnapshot.getString("id");
                            if (!tempUid.equals(curr_uid))
                            Assign_name.add(new Name_ModelClass(tempName+"\n"+documentSnapshot.getString("mobile"), tempUid));
                            adapter = new name_adapter_recyclerView(getContext(), Assign_name, addTaskGenerator);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        }
                    }
                });
    }
}
