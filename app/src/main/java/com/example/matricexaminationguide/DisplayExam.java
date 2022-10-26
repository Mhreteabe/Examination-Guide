package com.example.matricexaminationguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class DisplayExam extends AppCompatActivity {
     String subject;
     String year;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_exam);
        Intent intent=getIntent();
        subject=intent.getStringExtra("subject");
        year=intent.getStringExtra("year");
        //System.out.println("in displayexam subject is "+subject);
        LinearLayout linear=(LinearLayout) findViewById(R.id.linear_layout);
        ExamDatabaseHelper databaseHelper=new ExamDatabaseHelper(this);
        SQLiteDatabase db=databaseHelper.getWritableDatabase();
        addAnswer(databaseHelper,db);
        Cursor cursor=databaseHelper.select(db,
                "Info2",
                new String[]{"num_questions"},
                "subject=?",
                new String[]{subject},
                null);
        //System.out.println("length of select num questions "+cursor.getCount());
        cursor.moveToNext();
        int num_questions=cursor.getInt(0);
        ImageView[] questions=new ImageView[num_questions];
        RadioGroup[] answers= new RadioGroup[num_questions];
        RadioButton[] choices=new RadioButton[num_questions*4];
        for(int i=0;i<num_questions;i++){
            addAnswers(answers,choices,i);
            questions[i]=new ImageView(this);
            questions[i].setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            answers[i].setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            String mDrawableName = subject.toLowerCase().substring(0,4)+"_"+year+"_"+(i+1);
            //System.out.println("the image name is "+mDrawableName);
            int resID = getResources().getIdentifier(mDrawableName , "drawable", getPackageName());
            //System.out.println("the resource id of the image is "+resID);
            questions[i].setImageResource(resID);
            linear.addView(questions[i]);
            linear.addView(answers[i]);
        }


    }

    public void addAnswer(ExamDatabaseHelper helper,SQLiteDatabase db) {
        String math_2014 = "maths_2014.txt";
        String[] filenames = new String[]{
                math_2014
        };

        for (String path : filenames) {
            //System.out.println(path);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(
                        new InputStreamReader(getAssets().open(path)));
                String header[]=reader.readLine().split(",");
                String subject=header[0];
                String year=header[1];
                //System.out.println("inside of add answer for"+year+subject);
                String []cols=reader.readLine().split(",");//this is done to pass the cols
                String line;
                while ((line=reader.readLine())!=null) {
                    String[]data=line.split(",");
                    ContentValues values = new ContentValues();
                    values.put("subject",subject.trim());
                    values.put("year",year.trim());
                    for (int i=0;i<cols.length;i++){
                        //System.out.println(cols[i].trim()+"  "+data[i].trim());
                        values.put(cols[i].trim(),data[i].trim());
                    }

                    helper.insert(db,"Question",values);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void addAnswers(RadioGroup[]grp,RadioButton[]btns,int question_idx){
        grp[question_idx]=new RadioGroup(this);
        grp[question_idx].setOrientation( LinearLayout.HORIZONTAL);
        grp[question_idx].setGravity(Gravity.CENTER);
        grp[question_idx].setPadding(10,30,10,30);
        for(int i=4*question_idx;i<4*question_idx+4;i++){
            String label="";
            switch (i%4){
                case 0:label="A";break;
                case 1:label="B";break;
                case 2:label="C";break;
                case 3:label="D";break;
            }
            //System.out.println("length of buttons:"+btns.length+",i:"+i);
            btns[i]=new RadioButton(this);
            btns[i].setText(label);
            grp[question_idx].addView(btns[i]);
        }
    }

    public void showScore(View v){

    }
    public void saveAndExit(View v){

    }
}