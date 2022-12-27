package com.maratangsoft.todaysperformance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.maratangsoft.todaysperformance.databinding.FragmentSearchBinding;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SearchFragment extends Fragment {

    FragmentSearchBinding binding;
    ArrayList<RecyclerViewItem> itemList = new ArrayList<>(11);
    SearchFragmentAdapter adapter;

    int rows = 10;
    int page = 1;
    boolean isLoading = false;
    String category = "";

    String searchPeriodStart;
    String searchPeriodEnd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_search_option, menu);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ((MainActivity) getActivity()).setSupportActionBar(binding.toolbar);
        setHasOptionsMenu(true); //프래그먼트에 옵션메뉴 달린 툴바 쓸때 꼭 써주기!!

        Date date = new Date();
        date.setTime(date.getTime() - 1000*60*60*24*365);
        searchPeriodStart = new SimpleDateFormat("yyyyMMdd").format(date);
        date.setTime(date.getTime() + 1000*60*60*24*365);
        searchPeriodEnd = new SimpleDateFormat("yyyyMMdd").format(date);

        loadFromAPI();

        adapter = new SearchFragmentAdapter(getActivity(), itemList);
        binding.recycler.setAdapter(adapter);

        binding.tabLayout.addOnTabSelectedListener(onTabSelectedListener);
        binding.recycler.addOnScrollListener(onScrollListener);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////

    TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int position = tab.getPosition();
            category = MainActivity.CATEGORIES[position];
            itemList.clear();
            adapter.notifyDataSetChanged();
            page = 1;
            loadFromAPI();
        }
        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }
        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int displayedItemNum = layoutManager.getItemCount() - 1;
            int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();

            if (!isLoading && lastVisible == displayedItemNum) {
                page++;
                loadFromAPI();
            }
        }
    };

    void loadFromAPI() {
        new Thread(){
            @Override
            public void run() {
                getActivity().runOnUiThread(()->{
                    isLoading = true;
                    binding.itemProgressbar.setVisibility(View.VISIBLE);
                });

                StringBuffer buffer = new StringBuffer();
                String address = buffer.append("http://www.kopis.or.kr/openApi/restful/pblprfr")
                        .append("?service=").append(MainActivity.apiKey)
                        .append("&stdate=").append(searchPeriodStart)
                        .append("&eddate=").append(searchPeriodEnd)
                        .append("&cpage=").append(page)
                        .append("&rows=").append(rows)
                        .append("&prfstate=02")
                        .append("&shcate=").append(category)
                        .toString();

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
                                if (tagName.equals("db")){
                                    item = new RecyclerViewItem();
                                }else if(tagName.equals("mt20id")){
                                    parser.next();
                                    item.mt20id = parser.getText();
                                }else if(tagName.equals("prfnm")){
                                    parser.next();
                                    item.prfnm = parser.getText();
                                }else if(tagName.equals("prfpdfrom")){
                                    parser.next();
                                    item.prfpdfrom = parser.getText();
                                }else if(tagName.equals("prfpdto")){
                                    parser.next();
                                    item.prfpdto = parser.getText();
                                }else if(tagName.equals("fcltynm")){
                                    parser.next();
                                    item.fcltynm = parser.getText();
                                }else if(tagName.equals("poster")){
                                    parser.next();
                                    item.poster = parser.getText();
                                }else if(tagName.equals("genrenm")){
                                    parser.next();
                                    item.genrenm = parser.getText();
                                }else if(tagName.equals("prfstate")){
                                    parser.next();
                                    item.prfstate = parser.getText();
                                }
                                break;

                            case XmlPullParser.END_TAG:
                                if(parser.getName().equals("db")) itemList.add(item);
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
