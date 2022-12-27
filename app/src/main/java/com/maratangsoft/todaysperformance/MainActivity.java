package com.maratangsoft.todaysperformance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static String apiKey = "e70fc33a739e44e5a120156618b9ef59";

    BottomNavigationView bnv;
    FragmentManager manager;
    Fragment[] fragments = new Fragment[4];

    final static String[] CATEGORIES = {"", "AAAA", "AAAB", "BBBA", "CCCA", "CCCB", "CCCC", "EEEA"}; //전체, 연극, 뮤지컬, 무용, 클래식, 오페라, 국악, 복합

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragments[0] = new SearchFragment();
        manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.fragment_container, fragments[0]).commit();

        bnv = findViewById(R.id.bottom_navi);
        bnv.setOnItemSelectedListener(item -> bottomNaviSelect(item));


    }

    boolean bottomNaviSelect(MenuItem item){

        FragmentTransaction transaction = manager.beginTransaction();
        if (fragments[0] != null) transaction.hide(fragments[0]);
        if (fragments[1] != null) transaction.hide(fragments[1]);
        if (fragments[2] != null) transaction.hide(fragments[2]);
        if (fragments[3] != null) transaction.hide(fragments[3]);

        switch (item.getItemId()){
            case R.id.bnv_search:
                transaction.show(fragments[0]);
                break;

            case R.id.bnv_ranking:
                if (fragments[1] == null){
                    fragments[1] = new RankingFragment();
                    transaction.add(R.id.fragment_container, fragments[1]);
                }
                transaction.show(fragments[1]);
                break;
            case R.id.bnv_favorite:
                if (fragments[2] == null){
                    fragments[2] = new FavoriteFragment();
                    transaction.add(R.id.fragment_container, fragments[2]);
                }
                transaction.show(fragments[2]);
                break;
            case R.id.bnv_setting:
                if (fragments[3] == null){
                    fragments[3] = new SettingFragment();
                    transaction.add(R.id.fragment_container, fragments[3]);
                }
                transaction.show(fragments[3]);
                break;
        }
        transaction.commit();
        return true;
    }
}