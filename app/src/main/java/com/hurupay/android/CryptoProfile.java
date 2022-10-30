package com.hurupay.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.hurupay.android.adapters.TransactionAdapter;
import com.hurupay.android.constants.Constants;
import com.hurupay.android.listeners.AccountItemClickListener;
import com.hurupay.android.models.Account;
import com.hurupay.android.models.Token;
import com.hurupay.android.models.Transaction;
import com.hurupay.android.models.User;
import com.hurupay.android.utils.GetUser;
import com.hurupay.android.utils.RecyclerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class  CryptoProfile extends AppCompatActivity implements AccountItemClickListener, View.OnClickListener {

    private final Context mContext = CryptoProfile.this;
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private final List<Transaction> objectList = new ArrayList<>();
    private TransactionAdapter adapter;
    private Token token;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto_profile);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        token = (Token) bundle.getSerializable(Constants.OBJECT);
        user = GetUser.getUser(mContext, currentUserID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(token.getTitle());
        getSupportActionBar().setSubtitle(token.getSymbol());
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        CardView depositCardView = findViewById(R.id.depositCardView);
        CardView sendCardView = findViewById(R.id.sendCardView);
        CardView exchangeCardView = findViewById(R.id.exchangeCardView);
        CardView withdrawCardView = findViewById(R.id.withdrawCardView);
        TextView textView = findViewById(R.id.textView);
        TextView subTextView = findViewById(R.id.subTextView);
        TextView summaryTextView = findViewById(R.id.summaryTextView);
        ImageView imageView = findViewById(R.id.imageView);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        imageView.setTransitionName(token.getTokenAddress());
        textView.setText(getString(R.string.token_price, GetUser.fetchObject(mContext, token.getTokenAddress()), token.getSymbol()));
        subTextView.setText(token.getSymbol());
        summaryTextView.setText(token.getSummary());
        depositCardView.setOnClickListener(this);
        sendCardView.setOnClickListener(this);
        exchangeCardView.setOnClickListener(this);
        withdrawCardView.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        RecyclerItemDecoration recyclerItemDecoration = new RecyclerItemDecoration(mContext, getResources().getDimensionPixelSize(R.dimen.header_height), true, getSectionCallback(objectList));
        recyclerView.addItemDecoration(recyclerItemDecoration);

        adapter = new TransactionAdapter(mContext, objectList, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        fetchObjects(user.getAddress(), token.getTokenAddress());

        if (token.getTags().contains(user.getAddress())){
            imageView.setImageResource(R.drawable.ic_star_red_24dp);
        } else {
            imageView.setImageResource(R.drawable.ic_star_border_red_24dp);
        }
    }

    private void fetchObjects(String objectID, String tokenAddress){
        Query query = firebaseFirestore.collection(Constants.TRANSACTIONS).orderBy("hash", Query.Direction.DESCENDING).whereEqualTo("tokenAddress",tokenAddress).whereArrayContains("tags",objectID);
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
        fetchObjects(user.getAddress(), token.getTokenAddress());
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
        if (view.getId() == R.id.depositCardView) {
            startActivity(new Intent(mContext, Deposit.class));
        } else if (view.getId() == R.id.sendCardView) {
            startActivity(new Intent(mContext, ScanQR.class));
        } else if (view.getId() == R.id.exchangeCardView) {
            startActivity(new Intent(mContext, QRDetail.class));
        } else if (view.getId() == R.id.withdrawCardView){
            startActivity(new Intent(mContext, Withdraw.class));
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
        Intent intent = new Intent(mContext, UserProfile.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.OBJECT,account);
        intent.putExtras(bundle);
        intent.putExtra(Constants.OBJECT_ID,objectID);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create(imageView, account.getAddress()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }
}