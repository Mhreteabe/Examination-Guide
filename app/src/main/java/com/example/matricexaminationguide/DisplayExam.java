package com.example.matricexaminationguide;

import static java.sql.DriverManager.println;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
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
    ScrollView scroll_view=null;
    Button next,previous;
    int current_question_index=0;
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
        //addAnswer(databaseHelper,db);
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
            answers[i].setGravity(Gravity.CENTER);
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
            databaseHelper.delete(db,"UserState","subject=? and year=? and question_no=?",new String[]{subject,year,String.valueOf(question_idx+1)});

        }
    }
    public void updateQuestionStatisticsTable(int question_no,boolean is_correct){
        String []cols={"number_of_attempts","number_of_correct_attempts"};
        String where="subject=? and year=? and question_no=?";
        String []where_vals= {subject,year,String.valueOf(question_no)};
        Cursor cursor=databaseHelper.select(db,"QuestionStatistics",cols,where,where_vals,null);
        if (cursor.moveToFirst()){
            int number_of_attempts=cursor.getInt(0);
            int number_of_correct_attempts=cursor.getInt(0);
            //the number_of_attempts should increase whether the student got the question right or wrong
            number_of_attempts+=1;
            //number_of_correct_attempts should only increase when the student is correct
            if (is_correct){
                number_of_correct_attempts+=1;
            }
            //we should save our changes to the database
            ContentValues values=new ContentValues();
            values.put("number_of_attempts",number_of_attempts);
            values.put("number_of_correct_attempts",number_of_correct_attempts);
            databaseHelper.update(db,"QuestionStatistics",values,where,where_vals);
        }

    }


    public void updateExamStatisticsTable(int number_of_attempted_questions,int correct_number_of_questions){
        String []cols={"attempt_number"};
        String where="subject=? and year=?";
        String []where_vals= {subject,year};
        Cursor cursor=databaseHelper.select(db,"ExamStatistics",cols,where,where_vals,null);
        if (cursor.getCount()>=1) {
            //lets get the number of the last attempt
            cursor.moveToLast();
            int last_attempt_number=cursor.getInt(0);
            //lets add the current attempt to the database
            ContentValues values=new ContentValues();
            values.put("subject",subject);
            values.put("year",year);
            values.put("attempt_number",last_attempt_number+1);
            values.put("number_of_attempted_questions",number_of_attempted_questions);
            values.put("correct_number_of_questions",correct_number_of_questions);
            databaseHelper.insert(db,"ExamStatistics",values);
        }
        }



    public void showScore(View v){
        int total_questions=answers.length;
        int attempted=0;
        int correct=0;
        attempted=0;
        correct=0;
        String []cols={"question_no","answer"};
        String where="subject=? and year=?";
        String []where_vals= {subject,year};
        Cursor cursor=databaseHelper.select(db,"Question",cols,where,where_vals,null);
        //System.out.println("cursor length"+cursor.getCount()+"attempted and correct"+attempted+","+correct);
        if (cursor.getCount()>0){
            while (cursor.moveToNext()){
                int question_no=cursor.getInt(0);
                String ans=cursor.getString(1);
                if(answers[question_no-1].getCheckedRadioButtonId() != -1){
                        int button_id=answers[question_no-1].getCheckedRadioButtonId();
                        RadioButton btn=findViewById(button_id);
                        int ans_index=answers[question_no-1].indexOfChild(btn);
                        attempted+=1;
                        boolean is_correct;
                        if (ans_index=="ABCD".indexOf(ans)){
                        correct+=1;
                        //set the color of button group to Green
                        answers[question_no-1].setBackgroundColor(Color.GREEN);
                        //give him an explanation
                        comments[question_no-1].setText("Well done!");
                        comments[question_no-1].setGravity(Gravity.CENTER);
                        is_correct=true;
                    }
                    else{
                        //since the user got the question wrong set the background color to red
                        answers[question_no-1].setBackgroundColor(Color.RED);
                        //give him an explanation
                        comments[question_no-1].setText("Try again");
                        comments[question_no-1].setGravity(Gravity.CENTER);
                        is_correct=false;
                    }
                    //we should update the QuestionStatistics table only if the question is attempted
                    updateQuestionStatisticsTable(question_no,is_correct);
                }
        }
        //since the user is finished ,let's disable the finish button
            Button finish=(Button)v;
            finish.setEnabled(false);

         }
        //lets update our ExamStatisticsDatabase
        updateExamStatisticsTable(attempted,correct);
        //lets display the result to the user
        TextView textView=findViewById(R.id.answer_display_area);
        textView.setText("total questions:"+total_questions+"\n"+"attempted questions:"+attempted+"\n"+"correct:"+correct);
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
    }
    public void saveAndExit(View v) {
        Button finish_btn = (Button) findViewById(R.id.finish);
        if (finish_btn.isEnabled() == false) {
            Intent i = new Intent(this, ExamsByYear.class);
            i.putExtra("subject", subject);
            startActivity(i);

        } else {
            System.out.println("in save and exit");
            for (int i = 0; i < answers.length; i++) {
                if (answers[i].getCheckedRadioButtonId() != -1) {
                    int button_id = answers[i].getCheckedRadioButtonId();
                    RadioButton btn = findViewById(button_id);
                    int ans_index = answers[i].indexOfChild(btn);
                    ContentValues values = new ContentValues();
                    values.put("subject", subject);
                    values.put("year", year);
                    values.put("question_no", String.valueOf(i + 1));
                    values.put("answer", String.valueOf(ans_index));
                    databaseHelper.insert(db, "UserState", values);
                }
            }
            Intent i = new Intent(this, ExamsByYear.class);
            i.putExtra("subject", subject);
            startActivity(i);
        }
    }
  public void switchToSingleMode(View v){
      Switch btn=(Switch) v;
      if(scroll_view == null){
          scroll_view=(ScrollView) findViewById(R.id.scroll_view);
      }
      LinearLayout main_layout=(LinearLayout) findViewById(R.id.mainLinearLayout);
      if(btn.isChecked()){
          next=new Button(this);
          previous=new Button(this);
          next.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {


                  main_layout.removeView(questions[current_question_index]);
                  main_layout.removeView(answers[current_question_index]);
                  main_layout.removeView(comments[current_question_index]);
                  current_question_index+=1;

                  linear.removeView(questions[current_question_index]);
                  linear.removeView(answers[current_question_index]);
                  linear.removeView(comments[current_question_index]);
                  main_layout.addView(questions[current_question_index]);
                  main_layout.addView(answers[current_question_index]);
                  main_layout.addView(comments[current_question_index]);
              }
          });
          previous.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {

                  main_layout.removeView(questions[current_question_index]);
                  main_layout.removeView(answers[current_question_index]);
                  main_layout.removeView(comments[current_question_index]);
                  current_question_index-=1;

                  linear.removeView(questions[current_question_index]);
                  linear.removeView(answers[current_question_index]);
                  linear.removeView(comments[current_question_index]);
                  main_layout.addView(questions[current_question_index]);
                  main_layout.addView(answers[current_question_index]);
                  main_layout.addView(comments[current_question_index]);
              }
          });
          main_layout.removeView(scroll_view);
          linear.removeView(questions[0]);
          linear.removeView(answers[0]);
          linear.removeView(comments[0]);
          next.setText("next");
          previous.setText("previous");
          main_layout.addView(next);
          main_layout.addView(previous);
          main_layout.addView(questions[0]);
          main_layout.addView(answers[0]);
          main_layout.addView(comments[0]);

      }
      else{
          main_layout.removeView(questions[current_question_index]);
          main_layout.removeView(answers[current_question_index]);
          main_layout.removeView(comments[current_question_index]);
          current_question_index=0;
          linear.removeAllViews();
          for (int i=0;i<questions.length;i++){
              linear.addView(questions[i]);
              linear.addView(answers[i]);
              linear.addView(comments[i]);

          }
          main_layout.removeView(next);
          main_layout.removeView(previous);
          main_layout.addView(scroll_view);
      }

  }

}