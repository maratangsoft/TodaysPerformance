package com.maratangsoft.todaysperformance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maratangsoft.todaysperformance.databinding.ActivityDetailBinding;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    Intent intent;
    String mt20id, mt10id, poster, prfnm, genrenm, fcltynm, prfpdfrom, prfpdto, prfcast, prfcrew, prfruntime, prfage, entrpsnm, pcseguidance, dtguidance, sty, prfstate, openrun;
    String[] styurls = new String[4];
    ActivityDetailBinding binding;
    FavoriteDB favoriteDB = new FavoriteDB(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        mt20id = intent.getStringExtra("mt20id");
        loadFromAPI();

        binding.ivStyurl0.setOnClickListener(showPhotoview);
        binding.ivStyurl1.setOnClickListener(showPhotoview);
        binding.ivStyurl2.setOnClickListener(showPhotoview);
        binding.ivStyurl3.setOnClickListener(showPhotoview);
    }

    View.OnClickListener showPhotoview = view -> {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_photoviewer);
        AlertDialog dialog = builder.create();
        dialog.show();

        ImageView btnDialogClose = dialog.findViewById(R.id.dialog_btn_close);
        ImageView pvDialogViewer = dialog.findViewById(R.id.dialog_photoview);

        String photoViewUrl = null;
        switch (view.getId()){
            case R.id.iv_styurl0:
                photoViewUrl = styurls[0];
                break;
            case R.id.iv_styurl1:
                photoViewUrl = styurls[1];
                break;
            case R.id.iv_styurl2:
                photoViewUrl = styurls[2];
                break;
            case R.id.iv_styurl3:
                photoViewUrl = styurls[3];
                break;
        }
        Glide.with(DetailActivity.this).load(photoViewUrl).error(R.drawable.error).into(pvDialogViewer);

        btnDialogClose.setOnClickListener(v->{
            dialog.dismiss(); //TODO 버튼눌러서 다이얼로그 끄기
        });
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_detail_option, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            //뒤로가기 누르면 액티비티 종료
            case android.R.id.home:
                finish();
                break;
            //북마크 누르면 북마크 추가/삭제
            case R.id.option_favorite:
                if (favoriteDB.isFavorited(mt20id)){
                    favoriteDB.deleteFavorite(mt20id);
                    Toast.makeText(this, "북마크를 취소합니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    favoriteDB.insertFavorite(mt20id, prfnm, genrenm, prfpdfrom, prfpdto, poster, fcltynm);
                    Toast.makeText(this, "해당 공연을 북마크합니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            //TODO: 티켓팅 누르면 예매 페이지로 이동
            case R.id.option_ticket:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void loadFromAPI() {
        new Thread(){
            @Override
            public void run() {
                StringBuffer buffer = new StringBuffer();
                String address = buffer.append("http://www.kopis.or.kr/openApi/restful/pblprfr/")
                        .append(mt20id)
                        .append("?service=").append(MainActivity.apiKey).toString();

                try {
                    URL url = new URL(address);
                    InputStreamReader reader = new InputStreamReader(url.openStream());
                    XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                    parser.setInput(reader);

                    int styurlCount = 0;

                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT){

                        if (eventType == XmlPullParser.START_TAG) {
                            String tagName = parser.getName();
                            if (tagName.equals("prfnm")) {
                                parser.next();
                                prfnm = parser.getText();
                            } else if (tagName.equals("prfpdfrom")) {
                                parser.next();
                                prfpdfrom = parser.getText();
                            } else if (tagName.equals("prfpdto")) {
                                parser.next();
                                prfpdto = parser.getText();
                            } else if (tagName.equals("fcltynm")) {
                                parser.next();
                                fcltynm = parser.getText();
                            } else if (tagName.equals("prfcast")) {
                                parser.next();
                                prfcast = parser.getText();
                            } else if (tagName.equals("prfcrew")) {
                                parser.next();
                                prfcrew = parser.getText();
                            } else if (tagName.equals("prfruntime")) {
                                parser.next();
                                prfruntime = parser.getText();
                            } else if (tagName.equals("prfage")) {
                                parser.next();
                                prfage = parser.getText();
                            } else if (tagName.equals("entrpsnm")) {
                                parser.next();
                                entrpsnm = parser.getText();
                            } else if (tagName.equals("pcseguidance")) {
                                parser.next();
                                pcseguidance = parser.getText();
                            } else if (tagName.equals("poster")) {
                                parser.next();
                                poster = parser.getText();
                            } else if (tagName.equals("sty")) {
                                parser.next();
                                sty = parser.getText();
                            } else if (tagName.equals("genrenm")) {
                                parser.next();
                                genrenm = parser.getText();
                            } else if (tagName.equals("prfstate")) {
                                parser.next();
                                prfstate = parser.getText();
                            } else if (tagName.equals("openrun")) {
                                parser.next();
                                openrun = parser.getText();
                            } else if (tagName.equals("styurl")) {
                                parser.next();
                                if (styurlCount < styurls.length){
                                    styurls[styurlCount] = parser.getText();
                                    styurlCount++;
                                }
                            } else if (tagName.equals("mt10id")) {
                                parser.next();
                                mt10id = parser.getText();
                            }else if (tagName.equals("dtguidance")) {
                                parser.next();
                                dtguidance = parser.getText();
                            }
                        }
                        eventType = parser.next();
                    }
                    runOnUiThread(()-> {
                        binding.tvPrfnm.setText(prfnm);
                        binding.tvGenrenm.setText(genrenm);
                        binding.tvFcltynm.setText(fcltynm);
                        binding.tvPrfpdfrom.setText("공연 시작일: "+prfpdfrom);
                        binding.tvPrfpdto.setText("공연 종료일: "+prfpdto);
                        binding.tvPrfcast.setText("출연진: "+prfcast);
                        binding.tvPrfcrew.setText("제작진: "+prfcrew);
                        binding.tvPrfruntime.setText("런타임: "+prfruntime);
                        binding.tvPrfage.setText("관람 연령: "+prfage);
                        binding.tvEntrpsnm.setText("제작사: "+entrpsnm);
                        binding.tvPcseguidance.setText("티켓 가격: "+pcseguidance);
                        binding.tvDtguidance.setText("공연시간: "+dtguidance);
                        binding.tvSty.setText(sty);
                        Glide.with(DetailActivity.this).load(poster).error(R.drawable.error).into(binding.ivPoster);
                        Glide.with(DetailActivity.this).load(styurls[0]).into(binding.ivStyurl0);
                        Glide.with(DetailActivity.this).load(styurls[1]).into(binding.ivStyurl1);
                        Glide.with(DetailActivity.this).load(styurls[2]).into(binding.ivStyurl2);
                        Glide.with(DetailActivity.this).load(styurls[3]).into(binding.ivStyurl3);
                    });

                } catch (IOException | XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}