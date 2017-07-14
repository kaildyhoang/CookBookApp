package com.example.kaildyhoang.mycookbookapplication.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.kaildyhoang.mycookbookapplication.R;

/**
 * Created by IBM on 7/10/2017.
 */

public class Notifi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v_activity_notifi);
        setupToolbar();
    }
    void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
