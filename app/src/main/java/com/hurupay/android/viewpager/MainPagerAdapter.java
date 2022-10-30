package com.hurupay.android.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hurupay.android.fragments.BrandFragment;
import com.hurupay.android.fragments.ContactsFragment;
import com.hurupay.android.fragments.FavoritesFragment;
import com.hurupay.android.fragments.TransactionFragment;
import com.hurupay.android.models.User;

public class MainPagerAdapter extends FragmentStateAdapter  {

    private final User user;

    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity, User user) {
        super(fragmentActivity);
        this.user = user;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return TransactionFragment.getInstance(user);
            case 1:
                return FavoritesFragment.getInstance(user);
            case 2:
                return ContactsFragment.getInstance(user);
            case 3:
                return BrandFragment.getInstance(user);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 4;
    }


}
