package com.hurupay.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hurupay.android.constants.Constants;
import com.hurupay.android.models.Notify;
import com.hurupay.android.models.User;
import com.hurupay.android.celo.CeloBalance;
import com.hurupay.android.utils.GetUser;
import com.hurupay.android.viewpager.MainPagerAdapter;

import org.celo.contractkit.ContractKit;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CeloBalance.Callback {

    private final Context mContext = MainActivity.this;
    private long pressedTime;
    private final String TAG = this.getClass().getSimpleName();
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final FirebaseFunctions firebaseFunctions = FirebaseFunctions.getInstance();
    private User user;
    private static String[] tabList;
    CeloBalance mAsyncRetriever;

    @Override
    protected void onStart() {
        super.onStart();
        mAsyncRetriever = new CeloBalance(mContext,this);
        mAsyncRetriever.execute(ContractKit.ALFAJORES_TESTNET);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = GetUser.getUser(mContext,currentUserID);
        tabList = getResources().getStringArray(R.array.categories);

        CardView depositCardView = findViewById(R.id.depositCardView);
        CardView sendCardView = findViewById(R.id.sendCardView);
        CardView receiveCardView = findViewById(R.id.receiveCardView);
        CardView withdrawCardView = findViewById(R.id.withdrawCardView);
        TextView subTextView = findViewById(R.id.subTextView);
        CardView cardView = findViewById(R.id.cardView);
        ImageView imageView = findViewById(R.id.imageView);
        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        Glide.with(mContext.getApplicationContext()).load(user.getPic()).placeholder(R.drawable.logo).into(imageView);
        subTextView.setText(getString(R.string.price_with_currency_string,user.getBalance()));

        cardView.setOnClickListener(this);
        depositCardView.setOnClickListener(this);
        sendCardView.setOnClickListener(this);
        receiveCardView.setOnClickListener(this);
        withdrawCardView.setOnClickListener(this);
        fetchUserInfo(currentUserID);
        setUpViewPager(viewPager,tabLayout,user);
    }


    private void setUpViewPager(ViewPager2 viewPager, TabLayout tabLayout, User user) {
        MainPagerAdapter adapter = new MainPagerAdapter(this, user);
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabList[position])).attach();
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    @Override public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else {
            Toast.makeText(mContext, "Press Back Again to Exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();

    }


    @Override
    public void onResume() {
        super.onResume();
        mAsyncRetriever = new CeloBalance(mContext,this);
        mAsyncRetriever.execute(ContractKit.ALFAJORES_TESTNET);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAsyncRetriever != null) {
            mAsyncRetriever.cancel(true);
            mAsyncRetriever = null;
        }
    }


    private void fetchUserInfo(String currentUserID){
        FirebaseFirestore.getInstance().collection(Constants.USERS).document(currentUserID).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                user = documentSnapshot.toObject(User.class);
                assert user != null;
                GetUser.saveUser(mContext,user);
                fetchToken(user);
                //fetchBalance(user.getPrivateKey());
            } else {
                startActivity(new Intent(mContext, UpdateUserProfile.class));
            }
        });
    }

    private void fetchToken(User user){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                String token = task.getResult();
                String msg = getString(R.string.msg_token_fmt, token);
                Timber.d(msg);

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("token",token);
                FirebaseDatabase.getInstance().getReference(Constants.TOKEN).child(currentUserID).updateChildren(hashMap);

                Notify groupNotify = new Notify(token);
                FirebaseDatabase.getInstance().getReference(Constants.GROUPS_TOKENS).child(user.getCountry()).child(user.getId()).setValue(groupNotify);

                SharedPreferences.Editor editor = getSharedPreferences(Constants.USERS,Context.MODE_PRIVATE).edit();
                editor.putString(Constants.TOKEN, token);
                editor.apply();

                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put("token",token);
                FirebaseFirestore.getInstance().collection(Constants.USERS).document(currentUserID).update(userMap);
                FirebaseFirestore.getInstance().collection(Constants.ACCOUNTS).document(user.getAddress()).update(userMap);

            }else {
                Timber.tag(TAG).w(task.getException(), "Fetching FCM registration token failed");
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.depositCardView) {
            startActivity(new Intent(mContext, Deposit.class));
        } else if (view.getId() == R.id.sendCardView) {
            startActivity(new Intent(mContext, ScanQR.class));
        } else if (view.getId() == R.id.receiveCardView) {
            startActivity(new Intent(mContext, QRDetail.class));
        } else if (view.getId() == R.id.withdrawCardView){
            startActivity(new Intent(mContext, Withdraw.class));
        } else if (view.getId()==R.id.cardView){
            startActivity(new Intent(mContext, Settings.class));
        }
    }

    private void fetchBalance(String privateKey) {
        Map<String, Object> data = new HashMap<>();
        data.put("key", privateKey);
        firebaseFunctions.getHttpsCallable("fetchBalance").call(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                String balance = Objects.requireNonNull(task.getResult().getData()).toString();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("balance",balance);
                firebaseFirestore.collection(Constants.USERS).document(currentUserID).update(hashMap);
            } else {
                Objects.requireNonNull(task.getException()).printStackTrace();
            }
        });
    }

}
