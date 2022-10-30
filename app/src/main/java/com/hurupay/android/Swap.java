package com.hurupay.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.hurupay.android.adapters.TokenAdapter;
import com.hurupay.android.constants.Constants;
import com.hurupay.android.listeners.TokenItemClickListener;
import com.hurupay.android.models.Account;
import com.hurupay.android.models.Token;
import com.hurupay.android.models.User;
import com.hurupay.android.utils.GetUser;
import com.hurupay.android.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Swap extends AppCompatActivity implements View.OnClickListener, TokenItemClickListener {
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final Context mContext = Swap.this;
    private BottomSheetDialog bottomSheetTokenDialog;
    private Button button;
    private Account account;

    private Token fromToken = Constants.CUSD_TOKEN;
    private Token toToken = Constants.CUSD_TOKEN;

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private TokenAdapter adapter;
    private final List<Token> objectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swap);


        User user = GetUser.getUser(mContext, currentUserID);

        fetchObjects(user.getAddress());
        adapter = new TokenAdapter(mContext, objectList, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registration != null){
            registration.remove();
        }
    }

    @Override
    public void onClick(View v) {
    }

    private void confirmTransaction(String url){
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_confirm, null);
        ImageView closeImageView = view.findViewById(R.id.closeImageView);
        TextView textView = view.findViewById(R.id.textView);

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        ScreenUtils screenUtils = new ScreenUtils(mContext);
        behavior.setPeekHeight(screenUtils.getHeight());
        closeImageView.setOnClickListener(view1 -> {

            bottomSheetDialog.dismiss();
            finishAfterTransition();
        });

        textView.setOnClickListener(view12 -> {
            Intent indie = new Intent(Intent.ACTION_VIEW);
            indie.setData(Uri.parse(url));
            startActivity(indie);
            bottomSheetDialog.dismiss();
            finishAfterTransition();
        });
    }

    private String getDate(long time){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time);
        return DateFormat.format("EEEE, dd MMMM yyyy", cal).toString();
    }

    @Override
    public void onTokenItemClick(Token token, ImageView imageView) {
        fromToken = token;
        bottomSheetTokenDialog.dismiss();
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

    private void selectToken(){
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_portfolio, null);
        ImageView closeImageView = view.findViewById(R.id.closeImageView);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        bottomSheetTokenDialog = new BottomSheetDialog(mContext);
        bottomSheetTokenDialog.setContentView(view);
        bottomSheetTokenDialog.show();

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        ScreenUtils screenUtils = new ScreenUtils(mContext);
        behavior.setPeekHeight(screenUtils.getHeight());

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        closeImageView.setOnClickListener(view1 -> {
            bottomSheetTokenDialog.dismiss();
        });
    }
}