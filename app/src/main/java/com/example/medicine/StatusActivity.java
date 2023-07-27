package com.example.medicine;

import android.database.Cursor;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class StatusActivity extends MainActivity {

    /*전역 변수*/
    public String injection_date = "2000/01/01";

    /*컴포넌트 선언부*/
    public ListView dbshow_listView2;
    public TextView status_report_textView4;
    public Button show_less_button5, show_more_button6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        /*컴포넌트 변수화(findViewById) 파트*/
        dbshow_listView2 = (ListView) findViewById(R.id.listView2);
        status_report_textView4 = (TextView) findViewById(R.id.textView4);
        show_less_button5 = (Button) findViewById(R.id.button5);
        show_more_button6 = (Button) findViewById(R.id.button6);

        /*초기 세팅*/
        search_db_Status(getCurrentDate());
        dbshow_listView2.setAdapter(search_db_Status(injection_date));


        /*일부 보기 (주사 맞은 날 이후 복용한 약 종류 표시)*/
        show_less_button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sql = "select * from " + TableInfo.TABLE_NAME + " where " + TableInfo.COLUMN_NAME_DATE + " >= " +"'"+ injection_date +"'"+ " order by " + TableInfo.COLUMN_NAME_DATE + " desc;";
                Cursor c = db.rawQuery(sql, null);   // +"'"+
                String[] medicine_typeNnum = {};
                if (c.moveToFirst()){
                    do{
                        String col2 = c.getString(2);
                        String col1 = c.getString(1);
                        int col3 = c.getInt(3);
                        String newString = col2+" "+col1+" "+col3+"개";
                        medicine_typeNnum = ArrayAdd(medicine_typeNnum, newString);

                    }while(c.moveToNext());
                }
                c.close();
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, medicine_typeNnum);
                dbshow_listView2.setAdapter(arrayAdapter);

            }
        });
        /*데이터 전체 보기 (복용한 약 전체 표시)*/
        show_more_button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbshow_listView2.setAdapter(search_db_Status("2000/01/01"));

            }
        });
        /*리스트 뷰 클릭시*/
        dbshow_listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dbshow_listView2.setSelector(new PaintDrawable(0x75673AB7));
            }
        });
    }



    public ArrayAdapter<String> search_db_Status (String date) {
        String sql = "select * from " + TableInfo.TABLE_NAME + " order by " + TableInfo.COLUMN_NAME_DATE + " desc;";
        Cursor c = db.rawQuery(sql, null);
        String[] medicine_typeNnum = {};

        //final String[] medicine = {"-항목 선택", "나라믹","레이보우","멜라킹", "펜잘", "타이레놀","크래밍", "조믹", "엠겔","데파스"};
        int naramic_after_injection=0;
        int raybow_after_injection=0;
        int melaking_after_injection=0;
        int penzal_after_injection=0;
        int tairenol_after_injection=0;
        int craming_after_injection=0;
        int jomic_after_injection=0;
        int depas_after_injection=0;
        if (c.moveToFirst()){
            do{
                if (c.getString(1).equals("엠겔")){
                    injection_date = c.getString(2);
                    break;
                }
            }while(c.moveToNext());
        }
        if (c.moveToFirst()){
            do{
                String col2 = c.getString(2);
                String col1 = c.getString(1);
                int col3 = c.getInt(3);
                if (col2.compareTo(date) >= 0) {
                    String newString = col2 + " " + col1 + " " + col3 + "개";
                    medicine_typeNnum = ArrayAdd(medicine_typeNnum, newString);

                    if (col2.compareTo(injection_date) >= 0) {
                        if (col1.equals("나라믹")) naramic_after_injection += col3;
                        else if (col1.equals("레이보우")) raybow_after_injection += col3;
                        else if (col1.equals("멜라킹")) melaking_after_injection += col3;
                        else if (col1.equals("펜잘")) penzal_after_injection += col3;
                        else if (col1.equals("타이레놀")) tairenol_after_injection += col3;
                        else if (col1.equals("크래밍")) craming_after_injection += col3;
                        else if (col1.equals("조믹")) jomic_after_injection += col3;
                        else if (col1.equals("데파스")) depas_after_injection += col3;
                    }
                }
            }while(c.moveToNext());
        }
        c.close();
        status_report_textView4.setText("");
        String print_out = injection_date+" 주사 이후\n"
                +"\n나라믹 : "+naramic_after_injection
                +"\n레이보우 : "+raybow_after_injection
                +"\n멜라킹 : "+melaking_after_injection
                +"\n펜잘: "+penzal_after_injection
                +"\n타이레놀 : "+tairenol_after_injection
                +"\n크래밍 : "+craming_after_injection
                +"\n조믹 : "+jomic_after_injection
                +"\n데파스 : "+depas_after_injection
                ;

        status_report_textView4.setText(print_out);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, medicine_typeNnum);

        return arrayAdapter;
    }

}
