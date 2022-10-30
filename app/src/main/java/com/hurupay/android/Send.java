package com.hurupay.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.functions.FirebaseFunctions;
import com.hurupay.android.adapters.TokenAdapter;
import com.hurupay.android.constants.Constants;
import com.hurupay.android.listeners.TokenItemClickListener;
import com.hurupay.android.models.Account;
import com.hurupay.android.models.Token;
import com.hurupay.android.models.User;
import com.hurupay.android.utils.GetUser;
import com.hurupay.android.utils.ScreenUtils;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Send extends AppCompatActivity implements View.OnClickListener, TokenItemClickListener {
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final Context mContext = Send.this;
    private final FirebaseFunctions firebaseFunctions = FirebaseFunctions.getInstance();

    private RelativeLayout container;
    private TextInputEditText amountTextInputEditText;
    private AVLoadingIndicatorView progressBar;
    private BottomSheetDialog bottomSheetTokenDialog;
    private ImageView coinImageView;
    private TextView symbolTextView;
    private TextView chargesTextView;
    private TextView titleTextView;
    private Button button;
    private User user;
    private Account account;
    private Token tokenObject;

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private TokenAdapter adapter;
    private final List<Token> objectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        account = (Account) bundle.getSerializable(Constants.OBJECT);

        tokenObject = Constants.CUSD_TOKEN;

        user = GetUser.getUser(mContext, currentUserID);
        container = findViewById(R.id.container);
        titleTextView = findViewById(R.id.titleTextView);
        ImageView closeImageView = findViewById(R.id.closeImageView);
        ImageView senderImageView = findViewById(R.id.senderImageView);
        TextView senderTextView = findViewById(R.id.senderTextView);
        symbolTextView = findViewById(R.id.symbolTextView);
        chargesTextView = findViewById(R.id.chargesTextView);

        ImageView receiverImageView = findViewById(R.id.receiverImageView);
        TextView receiverTextView = findViewById(R.id.receiverTextView);

        coinImageView = findViewById(R.id.coinImageView);
        amountTextInputEditText = findViewById(R.id.amountTextInputEditText);
        progressBar = findViewById(R.id.progressBar);
        button = findViewById(R.id.button);
        amountTextInputEditText.requestFocus();
        button.setOnClickListener(this);
        titleTextView.setOnClickListener(this);
        closeImageView.setOnClickListener(this);

        Glide.with(mContext.getApplicationContext()).load(user.getPic()).placeholder(R.drawable.logo).into(senderImageView);
        senderTextView.setText(user.getAddress());

        Glide.with(mContext.getApplicationContext()).load(account.getPic()).placeholder(R.drawable.logo).into(receiverImageView);
        receiverTextView.setText(account.getAddress());

        Glide.with(mContext.getApplicationContext()).load(tokenObject.getPic()).placeholder(R.drawable.logo).into(coinImageView);
        symbolTextView.setText(tokenObject.getSymbol());
        titleTextView.setText(tokenObject.getSymbol());

        fetchObjects(user.getAddress());
        adapter = new TokenAdapter(mContext, objectList, this);

        amountTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    chargesTextView.setText(mContext.getString(R.string.token_price,s.toString(),tokenObject.getSymbol()));
                    if (Integer.parseInt(s.toString())>Integer.parseInt(user.getBalance())){
                        amountTextInputEditText.setError("Insufficient Balance");
                        amountTextInputEditText.requestFocus();
                        button.setEnabled(false);
                    } else {
                        button.setEnabled(true);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }@Override public void afterTextChanged(Editable s) { }
        });
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
        if (v.getId()==R.id.button){
            String amount = amountTextInputEditText.getText().toString().trim();
            if (TextUtils.isEmpty(amount)){
                amountTextInputEditText.requestFocus();
                amountTextInputEditText.setError("Enter Amount");
                progressBar.setVisibility(View.GONE);
                button.setEnabled(true);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                button.setEnabled(false);

                Map<String, Object> data = new HashMap<>();
                data.put("recipient", account.getAddress());
                data.put("key", user.getPrivateKey());
                data.put("value", amount);
                data.put("currency", tokenObject.getSymbol());
                data.put("tokenAddress", tokenObject.getTokenAddress());
                data.put("date", getDate(System.currentTimeMillis()));
                data.put("summary", user.getUsername()+ "sent "+amount+" to your wallet address");
                data.put("token", account.getToken());

                firebaseFunctions.getHttpsCallable("sendCUSD").call(data).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Map<String, Object> result = (Map<String, Object>) task.getResult().getData();
                        assert result != null;

                        String balance = Objects.requireNonNull(result.get("balance")).toString();
                        String responses = Objects.requireNonNull(result.get("responses")).toString();

                        SharedPreferences.Editor editor = getSharedPreferences(Constants.USERS,Context.MODE_PRIVATE).edit();
                        editor.putString(Constants.BALANCE, Objects.requireNonNull(result.get("balance")).toString());
                        editor.apply();

                        GetUser.saveObject(mContext,tokenObject.getTokenAddress(),balance);
                        GetUser.saveObject(mContext,"balance",balance);

                        confirmTransaction(responses);
                        button.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);

                        Toast.makeText(mContext, result.toString(),Toast.LENGTH_SHORT).show();
                    } else {
                        Snackbar snackbar = Snackbar.make(container, Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()), Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        button.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        } else if (v.getId()==R.id.closeImageView) {
            finishAfterTransition();
        } else if (v.getId()==R.id.coinImageView){
            selectToken();
        }
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
        Glide.with(mContext.getApplicationContext()).load(token.getPic()).placeholder(R.drawable.placeholder).into(coinImageView);
        symbolTextView.setText(token.getSymbol());
        titleTextView.setText(token.getSymbol());
        tokenObject = token;
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