package com.example.kaildyhoang.mycookbookapplication.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.kaildyhoang.mycookbookapplication.MainActivity;

public class Adapter_ViewPagerFragment extends FragmentPagerAdapter {
    private int page_num;
    public Adapter_ViewPagerFragment(FragmentManager fm, int page_num) {
        super(fm);
        this.page_num = page_num;
    }
    @Override
    public Fragment getItem(int position) {

        Fragment frag=null;
        switch (position){
            case 0:
               frag= new Left_Activity();

                break;
            case 1:
                frag= new MainActivity();
                break;
            case 2:
                frag= new Right_Activity();
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return page_num;
    }
}