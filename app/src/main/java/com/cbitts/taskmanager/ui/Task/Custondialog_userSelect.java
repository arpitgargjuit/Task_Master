package com.cbitts.taskmanager.ui.Task;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cbitts.taskmanager.R;
import com.google.android.gms.tasks.OnFailureListener;
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
    ArrayList<String> contacts = new ArrayList<>();
    final String TAG = "UserSelectDialog";
    SharedPreferences sharedPreferences;
    name_adapter_recyclerView adapter;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    Cursor c;


    public interface OnInputSelected {
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

        sharedPreferences = getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);

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
                } else
                    Toast.makeText(getContext(), "Select the User", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void filterdata(String Text) {
        ArrayList<Name_ModelClass> filterName = new ArrayList<>();

        for (Name_ModelClass item : Assign_name) {
            if (item.getName().toLowerCase().contains(Text.toLowerCase())) {
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
        } catch (ClassCastException e) {
            Log.d(TAG, "onAttach: Class Cast Exception " + e.getMessage());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getdata();
    }

    private void getdata() {
        Assign_name.clear();
        contacts.clear();

        c = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC ");
        while (c.moveToNext()) {
            String phNumber = (c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))).replaceAll("[()\\s-]+", "");
            if (!phNumber.startsWith("+91"))
                phNumber = "+91" + phNumber;
            phNumber = phNumber.trim();
            contacts.add(phNumber);
        }


        final String curr_uid = sharedPreferences.getString("uid", null);
        firebaseFirestore.collection("users").orderBy("name").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String tempName, tempUid, temp_mobile;
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            tempName = documentSnapshot.getString("name");
                            tempUid = documentSnapshot.getString("id");
                            temp_mobile = documentSnapshot.getString("mobile");


                            if (!tempUid.equals(curr_uid)) {
                                for (String item : contacts) {
                                    if (item.equals(temp_mobile)) {
                                        Assign_name.add(new Name_ModelClass(tempName + "\n" + temp_mobile, tempUid));
                                        Log.d("check", temp_mobile);
                                        adapter = new name_adapter_recyclerView(getContext(), Assign_name, addTaskGenerator);
                                        recyclerView.setAdapter(adapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                        break;
                                    }
                                }
                            }
                        }

                        Toast.makeText(getContext(), "All contacts loaded", Toast.LENGTH_SHORT).show();

                        c.close();
                        Log.d("contacts", contacts + "");


                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
                Log.d("error",e.toString());
            }
        });
    }
}
