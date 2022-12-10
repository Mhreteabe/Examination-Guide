package com.example.matricexaminationguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;

public class AnalysisPage extends AppCompatActivity {

    String subject;
    String year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_page);

        //initalize and assign variable
        BottomNavigationView bottomNavigationView= findViewById(R.id.bottom_navigation);

        //set home selected
        bottomNavigationView.setSelectedItemId(R.id.analysis);

        //perform itemselectedlistner
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.analysis:
                        return true;
                    case R.id.home:
                        Intent i=new Intent(AnalysisPage.this,MainActivity.class);
                        startActivity(i);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        Intent a=new Intent(AnalysisPage.this,ProfilePage.class);
                        startActivity(a);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        ExamDatabaseHelper databaseHelper=new ExamDatabaseHelper(this);
        SQLiteDatabase db;
        db=databaseHelper.getWritableDatabase();

        Intent intent = getIntent();
        subject = intent.getStringExtra("subject");
        year = intent.getStringExtra("year");

        String[] cols = {"chapter", "grade", "number_of_attempts", "number_of_correct_attempts"};
        String where = "subject=? and year=?";
        String[] where_vals = {subject};
        Cursor cursor = databaseHelper.select(db, "QuestionStatistics", cols, where, where_vals, null);
        TextView tv = findViewById(R.id.analysis_textview);

        HashMap<Integer,Integer> wrong_grade_12=new HashMap<>();
        HashMap<Integer,Integer> wrong_grade_11=new HashMap<>();

        while (cursor.moveToNext()) {
            int chapter = cursor.getInt(0);
            int grade = cursor.getInt(1);
            int number_of_attempts = cursor.getInt(2);
            int number_of_correct_attempts = cursor.getInt(3);

            int number_of_wrong = number_of_attempts - number_of_correct_attempts;

            if (grade == 12 ) {
             if(number_of_wrong>=1){
                    int newvalue=1;

                    if(wrong_grade_12.containsKey(chapter)) {
                        int existingvalue = wrong_grade_12.get(chapter);
                        existingvalue=existingvalue+1;
                        wrong_grade_12.put(chapter, existingvalue);
                    }
                    else
                        wrong_grade_12.put(chapter,newvalue);

                }


            }
            else if (grade==11){
                if(number_of_wrong>=1){
                    int newvalue=1;

                    if(wrong_grade_11.containsKey(chapter)) {
                        int existingvalue = wrong_grade_11.get(chapter);
                        existingvalue=existingvalue+1;
                        wrong_grade_11.put(chapter, existingvalue);
                    }
                    else
                        wrong_grade_11.put(chapter,newvalue);

                }
            }


        }


        StringBuilder stringBuilder11=new StringBuilder();
        stringBuilder11.append("Grade 11 \n");
        for (int i=1;i<=15;i++){
            if (wrong_grade_11.containsKey(i)){
                int ch=wrong_grade_11.get(i);
                stringBuilder11.append("Incorect question from chapter "+i+" = "+ch+"\n");
            }
        }

        StringBuilder stringBuilder12=new StringBuilder();
        stringBuilder12.append("Grade 12 \n");
        for (int i=1;i<=15;i++){
            if (wrong_grade_12.containsKey(i)){
                int ch=wrong_grade_12.get(i);
                stringBuilder12.append("Inncorect question from chapter "+i+" = "+ch+"\n");
            }
        }

        StringBuilder stringBuilder;
        if (wrong_grade_11.isEmpty()) {
            stringBuilder = new StringBuilder(stringBuilder11+"\nNo wrong question from grade 11 " + "\n\n" + stringBuilder12);
        }
        else if(wrong_grade_12.isEmpty()){
            stringBuilder = new StringBuilder(stringBuilder11 + "\n\n" + stringBuilder12+"\nNo wrong question from grade 12 ");
        }else
            stringBuilder= new StringBuilder(stringBuilder11 + "\n\n" + stringBuilder12);

        tv.setText(stringBuilder);

    }

    }
