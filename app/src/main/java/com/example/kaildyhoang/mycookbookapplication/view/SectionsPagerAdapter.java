package com.example.kaildyhoang.mycookbookapplication.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public Fragment getItem(int position) {

        Fragment frag=null;
        switch (position){
            case 0:
                frag= new MyMethodActivity();
                break;
            case 1:
                frag= new BookMarkActivity();
                break;

        }
        return frag;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "CT CỦA TÔI";
            case 1:
                return "CT ĐÃ LƯU";
        }
        return null;
    }
}