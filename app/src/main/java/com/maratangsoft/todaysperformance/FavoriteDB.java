package com.maratangsoft.todaysperformance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class FavoriteDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favorites.db";
    public static final int DATABASE_VERSION = 1;

    final String TABLE_NAME = "favorites";
    final String COLUMN_MT20ID = "mt20id";
    final String COLUMN_PRFNM = "prfnm";
    final String COLUMN_GENRENM = "genrenm";
    final String COLUMN_PRFPDFROM = "prfpdfrom";
    final String COLUMN_PRFPDTO = "prfpdto";
    final String COLUMN_POSTER = "poster";
    final String COLUMN_FCLTYNM = "fcltynm";

    Context context;

    public FavoriteDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_NAME
                + "(" + COLUMN_MT20ID + " TEXT PRIMARY KEY, "
                + COLUMN_PRFNM + " TEXT, "
                + COLUMN_GENRENM + " TEXT, "
                + COLUMN_PRFPDFROM + " TEXT, "
                + COLUMN_PRFPDTO + " TEXT, "
                + COLUMN_POSTER + " TEXT, "
                + COLUMN_FCLTYNM + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //DB -> FavoriteFragment 즐겨찾기 가져오기
    public Cursor readAllData(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        String query = "SELECT * FROM " + TABLE_NAME;
        if(db != null) cursor = db.rawQuery(query, null);

        return cursor;
    }

    //즐겨찾기 등록돼 있는지 확인하는 메소드
    public boolean isFavorited(String mt20id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE mt20id = '"+mt20id+"'";
        if(db != null) cursor = db.rawQuery(query, null);

        return (cursor.getCount() > 0);
    }

    //SearchFragment -> DB 즐겨찾기 등록
    public void insertFavorite(String mt20id, String prfnm, String genrenm, String prfpdfrom, String prfpdto, String poster, String fcltynm){
        SQLiteDatabase db = this.getWritableDatabase();

        //ContentValues 객체의 put 메소드로 데이터 삽입
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MT20ID, mt20id);
        cv.put(COLUMN_PRFNM, prfnm);
        cv.put(COLUMN_GENRENM, genrenm);
        cv.put(COLUMN_PRFPDFROM, prfpdfrom);
        cv.put(COLUMN_PRFPDTO, prfpdto);
        cv.put(COLUMN_POSTER, poster);
        cv.put(COLUMN_FCLTYNM, fcltynm);

        db.insert(TABLE_NAME, null, cv);
        //if (result == -1) Toast.makeText(context, "등록 실패", Toast.LENGTH_SHORT).show();
        //else Toast.makeText(context, "등록 성공", Toast.LENGTH_SHORT).show();
    }

    //DB에서 삭제
    void deleteFavorite(String mt20id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, "mt20id=?", new String[]{mt20id});
        //if(result == -1) Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show();
        //else Toast.makeText(context, "삭제 성공", Toast.LENGTH_SHORT).show();
    }
}
