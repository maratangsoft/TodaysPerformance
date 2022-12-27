package com.maratangsoft.todaysperformance;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.maratangsoft.todaysperformance.databinding.FragmentRankingBinding;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RankingFragment extends Fragment {

    FragmentRankingBinding binding;
    ArrayList<RecyclerViewItem> itemList = new ArrayList<>();
    RankingFragmentAdapter adapter;

    boolean isLoading = false;
    String ststype = "week";
    String category = "";
    String yesterday;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRankingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).setSupportActionBar(binding.toolbar);

        Date date = new Date();
        date.setTime(date.getTime() - 1000*60*60*24);
        yesterday = new SimpleDateFormat("yyyyMMdd").format(date);

        loadFromAPI();

        adapter = new RankingFragmentAdapter(getActivity(), itemList);
        binding.recycler.setAdapter(adapter);

        binding.tabLayout.addOnTabSelectedListener(onTabSelectedListener);
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////
    TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int position = tab.getPosition();
            category = MainActivity.CATEGORIES[position];
            itemList.clear();
            adapter.notifyDataSetChanged();
            loadFromAPI();
        }
        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }
        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    void loadFromAPI(){
        new Thread(){
            @Override
            public void run() {
                getActivity().runOnUiThread(()->{
                    isLoading = true;
                    binding.itemProgressbar.setVisibility(View.VISIBLE);
                });

                StringBuffer buffer = new StringBuffer();
                String address = buffer.append("http://kopis.or.kr/openApi/restful/boxoffice")
                        .append("?service=").append(MainActivity.apiKey)
                        .append("&ststype=").append(ststype)
                        .append("&date=").append(yesterday)
                        .append("&catecode=").append(category)
                        .toString();
                buffer.delete(0,buffer.length());

                try {
                    URL url = new URL(address);
                    InputStreamReader reader = new InputStreamReader(url.openStream());
                    XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                    parser.setInput(reader);

                    RecyclerViewItem item = null;
                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT){

                        switch (eventType){
                            case XmlPullParser.START_TAG:
                                String tagName = parser.getName();
                                if (tagName.equals("boxof")){
                                    item = new RecyclerViewItem();
                                } else if (tagName.equals("area")){
                                    parser.next();
                                    item.area = parser.getText();
                                } else if (tagName.equals("prfdtcnt")){
                                    parser.next();
                                    item.prfdtcnt = parser.getText();
                                } else if (tagName.equals("prfpd")){
                                    parser.next();
                                    String[] prfpd = parser.getText().split("~");
                                    item.prfpdfrom = prfpd[0];
                                    item.prfpdto = prfpd[1];
                                } else if (tagName.equals("cate")){
                                    parser.next();
                                    item.genrenm = parser.getText();
                                } else if (tagName.equals("prfplcnm")){
                                    parser.next();
                                    item.fcltynm = parser.getText();
                                } else if (tagName.equals("prfnm")){
                                    parser.next();
                                    item.prfnm = parser.getText();
                                } else if (tagName.equals("rnum")){
                                    parser.next();
                                    item.rnum = parser.getText();
                                } else if (tagName.equals("seatcnt")){
                                    parser.next();
                                    item.seatcnt = parser.getText();
                                } else if (tagName.equals("poster")){
                                    parser.next();
                                    item.poster = buffer.append("http://kopis.or.kr").append(parser.getText()).toString();
                                    buffer.delete(0,buffer.length());
                                } else if (tagName.equals("mt20id")){
                                    parser.next();
                                    item.mt20id = parser.getText();
                                }
                                break;

                            case XmlPullParser.END_TAG:
                                if(parser.getName().equals("boxof")) itemList.add(item);
                                break;
                        }
                        eventType = parser.next();
                    }
                    getActivity().runOnUiThread(()->{
                        adapter.notifyDataSetChanged();
                        isLoading = false;
                        binding.itemProgressbar.setVisibility(View.INVISIBLE);
                    });

                } catch (IOException | XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
