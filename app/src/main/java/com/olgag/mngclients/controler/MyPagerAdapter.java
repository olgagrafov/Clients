package com.olgag.mngclients.controler;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.olgag.mngclients.R;
import com.olgag.mngclients.fragments.AppointmentFragment;
import com.olgag.mngclients.fragments.ClientFragment;
import com.olgag.mngclients.fragments.OrderFragment;

public class MyPagerAdapter  extends FragmentPagerAdapter  {
    private final int NUM_ITEMS =3;
    private Context context;
    private String userId;

    public MyPagerAdapter(FragmentManager fragmentManager, Context con, String userId) {
        super(fragmentManager);
        this.context = con;
        this.userId = userId;
    }

    public MyPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
         switch (position) {
            case 0:
                return AppointmentFragment.newInstance(0, "Page # 1", userId);
            case 1:
                return ClientFragment.newInstance(1, "Page # 2", userId);
            case 2:
               return OrderFragment.newInstance(2, "Page # 3", userId);
            default:
            	return null;
            }
    }


    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String pageName=this.context.getString(R.string.clients);
        switch (position) {
            case 0:
                pageName = this.context.getString(R.string.appointment);
              break;
            case 1:
                pageName = this.context.getString(R.string.clients) ;
              break;
            case 2:
                pageName = this.context.getString(R.string.orders) + "       ";
                break;
            default:
                return null;

        }
       return pageName;
    }

}
