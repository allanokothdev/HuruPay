package com.hurupay.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hurupay.android.R;
import com.hurupay.android.constants.Constants;
import com.hurupay.android.listeners.AccountItemClickListener;
import com.hurupay.android.models.Account;
import com.hurupay.android.models.Transaction;
import com.hurupay.android.models.User;
import com.hurupay.android.utils.GetUser;

import java.util.List;
import java.util.Objects;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder>{

    private final Context mContext;
    private final List<Transaction> stringList;
    private final AccountItemClickListener accountItemClickListener;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    public TransactionAdapter(Context mContext, List<Transaction> stringList, AccountItemClickListener accountItemClickListener) {
        this.stringList = stringList;
        this.mContext = mContext;
        this.accountItemClickListener = accountItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.transaction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        private final TextView textView;
        private final TextView subTextView;
        private final TextView subItemTextView;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
            subItemTextView = itemView.findViewById(R.id.subItemTextView);
        }

        void bind(int position) {

            Transaction transaction = stringList.get(position);
            User user = GetUser.getUser(mContext,currentUserID);
            if (user.getAddress().equals(transaction.getSender())){
                fetchObject(transaction.getRecipient(),imageView, transaction.getSender()+transaction.getRecipient(),itemView);
                textView.setText(R.string.send_money);
                subTextView.setText(transaction.getRecipient());
            } else {
                fetchObject(transaction.getSender(),imageView, transaction.getSender()+transaction.getRecipient(),itemView);
                textView.setText(R.string.payment_received);
                subTextView.setText(transaction.getSender());
            }
            subItemTextView.setText(mContext.getString(R.string.token_price,transaction.getValue(),transaction.getCurrency()));
        }
    }

    private void fetchObject(String address, ImageView imageView, String objectID, View itemView){
        firebaseFirestore.collection(Constants.ACCOUNTS).document(address).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                Account account = documentSnapshot.toObject(Account.class);
                assert account != null;
                imageView.setTransitionName(account.getAddress());
                Glide.with(mContext.getApplicationContext()).load(account.getPic()).placeholder(R.drawable.logo).into(imageView);
                itemView.setOnClickListener(v -> accountItemClickListener.onAccountItemClick(account,imageView,objectID));
            } else {
                Glide.with(mContext.getApplicationContext()).load(R.drawable.logo).into(imageView);
            }
        });
    }

}
