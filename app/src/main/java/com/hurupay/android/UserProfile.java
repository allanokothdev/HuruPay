package com.hurupay.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.hurupay.android.adapters.TransactionAdapter;
import com.hurupay.android.constants.Constants;
import com.hurupay.android.listeners.AccountItemClickListener;
import com.hurupay.android.models.Account;
import com.hurupay.android.models.Transaction;
import com.hurupay.android.models.User;
import com.hurupay.android.utils.GetUser;
import com.hurupay.android.utils.RecyclerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserProfile extends AppCompatActivity implements AccountItemClickListener, View.OnClickListener {

    private final Context mContext = UserProfile.this;
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private final List<Transaction> objectList = new ArrayList<>();
    private TransactionAdapter adapter;

    private Account account;
    private String objectID;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        account = (Account) bundle.getSerializable(Constants.OBJECT);
        objectID = intent.getStringExtra(Constants.OBJECT_ID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(account.getUsername());
        getSupportActionBar().setSubtitle(account.getAddress());
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        user = GetUser.getUser(mContext,currentUserID);
        Button button = findViewById(R.id.button);
        ImageView moreImageView = findViewById(R.id.moreImageView);
        ImageView imageView = findViewById(R.id.imageView);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        imageView.setTransitionName(account.getAddress());
        Glide.with(mContext.getApplicationContext()).load(account.getPic()).placeholder(R.drawable.logo).into(imageView);

        moreImageView.setOnClickListener(this);
        button.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        RecyclerItemDecoration recyclerItemDecoration = new RecyclerItemDecoration(mContext,getResources().getDimensionPixelSize(R.dimen.header_height),true,getSectionCallback(objectList));
        recyclerView.addItemDecoration(recyclerItemDecoration);

        adapter = new TransactionAdapter(mContext, objectList, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        fetchObjects(objectID);
    }

    private void fetchObjects(String objectID){
        Query query = firebaseFirestore.collection(Constants.TRANSACTIONS).orderBy("timestamp", Query.Direction.DESCENDING).whereArrayContains("tags",objectID);
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
        fetchObjects(objectID);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (registration != null){
            registration.remove();
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.OBJECT,account);
            startActivity(new Intent(mContext, Send.class).putExtras(bundle));
        } else if (view.getId() == R.id.moreImageView) {
            firebaseFirestore.collection(Constants.BOOKMARKS).document(user.getAddress()).collection(Constants.CONTACTS).document(account.getAddress()).set(account).addOnSuccessListener(unused -> Toast.makeText(mContext, account.getUsername()+" was successfully added in your contacts",Toast.LENGTH_SHORT).show());

        }
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


    @Override
    public void onAccountItemClick(Account account, ImageView imageView, String objectID) {

    }
}
