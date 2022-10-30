package com.hurupay.android.celo;

import android.content.Context;
import android.os.AsyncTask;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hurupay.android.constants.Constants;
import com.hurupay.android.models.User;
import com.hurupay.android.utils.GetUser;

import org.celo.contractkit.AccountBalance;
import org.celo.contractkit.CeloContract;
import org.celo.contractkit.ContractKit;
import org.celo.contractkit.ContractKitOptions;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Objects;

import timber.log.Timber;

public class SendCeloEuro extends AsyncTask<String, Void, String> {

    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final Context context;
    AccountBalance accountBalance;
    ContractKit contractKit;
    private Callback callback;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public SendCeloEuro(Context context){
        this.context = context;
    }
    public interface Callback {
    }

    public SendCeloEuro(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    protected String doInBackground(String... urls) {
        try {
            User user = GetUser.getUser(context, currentUserID);
            Web3j web3j = Web3j.build(new HttpService(ContractKit.ALFAJORES_TESTNET));
            ContractKitOptions config = new ContractKitOptions.Builder().setFeeCurrency(CeloContract.GoldToken).setGasPrice(BigInteger.valueOf(21_000)).build();
            Timber.d("Web3 Object created");
            contractKit = new ContractKit(web3j,config);
            Timber.d("Contract kit object created");

            Credentials credentials;
            credentials = Credentials.create(user.getPrivateKey());
            contractKit.addAccount(credentials);
            accountBalance = contractKit.getTotalBalance(user.getAddress());
            return contractKit.getAddress();
        } catch (Exception e) {
            Timber.e(e.toString());
        } finally {
            Timber.d("Try/catch -> final execution completed");
        }
        return "DoInBackground -> Incomplete process";
    }

    protected void onPostExecute(String data){
        Timber.d("onPostExecute data: %s", data);
        try {
            BigDecimal cUSDSum = new BigDecimal(String.valueOf(accountBalance.cUSD));
            BigDecimal celoSum = new BigDecimal(String.valueOf(accountBalance.CELO));

            GetUser.saveObject(context,"balance",Convert.fromWei(cUSDSum, Convert.Unit.ETHER).toString());
            GetUser.saveObject(context,Constants.CELO,Convert.fromWei(celoSum, Convert.Unit.ETHER).toString());
            GetUser.saveObject(context,Constants.CUSD,Convert.fromWei(cUSDSum, Convert.Unit.ETHER).toString());
            //GetUser.saveObject(context,Constants.CEUR,Convert.fromWei(cUSDSum, Convert.Unit.ETHER).toString());

            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("balance",Convert.fromWei(cUSDSum, Convert.Unit.ETHER).toString());
            FirebaseFirestore.getInstance().collection(Constants.USERS).document(currentUserID).update(userMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

