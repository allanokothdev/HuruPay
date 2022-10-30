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
import com.hurupay.android.R;
import com.hurupay.android.constants.Constants;
import com.hurupay.android.listeners.TokenItemClickListener;
import com.hurupay.android.models.Token;
import com.hurupay.android.utils.GetUser;

import java.util.List;

public class TokenAdapter extends RecyclerView.Adapter{

    private final Context mContext;
    private final List<Token> stringList;
    private final TokenItemClickListener tokenItemClickListener;

    public TokenAdapter(Context mContext, List<Token> stringList, TokenItemClickListener tokenItemClickListener) {
        this.stringList = stringList;
        this.mContext = mContext;
        this.tokenItemClickListener = tokenItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {

        Token token = stringList.get(position);
        if (token.getType()== Constants.CELO_NATIVE){
            return Constants.CELO_NATIVE;
        } else if (token.getType()==Constants.CELO_EURO){
            return Constants.CELO_EURO;
        } else {
            return Constants.CELO_DOLLAR;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Constants.CELO_NATIVE){
            return new CeloViewHolder(LayoutInflater.from(mContext).inflate(R.layout.token_item, parent, false));
        }else if (viewType == Constants.CELO_EURO){
            return new CeloEuroViewHolder(LayoutInflater.from(mContext).inflate(R.layout.token_item, parent, false));
        }else {
            return new CeloDollarViewHolder(LayoutInflater.from(mContext).inflate(R.layout.token_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        Token token = stringList.get(position);
        if (token.getType()== Constants.CELO_NATIVE){
            ((CeloViewHolder) holder).bind(position);
        } else if (token.getType()==Constants.CELO_EURO){
            ((CeloEuroViewHolder) holder).bind(position);
        } else {
            ((CeloDollarViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class CeloDollarViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        private final TextView textView;
        private final TextView subTextView;
        private final TextView subItemTextView;

        private CeloDollarViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
            subItemTextView = itemView.findViewById(R.id.subItemTextView);
        }

        void bind(int position) {

            Token token = stringList.get(position);
            Glide.with(mContext.getApplicationContext()).load(token.getPic()).placeholder(R.drawable.logo).into(imageView);
            textView.setText(token.getTitle());
            subTextView.setText(token.getSymbol());
            subItemTextView.setText(mContext.getString(R.string.token_price, GetUser.fetchObject(mContext, token.getTokenAddress()), token.getSymbol()));
            itemView.setOnClickListener(v -> tokenItemClickListener.onTokenItemClick(token,imageView));

        }
    }

    public class CeloEuroViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        private final TextView textView;
        private final TextView subTextView;
        private final TextView subItemTextView;

        private CeloEuroViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
            subItemTextView = itemView.findViewById(R.id.subItemTextView);
        }

        void bind(int position) {

            Token token = stringList.get(position);
            Glide.with(mContext.getApplicationContext()).load(token.getPic()).placeholder(R.drawable.logo).into(imageView);
            textView.setText(token.getTitle());
            subTextView.setText(token.getSymbol());
            subItemTextView.setText(mContext.getString(R.string.token_price, GetUser.fetchObject(mContext, token.getTokenAddress()), token.getSymbol()));
            itemView.setOnClickListener(v -> tokenItemClickListener.onTokenItemClick(token,imageView));


        }
    }

    public class CeloViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        private final TextView textView;
        private final TextView subTextView;
        private final TextView subItemTextView;

        private CeloViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
            subItemTextView = itemView.findViewById(R.id.subItemTextView);
        }

        void bind(int position) {

            Token token = stringList.get(position);
            Glide.with(mContext.getApplicationContext()).load(token.getPic()).placeholder(R.drawable.logo).into(imageView);
            textView.setText(token.getTitle());
            subTextView.setText(token.getSymbol());
            subItemTextView.setText(mContext.getString(R.string.token_price, GetUser.fetchObject(mContext, token.getTokenAddress()), token.getSymbol()));
            itemView.setOnClickListener(v -> tokenItemClickListener.onTokenItemClick(token,imageView));

        }
    }



}
