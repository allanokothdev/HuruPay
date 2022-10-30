package com.hurupay.android;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.hurupay.android.constants.Constants;
import com.hurupay.android.models.Token;
import com.hurupay.android.models.User;
import com.hurupay.android.utils.GetUser;

import java.util.ArrayList;
import java.util.Objects;

public class About extends AppCompatActivity {

    private final ArrayList<Token> objectList = new ArrayList<>();
    private final Context mContext = About.this;
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        User user = GetUser.getUser(mContext,currentUserID);

        ArrayList<String> tags = new ArrayList<>();
        tags.add(user.getAddress());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_blue_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        String sDola = "Celo Dollar (cUSD) is a stablecoin created by Celo that has been pegged to follow the US Dollar. This stablecoin is making financial solutions like making transfers and accepting payments faster, cheaper and easy to access";
        String sNative = "Celo (CELO) is the protocol's native asset. It serves as a utility that enables users to participate in network consensus (through its Proof-of-Stake system) pay for on-chain transactions, and vote on governance decisions";
        String sEuro = "Celo EURO (cEUR) is a stablecoin created by Celo that has been pegged to follow the EURO. This stablecoin is making financial solutions like making transfers and accepting payments faster, cheaper and easy to access";

        String dollar ="https://firebasestorage.googleapis.com/v0/b/eazycrypto-14ab2.appspot.com/o/tokens%2Fcelodollar.webp?alt=media&token=ffe4fb5d-c89d-4c84-87f4-252c37fc476a";
        String euro = "https://firebasestorage.googleapis.com/v0/b/eazycrypto-14ab2.appspot.com/o/tokens%2Fceloeuro.jpg?alt=media&token=fac07ca0-b616-4bb6-adaf-83b6d9c7699d";
        String nativ = "https://firebasestorage.googleapis.com/v0/b/eazycrypto-14ab2.appspot.com/o/tokens%2Fcelonative.jpg?alt=media&token=1610520a-fe2c-4837-81fb-8957d1b32b9f";

        Token celo = new Token("0xF194afDf50B03e69Bd7D057c1Aa9e10c9954E4C9",nativ,"Celo Native",sNative,18,"ERC-20","CELO", Constants.CELO_NATIVE, tags);
        Token cUSD = new Token("0x874069Fa1Eb16D44d622F2e0Ca25eeA172369bC1",dollar,"Celo Dollar",sDola,18,"ERC-20","cUSD",Constants.CELO_DOLLAR, tags);
        Token cEUR = new Token("0x10c892A6EC43a53E45D0B916B4b7D383B1b78C0F",euro,"Celo Euro",sEuro,18,"ERC-20","cEUR",Constants.CELO_EURO, tags);

        objectList.add(celo);
        objectList.add(cUSD);
        objectList.add(cEUR);

        for (Token token : objectList){
           // FirebaseFirestore.getInstance().collection(Constants.TOKENS).document(crypto.getTokenAddress()).set(crypto).addOnSuccessListener(unused -> Toast.makeText(mContext, "Done",Toast.LENGTH_SHORT).show());
        }
    }
}