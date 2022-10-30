package com.hurupay.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.hurupay.android.CryptoProfile;
import com.hurupay.android.R;
import com.hurupay.android.adapters.TokenAdapter;
import com.hurupay.android.constants.Constants;
import com.hurupay.android.listeners.TokenItemClickListener;
import com.hurupay.android.models.Token;
import com.hurupay.android.models.User;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements TokenItemClickListener {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private TokenAdapter adapter;
    private User user;
    private final List<Token> objectList = new ArrayList<>();

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public static FavoritesFragment getInstance(User user){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.OBJECT, user);
        FavoritesFragment fragment = new FavoritesFragment();
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
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        adapter = new TokenAdapter(getContext(), objectList, this);
        recyclerView.setAdapter(adapter);
        fetchObjects(user.getCountry());
        return view;
    }


    private void fetchObjects(String objectID){
        Query query = firebaseFirestore.collection(Constants.TOKENS).orderBy("tokenAddress", Query.Direction.DESCENDING).whereArrayContains("tags",objectID);
        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Token object = documentChange.getDocument().toObject(Token.class);
                        if (!objectList.contains(object)){
                            objectList.add(object);
                            adapter.notifyItemInserted(objectList.size()-1);
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.MODIFIED){
                        Token object = documentChange.getDocument().toObject(Token.class);
                        if (objectList.contains(object)){
                            objectList.set(objectList.indexOf(object),object);
                            adapter.notifyItemChanged(objectList.indexOf(object));
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.REMOVED){
                        Token object = documentChange.getDocument().toObject(Token.class);
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
        fetchObjects(user.getCountry());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registration != null){
            registration.remove();
        }
    }

    @Override
    public void onTokenItemClick(Token token, ImageView imageView) {
        Intent intent = new Intent(requireContext(), CryptoProfile.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.OBJECT, token);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}