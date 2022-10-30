package com.hurupay.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.hurupay.android.R;
import com.hurupay.android.UserProfile;
import com.hurupay.android.adapters.UserAdapter;
import com.hurupay.android.constants.Constants;
import com.hurupay.android.listeners.AccountItemClickListener;
import com.hurupay.android.models.Account;
import com.hurupay.android.models.User;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment implements AccountItemClickListener {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private UserAdapter adapter;
    private User user;
    private final List<Account> objectList = new ArrayList<>();

    public ContactsFragment() {
        // Required empty public constructor
    }

    public static ContactsFragment getInstance(User user){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.OBJECT, user);
        ContactsFragment fragment = new ContactsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        user = (User) getArguments().getSerializable(Constants.OBJECT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        adapter = new UserAdapter(getContext(), objectList, this);
        recyclerView.setAdapter(adapter);
        fetchObjects(user.getAddress());
        return view;
    }

    private void fetchObjects(String objectID){
        Query query = firebaseFirestore.collection(Constants.BOOKMARKS).document(objectID).collection(Constants.CONTACTS).orderBy("address", Query.Direction.DESCENDING).whereArrayContains("tags","Person");
        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Account object = documentChange.getDocument().toObject(Account.class);
                        if (!objectList.contains(object)){
                            objectList.add(object);
                            adapter.notifyItemInserted(objectList.size()-1);
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.MODIFIED){
                        Account object = documentChange.getDocument().toObject(Account.class);
                        if (objectList.contains(object)){
                            objectList.set(objectList.indexOf(object),object);
                            adapter.notifyItemChanged(objectList.indexOf(object));
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.REMOVED){
                        Account object = documentChange.getDocument().toObject(Account.class);
                        if (objectList.contains(object)){
                            objectList.remove(object);
                            adapter.notifyItemRemoved(objectList.indexOf(object));
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchObjects(user.getAddress());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registration != null){
            registration.remove();
        }
    }

    @Override
    public void onAccountItemClick(Account account, ImageView imageView, String objectID) {
        Intent intent = new Intent(requireContext(), UserProfile.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.OBJECT,account);
        intent.putExtras(bundle);
        intent.putExtra(Constants.OBJECT_ID,objectID);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), Pair.create(imageView, account.getAddress()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }
}