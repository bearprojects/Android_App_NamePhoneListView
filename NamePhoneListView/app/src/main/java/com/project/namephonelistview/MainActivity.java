package com.project.namephonelistview;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //自建的資料庫類別
    private NamePhoneMyDB db = null;

    //資料表欄位
    //private final static String _ID = "_id";
    //private final static String NAME = "name";
    //private final static String PHONE = "phone";

    Button btnAppend, btnEdit, btnDelete, btnClear;
    EditText edtName, edtPhone;
    ListView listview01;
    Cursor cursor;
    long myid; //儲存_id 的值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("NamePhoneListView");

        // 取得元件
        edtName = (EditText)findViewById(R.id.edtName);
        edtPhone = (EditText)findViewById(R.id.edtPhone);
        listview01 = (ListView)findViewById(R.id.ListView01);
        btnAppend = (Button)findViewById(R.id.btnAppend);
        btnEdit = (Button)findViewById(R.id.btnEdit);
        btnDelete = (Button)findViewById(R.id.btnDelete);
        btnClear = (Button)findViewById(R.id.btnClear);

        btnAppend.setOnClickListener(myListener);
        btnEdit.setOnClickListener(myListener);
        btnDelete.setOnClickListener(myListener);
        btnClear.setOnClickListener(myListener);
        listview01.setOnItemClickListener(listview01Listener);

        db = new NamePhoneMyDB(this); //建立MyDB物件
        db.open();
        cursor = db.getAll(); //載入全部資料
        UpdateAdapter(cursor); //載入資料表至ListView中
    }

    private ListView.OnItemClickListener listview01Listener =
            new ListView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    ShowData(id);
                    cursor.moveToPosition(position);
                }
            };

    private void ShowData(long id){ //顯示單筆資料
        Cursor c = db.get(id);
        myid = id;  //取得_id 欄位
        edtName.setText(c.getString(1)); //name 欄位
        edtPhone.setText("" + c.getString(2)); //phone 欄位
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close(); // 關閉資料庫
    }

    private Button.OnClickListener myListener = new Button.OnClickListener(){
        public void onClick(View v){
            try{
                switch (v.getId()){
                    case R.id.btnAppend:{ //新增
                        String phone = (edtPhone.getText().toString());
                        String name = edtName.getText().toString();
                        if (db.append(name,phone)>0){
                            cursor = db.getAll(); //載入全部資料
                            UpdateAdapter(cursor);  //載入資料表至ListView中
                            ClearEdit();
                        }
                        edtName.requestFocusFromTouch();
                        break;
                    }

                    case R.id.btnEdit: {  //修改
                        String phone = (edtPhone.getText().toString());
                        String name = edtName.getText().toString();
                        if (db.update(myid,name,phone)){
                            cursor = db.getAll(); //載入全部資料
                            UpdateAdapter(cursor);  //載入資料表至 ListView 中
                        }
                        break;
                    }

                    case R.id.btnDelete: { //刪除
                        if (cursor != null && cursor.getCount() >= 0){
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("刪除資料");
                            builder.setMessage("確定要刪除" + edtName.getText() + "這筆資料?");
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int i) {
                                }
                            });
                            builder.setPositiveButton("確定",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int i) {
                                    Toast toast = Toast.makeText(MainActivity.this,"資料已刪除",Toast.LENGTH_SHORT);
                                    toast.show();

                                    if (db.delete(myid)){
                                        cursor = db.getAll(); //載入全部資料
                                        UpdateAdapter(cursor); //載入資料表至ListView中
                                        ClearEdit();
                                    }
                                }
                            });
                            builder.show();
                        }
                        break;
                    }

                    case R.id.btnClear: { //清除
                        ClearEdit();
                        break;
                    }
                }
            }catch (Exception err){
                Toast.makeText(getApplicationContext(), "資料不正確!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void ClearEdit(){
        edtName.setText("");
        edtPhone.setText("");
    }

    public void UpdateAdapter(Cursor cursor){
        if (cursor != null && cursor.getCount() >= 0){
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    R.layout.namephone_mylayout, //包含兩個資料項
                    cursor, //資料庫的Cursors物件
                    new String[] { "name", "phone" }, //name、phone 欄位
                    new int[] { R.id.txtName, R.id.txtPhone},
                    0);
            listview01.setAdapter(adapter); // 將adapter增加到listview01中
        }
    }
}
