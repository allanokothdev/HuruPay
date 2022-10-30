package com.hurupay.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
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
import com.hurupay.android.UserProfile;
import com.hurupay.android.adapters.TransactionAdapter;
import com.hurupay.android.adapters.UserAdapter;
import com.hurupay.android.constants.Constants;
import com.hurupay.android.listeners.AccountItemClickListener;
import com.hurupay.android.listeners.TransactionItemClickListener;
import com.hurupay.android.models.Account;
import com.hurupay.android.models.Transaction;
import com.hurupay.android.models.User;
import com.hurupay.android.utils.RecyclerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransactionFragment extends Fragment implements AccountItemClickListener {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private TransactionAdapter adapter;
    private User user;
    private final List<Transaction> objectList = new ArrayList<>();

    public TransactionFragment() {
        // Required empty public constructor
    }

    public static TransactionFragment getInstance(User user){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.OBJECT, user);
        TransactionFragment fragment = new TransactionFragment();
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
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerItemDecoration recyclerItemDecoration = new RecyclerItemDecoration(requireContext(),getResources().getDimensionPixelSize(R.dimen.header_height),true,getSectionCallback(objectList));
        recyclerView.addItemDecoration(recyclerItemDecoration);
        adapter = new TransactionAdapter(getContext(), objectList, this);
        recyclerView.setAdapter(adapter);
        fetchObjects(user.getAddress());
        return view;
    }

    private RecyclerItemDecoration.SectionCallback getSectionCallback(final List<Transaction> objectList) {
        return new RecyclerItemDecoration.SectionCallback() {
            @Override public boolean isSection(int pos) {
                return pos==0 || !objectList.get(pos).getTimestamp().equals(objectList.get(pos - 1).getTimestamp());
            }
            @Override public String getSectionHeaderName(int pos) {
                Transaction transaction = objectList.get(pos);
                return transaction.getTimestamp();
            }
        };
    }

    private void fetchObjects(String objectID){
        Query query = firebaseFirestore.collection(Constants.TRANSACTIONS).orderBy("hash", Query.Direction.DESCENDING).whereArrayContains("tags",objectID);
        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Transaction object = documentChange.getDocument().toObject(Transaction.class);
                        if (!objectList.contains(object)){
                            objectList.add(object);
                            adapter.notifyItemInserted(objectList.size()-1);
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.MODIFIED){
                        Transaction object = documentChange.getDocument().toObject(Transaction.class);
                        if (objectList.contains(object)){
                            objectList.set(objectList.indexOf(object),object);
                            adapter.notifyItemChanged(objectList.indexOf(object));
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.REMOVED){
                        Transaction object = documentChange.getDocument().toObject(Transaction.class);
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