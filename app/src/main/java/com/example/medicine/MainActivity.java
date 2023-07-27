package com.example.medicine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;



public class MainActivity extends AppCompatActivity {
    /*기타 전역 변수 선언부 */
    public Integer medicine_num;
    public String medicine_type;

    /* 컴포넌트 변수 선언부 */
    public CalendarView calendar_calendarView1;
    public Button delete_button2, save_button3, ActivityChange_button4, today_button7;
    public TextView selectedDate_textView1, image_textView3;
    public Spinner medicine_select_spinner1, medicine_num_spinner2;
    public ListView content_listView1;

    public DBhelper myDBhelper;
    public SQLiteDatabase db;
    public String curr_selected_date;
    public Object listView_selected_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*컴포넌트 변수화(findViewById) 파트*/
        {
            calendar_calendarView1 = (CalendarView) findViewById(R.id.calendarView1); // 캘린더
            medicine_select_spinner1 = (Spinner) findViewById(R.id.spinner1); // 약 종류 콤보박스
            medicine_num_spinner2 = (Spinner) findViewById((R.id.spinner2)); // 약 개수 콤보박스
            selectedDate_textView1 = (TextView) findViewById(R.id.textView1); // 캘린더 뷰에서 가져온 날짜
            image_textView3 = (TextView) findViewById(R.id.textView3); // '개' 표시
            save_button3 = (Button) findViewById(R.id.button3); // 저장 버튼
            delete_button2 = (Button) findViewById(R.id.button2); // 삭제 버튼
            ActivityChange_button4 = (Button) findViewById(R.id.button4); //창 전환 버튼
            content_listView1 = (ListView) findViewById(R.id.listView1); // db에서 가져온 값
            today_button7 = (Button) findViewById(R.id.button7); // 오늘 가리키는 버튼
        }
        /*스피너(콤보 박스) 세팅*/
        {
            //spinner1
            final String[] medicine = {"-항목 선택", "나라믹","레이보우", "멜라킹","펜잘", "타이레놀","크래밍", "조믹", "엠겔","데파스"};
            ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, medicine);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            medicine_select_spinner1.setAdapter(adapter1);

