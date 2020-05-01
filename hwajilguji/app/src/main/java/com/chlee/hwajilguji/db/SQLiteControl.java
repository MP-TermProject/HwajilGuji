package com.chlee.hwajilguji.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SQLiteControl {

    SQLiteHelper helper;
    SQLiteDatabase sqlite;

    // 생성자
    public SQLiteControl(SQLiteHelper _helper){
        this.helper = _helper;
    }

    // DB Insert
    public void insert(int imageID, String image){
        sqlite = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(helper.ID, imageID);
        values.put(helper.IMAGE, image);

        sqlite.insert(helper.TABLE_NAME, null, values);
    }

    // DB Select
    public String[] select(){
        sqlite = helper.getReadableDatabase();
        // 커서 사용
        Cursor c = sqlite.query(helper.TABLE_NAME, null, null, null,null,null,null);

        // 칼럼 정보를 배열에 넣고
        String[] columnName = {helper.ID, helper.IMAGE};

        // 칼럼 정보와 길이가 같은 배열을 생성 후
        String[] returnValue = new String[columnName.length];

        // 생성한 배열에 데이터를 받아줍니다.
        while(c.moveToNext()){
            for(int i=0 ; i<returnValue.length; i++){
                returnValue[i] = c.getString(c.getColumnIndex(columnName[i]));
                Log.e("DB Select : ",i + " - "+returnValue[i]);
            }
        }
        // 커서를 사용 후에 꼭 닫아줍시다!
        c.close();
        return returnValue;
    }

    // DB Update
    public void update(String _key, String _value, String _phoneNum){
        sqlite = helper.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(_key, _value);
        // 제가 phoneNum를 사용한 이유는 포스팅하는 예제 Table의 Primary Key가 phoneNum 이기 때문입니다.
        sqlite.update(helper.TABLE_NAME, value, "phoneNum=?", new String[]{_phoneNum});
    }

    // DB Delete
    public void delete(String _phoneNum){
        sqlite = helper.getWritableDatabase();
        // 제가 phoneNum를 사용한 이유는 포스팅하는 예제 Table의 Primary Key가 phoneNum 이기 때문입니다.
        sqlite.delete(helper.TABLE_NAME, "phoneNum=?", new String[]{_phoneNum});
    }

    // SQLite Close
    public void db_close(){
        sqlite.close();
        helper.close();
    }

}
