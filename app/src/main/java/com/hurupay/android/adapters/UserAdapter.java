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
import com.hurupay.android.R;
import com.hurupay.android.listeners.AccountItemClickListener;
import com.hurupay.android.models.Account;
import com.hurupay.android.models.User;
import com.hurupay.android.utils.GetUser;

import java.util.List;
import java.util.Objects;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private final Context mContext;
    private final List<Account> stringList;
    private final AccountItemClickListener accountItemClickListener;
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    public UserAdapter(Context mContext, List<Account> stringList, AccountItemClickListener accountItemClickListener) {
        this.stringList = stringList;
        this.mContext = mContext;
        this.accountItemClickListener = accountItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
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

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
        }

        void bind(int position) {

            Account account = stringList.get(position);
            imageView.setTransitionName(account.getAddress());
            User user = GetUser.getUser(mContext,currentUserID);
            Glide.with(mContext.getApplicationContext()).load(account.getPic()).placeholder(R.drawable.logo).into(imageView);
            textView.setText(account.getUsername());
            subTextView.setText(account.getPhone());
            itemView.setOnClickListener(v -> accountItemClickListener.onAccountItemClick(account,imageView,user.getAddress()+account.getAddress()));
        }
    }

}
