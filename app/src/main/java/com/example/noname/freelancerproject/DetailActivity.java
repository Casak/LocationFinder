package com.example.noname.freelancerproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            // Testing commiting

            setContentView(com.example.noname.freelancerproject.R.layout.activity_detail);

            Intent detailIntent = getIntent();
            //Clicked item id from mainActivity

            JSONObject jsonObj = new JSONObject(detailIntent.getStringExtra("clickedItem"));

            TabLayout tabLayout = (TabLayout) findViewById(com.example.noname.freelancerproject.R.id.tabLayout);
            ViewPager viewPager = (ViewPager) findViewById(com.example.noname.freelancerproject.R.id.viewPager);

            ArrayList<Fragment> fragmentList = new ArrayList<>();
            ArrayList<String> Tab1Data = new ArrayList<>();
            ArrayList<String> Tab2Data = new ArrayList<>();



            String itemDescription = "";


            fragmentList.add(TabLayoutFragment.newInstance(this,Tab1Data));
            fragmentList.add(TabLayoutFragment.newInstance(this,Tab1Data));
            fragmentList.add(TabLayoutFragment.newInstance(this,Tab2Data));
            fragmentList.add(TabLayoutFragment.newInstance(this,Tab1Data));

            ViewPagerAdapter customAdapterViewPager = new ViewPagerAdapter(getSupportFragmentManager(),fragmentList);
            viewPager.setAdapter(customAdapterViewPager);
            tabLayout.setupWithViewPager(viewPager);

            tabLayout.getTabAt(0).setText("tab (0)");
            tabLayout.getTabAt(1).setText("tab (0)");
            tabLayout.getTabAt(2).setText("tab (0)");
            tabLayout.getTabAt(3).setText("tab (0)");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
