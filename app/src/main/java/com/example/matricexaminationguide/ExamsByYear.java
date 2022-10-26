package com.example.matricexaminationguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ExamsByYear extends AppCompatActivity {
    String subject;
    ExamDatabaseHelper databaseHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_by_year);
        Intent intent=getIntent();
        subject=intent.getStringExtra("subject");
        //System.out.println("selcted subject"+subject);
        databaseHelper=new ExamDatabaseHelper(this);
        SQLiteDatabase db=databaseHelper.getWritableDatabase();
        Cursor cursor=databaseHelper.select(db,
                "Info",
                new String[]{"year"},
                "subject=?",
                new String[]{subject},
                null);
        LinearLayout linear=(LinearLayout) findViewById(R.id.linearlayout);
        Button[] btns= new Button[cursor.getCount()];
        int i=0;
        while (cursor.moveToNext()){
            btns[i]=new Button(this);
            btns[i].setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            btns[i].setText(cursor.getString(0));
            btns[i].setOnClickListener(this::displayExam);
            linear.addView(btns[i]);
            i++;
        }
    }
    public void displayExam(View v){
        Button btn=(Button)v;
        String year=btn.getText().toString();
        Intent i=new Intent(ExamsByYear.this,DisplayExam.class);
        i.putExtra("subject",subject);
        i.putExtra("year",year);
        startActivity(i);
    }
}