            //spinner2
            final Integer[] NumberOfMedicine = {0, 1, 2, 3, 4, 5};
            ArrayAdapter adapter2 = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, NumberOfMedicine);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            medicine_num_spinner2.setAdapter(adapter2);
        }

        /*초기 세팅*/
        selectedDate_textView1.setText(getCurrentDate()); // textView1에 현재 날짜를 (yyyy/mm/dd) 형식으로 표시
        curr_selected_date = getCurrentDate();

        /*db 구축*/
        myDBhelper = new DBhelper(MainActivity.this, DBhelper.DATABASE_NAME, null, DBhelper.DATABASE_VERSION);
        db = myDBhelper.getWritableDatabase();
        myDBhelper.onCreate(db);
        /*초기 세팅2*/
        search_db(getCurrentDate());


        /*캘린더 날짜 변경하면*/
        calendar_calendarView1.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                curr_selected_date = String.format("%d/%02d/%02d", year, month + 1, dayOfMonth);
                selectedDate_textView1.setText(curr_selected_date); //달력 선택 날짜 표시
                medicine_select_spinner1.setVisibility(View.VISIBLE); //약 종류 선택 콤보박스
                medicine_select_spinner1.setSelection(0); //약 종류 콤보박스 맨 윗 항목으로 초기화
                medicine_num_spinner2.setSelection(0); //약 종류 콤보박스 맨 윗 항목으로 초기화
                selectedDate_textView1.setVisibility(View.VISIBLE); // 선택한 날짜 표시

                search_db(curr_selected_date);

                delete_button2.setVisibility(View.INVISIBLE);
                save_button3.setVisibility(View.INVISIBLE);
            }
        });
        /*약 종류 선택*/
        medicine_select_spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                medicine_num_spinner2.setSelection(0);
                image_textView3.setVisibility(View.VISIBLE);
                delete_button2.setVisibility(View.INVISIBLE);
                if (medicine_select_spinner1.getSelectedItemId()==5){
                    medicine_num_spinner2.setSelection(2);
                    save_button3.setVisibility(View.VISIBLE);
                }
                else if(medicine_select_spinner1.getSelectedItemId()!=0) {
                    medicine_num_spinner2.setSelection(1);
                    save_button3.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        /*약 개수 선택*/
        medicine_num_spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                long spinner1_item_id = medicine_select_spinner1.getSelectedItemId();
                long spinner2_item_id = medicine_num_spinner2.getSelectedItemId();
                if (spinner1_item_id != 0 && spinner2_item_id != 0){ //둘다 0이면 저장버튼 생성
                    save_button3.setVisibility(View.VISIBLE);
                }
                else if (spinner1_item_id == 0 && spinner2_item_id != 0){
                    save_button3.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),"약 종류를 선택하세요", Toast.LENGTH_LONG).show();
                }
                else if (spinner1_item_id != 0 && spinner2_item_id == 0){
                    save_button3.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),"약 개수를 선택하세요", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        /*db에 저장*/
        save_button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (medicine_select_spinner1.getSelectedItemId() != 0) {
                    String date1 = selectedDate_textView1.getText().toString();
                    medicine_type = medicine_select_spinner1.getSelectedItem().toString();
                    medicine_num = (Integer) medicine_num_spinner2.getSelectedItem();
                    saveData(date1, medicine_type, medicine_num);

                    search_db(curr_selected_date);
                    save_button3.setVisibility(View.INVISIBLE);
                    content_listView1.setVisibility(View.VISIBLE);
                    medicine_select_spinner1.setSelection(0);
                    medicine_num_spinner2.setSelection(0);
                    Toast.makeText(getApplicationContext(), "저장되었습니다!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "약 종류를 선택하세요!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*창 전환*/
        ActivityChange_button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), StatusActivity.class);
                startActivity(intent);
            }
        });
        /*ListView item 클릭*/
        content_listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                delete_button2.setVisibility(View.VISIBLE);
                listView_selected_item = (Object)adapterView.getAdapter().getItem(i);  //리스트뷰의 포지션 내용을 가져옴.
                content_listView1.setSelector(new PaintDrawable(0x75673AB7));
            }
        });
        /*데이터베이스에서 삭제*/
        delete_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = listView_selected_item.toString();
                String medi_type = str.substring(0, str.indexOf(" "));
                String medi_num = str.substring(str.length()-2, str.length()-1);
                removeData(medi_type, medi_num);
                search_db(curr_selected_date);
                Toast.makeText(getApplicationContext(),"삭제되었습니다!", Toast.LENGTH_LONG).show();
                delete_button2.setVisibility(View.INVISIBLE);
            }
        });
        /*캘린더 뷰 오늘 날짜료 표시*/
        today_button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strDate = getCurrentDate();			// dateData.getMeetingDate() 는 제가 가지고 있는 Date형 값입니다.
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date currentDay = dateFormat.parse(strDate, new ParsePosition(0));
                Long currentLong = currentDay.getTime();

                calendar_calendarView1.setDate(currentLong);
            }
        });
    }

    //////////////////////////////// 기타 function ////////////////////////////////
    //데이터베이스에서 불러오기
    public void search_db(String date) {
        String sql = "select * from " + TableInfo.TABLE_NAME + " where "+TableInfo.COLUMN_NAME_DATE+" = '" + date + "'";
        Cursor c = db.rawQuery(sql, null);
        String[] medicine_typeNnum = {};

        if (c.moveToFirst()){
            do{
                String col1 = c.getString(1);
                int col3 = c.getInt(3);
                String newString = col1+" "+col3+"개";
                medicine_typeNnum = ArrayAdd(medicine_typeNnum, newString);
            }while(c.moveToNext());
        }
        c.close();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, medicine_typeNnum);

        content_listView1.setAdapter(arrayAdapter);
    }
    public void removeData(String medicine_type, String medicine_num) {
        String sql1 = "delete from "+TableInfo.TABLE_NAME+" where "+TableInfo.COLUMN_NAME_DATE+" = "+"'"+curr_selected_date+"'"+" and "+TableInfo.COLUMN_NAME_TYPE+" = "+"'"+medicine_type+"'"+" and "+TableInfo.COLUMN_NAME_NUM+" = "+medicine_num+";";
        db.execSQL(sql1);
    }
    public void saveData(String date, String type, int num) {
        ContentValues values = new ContentValues();
        values.put(TableInfo.COLUMN_NAME_TYPE, type);
        values.put(TableInfo.COLUMN_NAME_DATE, date);
        values.put(TableInfo.COLUMN_NAME_NUM, num);

        db.insert(TableInfo.TABLE_NAME, null, values);

    }
    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+09:00"), Locale.KOREA);
        return String.format("%d/%02d/%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE));
    }
    public static String[] ArrayAdd(String[] originArray, String Val) {
        // 순서 1. 배열을 List로 변환
        List<String> newList = new ArrayList<>(Arrays.asList(originArray));

        // 순서 2. List의 Add() 메서드를 호출하여 새로운 값을 할당
        newList.add(Val);

        // 순서 3. List를 배열을 변환 후 반환
        return newList.toArray(new String[0]);
    }
}
