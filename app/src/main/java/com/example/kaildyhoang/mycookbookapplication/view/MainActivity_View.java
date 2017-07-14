package com.example.kaildyhoang.mycookbookapplication.view;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.example.kaildyhoang.mycookbookapplication.R;

import static com.example.kaildyhoang.mycookbookapplication.R.id.viewpager;


public class MainActivity_View extends AppCompatActivity {
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v_activity_mainview);
        viewPager = (ViewPager) findViewById(viewpager);
        Adapter_ViewPagerFragment adapter_viewPagerFragment =new Adapter_ViewPagerFragment(getSupportFragmentManager(),3);
        viewPager.setAdapter(adapter_viewPagerFragment);
        //activity 0 1 2
        //set 1



        viewPager.setCurrentItem(1);
       // viewPager.setClipToPadding(false);
        // set padding manually, the more you set the padding the more you see of prev & next page

       // viewPager.setPadding(40, 0, 0, 0);
        // sets a margin b/w individual pages to ensure that there is a gap b/w them
            //set color toolbar
        /* ActionBar actionBar = getSupportActionBar();
        final int actionBarColor = getResources().getColor(R.color.actionbar_background);
        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }



}