package com.example.matricexaminationguide;

import static java.sql.DriverManager.println;

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
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class DisplayExam extends AppCompatActivity {
    String subject;
    String year;
    ExamDatabaseHelper databaseHelper;
    SQLiteDatabase db;
    ImageView[] questions;
    RadioGroup[] answers;
    RadioButton[] choices;
    TextView[] comments;
    LinearLayout linear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_exam);
        Intent intent=getIntent();
        subject=intent.getStringExtra("subject").toLowerCase();
        year=intent.getStringExtra("year");
        //System.out.println("in displayexam subject is "+subject);
        linear=(LinearLayout) findViewById(R.id.linear_layout);
        databaseHelper=new ExamDatabaseHelper(this);
        db=databaseHelper.getWritableDatabase();
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
        questions=new ImageView[num_questions];
        answers= new RadioGroup[num_questions];
        choices=new RadioButton[num_questions*4];
        comments=new TextView[num_questions];

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
            String mDrawableName = subject+"_"+year+"_"+(i+1);
            //System.out.println("the image name is "+mDrawableName);
            int resID = getResources().getIdentifier(mDrawableName , "drawable", getPackageName());
            //System.out.println("the resource id of the image is "+resID);
            questions[i].setImageResource(resID);
            linear.addView(questions[i]);
            linear.addView(answers[i]);
            //lets add the comment textview for later use
            comments[i]=new TextView(this);
            comments[i].setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            linear.addView(comments[i]);
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
        //this code will enable us to recover saved state for users
        Cursor cursor=databaseHelper.select(db,"UserState",new String[]{"answer"},"subject=? and year=? and question_no=?",new String[]{subject,year,String.valueOf(question_idx+1)},null);
        if (cursor.getCount()>0){
            cursor.moveToFirst();
            int ans_index=cursor.getInt(0);
             RadioButton btn =(RadioButton) grp[question_idx].getChildAt(ans_index);
             btn.setChecked(true);
             //then we should reset the states
        }
    }

    public void showScore(View v){
        int total_questions=answers.length;
        int attempted=0;
        int correct=0;
        String []cols={"question_no","answer"};
        String where="subject=? and year=?";
        String []where_vals= {subject,year};
        Cursor cursor=databaseHelper.select(db,"Question",cols,where,where_vals,null);
        System.out.println("cursor length"+cursor.getCount());
        if (cursor.getCount()>0){
            while (cursor.moveToNext()){
                int question_no=cursor.getInt(0);
                String ans=cursor.getString(1);
                if(answers[question_no-1].getCheckedRadioButtonId() != -1){
                    int button_id=answers[question_no-1].getCheckedRadioButtonId();
                    RadioButton btn=findViewById(button_id);
                    int ans_index=answers[question_no-1].indexOfChild(btn);
                    attempted+=1;
                if (ans_index=="ABCD".indexOf(ans)){
                    correct+=1;
                }

            }
        }
         }
        TextView textView=findViewById(R.id.answer_display_area);
        textView.setText("total questions:"+total_questions+"\n"+"attempted questions:"+attempted+"\n"+"correct:"+correct);
    }
    public void saveAndExit(View v){
        System.out.println("in save and exit");
        for(int i=0;i<answers.length;i++){
            if(answers[i].getCheckedRadioButtonId() != -1){
                int button_id=answers[i].getCheckedRadioButtonId();
                RadioButton btn=findViewById(button_id);
                int ans_index=answers[i].indexOfChild(btn);
                ContentValues values =new ContentValues();
                values.put("subject",subject);
                values.put("year",year);
                values.put("question_no",String.valueOf(i+1));
                values.put("answer",String.valueOf(ans_index));
                databaseHelper.insert(db,"UserState",values);
            }
        }
        Intent i=new Intent(this,ExamsByYear.class);
        i.putExtra("subject",subject);
        startActivity(i);
    }


}