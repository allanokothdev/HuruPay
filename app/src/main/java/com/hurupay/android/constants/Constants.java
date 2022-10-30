package com.hurupay.android.constants;

import com.hurupay.android.models.Token;

import java.util.ArrayList;

public class Constants {

    public final static int NOTIFICATION_ID = 0;
    public final static String NOTIFICATION = "notification";
    public final static String CHANNEL_ID = "HuruPay";
    public final static String CHANNEL_NAME = "HuruPay";
    public final static String CHANNEL_DESCRIPTION = "HuruPay";

    public final static String CELO_DOLLAR_PIC = "https://firebasestorage.googleapis.com/v0/b/eazycrypto-14ab2.appspot.com/o/tokens%2Fcelodollar.webp?alt=media&token=ffe4fb5d-c89d-4c84-87f4-252c37fc476a";

    public final static String PIC = "pic";
    public final static String NAME = "HuruPay Inc";
    public final static String EMAIL = "support@hurupayinc.com";
    public final static String BALANCE = "0";
    public final static String PHONE_NUMBER = "+254700000000";
    public final static String WALLET_ADDRESS = "WALLET_ADDRESS";
    public final static String PRIVATE_KEY = "PRIVATE_KEY";
    public final static String COUNTRY = "country";
    public final static String USERNAME = "username";
    public final static String CURRENCY = "currency";
    public final static String JOININGdate = "JOININGdate";

    public final static String IDENTITY_NO = "IDENTITY_NO";
    public final static String IDENTITY_CARD = "IDENTITY_CARD";
    public final static String REAL_PIC = "REAL_PIC";
    public final static String VERIFIED = "VERIFIED";

    public final static String PRIVACY_POLICY = "https://hurupay.wordpress.com/huru-pay-privacy-policy/";

    public final static String OBJECT = "object";
    public final static String OBJECT_ID = "objectID";

    public final static String SEND_MONEY = "Send Money";
    public final static String DEPOSIT = "Deposit";
    public final static String WITHDRAW = "Withdraw";
    public final static String SWAP = "Swap";

    public final static String TOKEN = "token";
    public final static String TOKEN_NOTIFICATION = "notifications";
    public final static String GROUPS_TOKENS = "grouptokens";

    public final static String ACCOUNTS = "accounts";
    public final static String USERS = "users";
    public final static String BOOKMARKS = "bookmarks";
    public final static String CONTACTS = "contacts";
    public final static String FAVORITES = "favorites";
    public final static String TOKENS = "tokens";
    public final static String TRANSACTIONS = "transactions";

    public final static Integer CELO_NATIVE = 0;
    public final static Integer CELO_DOLLAR = 1;
    public final static Integer CELO_EURO = 2;

    public final static String ERC_20 = "ERC_20";
    public final static String CELO = "0xF194afDf50B03e69Bd7D057c1Aa9e10c9954E4C9";
    public final static String CUSD = "0x874069Fa1Eb16D44d622F2e0Ca25eeA172369bC1";
    public final static String CEUR = "0x10c892A6EC43a53E45D0B916B4b7D383B1b78C0F";

    public final static ArrayList<String> tags = new ArrayList<>();
    public final static String CUSD_SUMMARY = "Celo Dollar (cUSD) is a stablecoin created by Celo that has been pegged to follow the US Dollar. This stablecoin is making financial solutions like making transfers and accepting payments faster, cheaper and easy to access";
    public final static Token CUSD_TOKEN = new Token(CUSD,CELO_DOLLAR_PIC,"Celo Dollar",CUSD_SUMMARY,18,"ERC-20","cUSD",CELO_DOLLAR, tags);



}
