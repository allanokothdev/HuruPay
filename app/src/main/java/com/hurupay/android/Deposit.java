package com.hurupay.android;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.hurupay.android.models.User;
import com.hurupay.android.utils.GetUser;

import java.util.Objects;

public class Deposit extends AppCompatActivity {

    private final Context mContext = Deposit.this;
    private final String TAG = this.getClass().getSimpleName();
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final String URL = "https://widget.onramper.com?color=266677&apiKey=pk_test__B6YFLMm1OpnHZR0eXGFxJxngycCQ1xPuhWwdvtTT0Q0&onlyCryptos=CUSD&onlyGateways=Moonpay&wallets=CELO:0x099dF29cEF2bFFE6248F8cFD5B03d905339A03D9";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        User user = GetUser.getUser(mContext, currentUserID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Deposit");
        getSupportActionBar().setSubtitle(user.getAddress());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_blue_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        WebView webView = findViewById(R.id.webView);
        webView.setWebViewClient(new Browser());
        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(fetchURL(user.getAddress()));
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
    }

    private String fetchURL(String address){
        return "https://widget.onramper.com?color=071629&apiKey=pk_test__B6YFLMm1OpnHZR0eXGFxJxngycCQ1xPuhWwdvtTT0Q0&onlyCryptos=CUSD&onlyGateways=Moonpay&wallets=CELO:"+address;
    }

    private static class Browser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url){
            webView.loadUrl(url);
            //webView.getProgress();
            return true;
        }
    }

    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }

}