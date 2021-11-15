package com.project.namephonelistview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class NamePhoneMyDB {
    public SQLiteDatabase db = null; //資料庫類別
    private final static String DATABASE_NAME = "db1.db"; //資料庫名稱
    private final static String TABLE_NAME = "table01"; //資料表名稱
    private final static String _ID = "_id"; //資料表欄位
    private final static String NAME = "name";
    private final static String PHONE = "phone";
    /* 建立資料表的欄位 */
    private final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + _ID
            + " INTEGER PRIMARY KEY," + NAME + " TEXT," + PHONE + " TEXT)";

    private Context mCtx = null;

    public NamePhoneMyDB(Context ctx) { //建構式
        this.mCtx = ctx;  //傳入建立物件的MainActivity
    }

    public void open() throws SQLException { //開啟已經存在的資料庫
        db = mCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);
        try {
            db.execSQL(CREATE_TABLE); //建立資料表
        } catch (Exception e) {
        }
    }

    public void close() {  //關閉資料庫
        db.close();
    }

//	public Cursor getAll() { //查詢所有資料，取出所有的欄位
//	    return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
//	}

    public Cursor getAll() { //查詢所有資料，只取出三個欄位
        return db.query(TABLE_NAME,
                new String[]{_ID, NAME, PHONE},
                null, null, null, null, null, null);
    }

    public Cursor get(long rowId) throws SQLException { //查詢指定 ID 的資料，只取出三個欄位
        Cursor mCursor = db.query(TABLE_NAME,
                new String[]{_ID, NAME, PHONE},
                _ID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public long append(String name, String phone) { //新增一筆資料
        ContentValues args = new ContentValues();
        args.put(NAME, name);
        args.put(PHONE, phone);
        return db.insert(TABLE_NAME, null, args);
    }

    public boolean delete(long rowId) {  //刪除指定的資料
        return db.delete(TABLE_NAME, _ID + "=" + rowId, null) > 0;
    }

    public boolean update(long rowId, String name, String phone) { //更新指定的資料
        ContentValues args = new ContentValues();
        args.put(NAME, name);
        args.put(PHONE, phone);
        return db.update(TABLE_NAME, args, _ID + "=" + rowId, null) > 0;
    }
}
