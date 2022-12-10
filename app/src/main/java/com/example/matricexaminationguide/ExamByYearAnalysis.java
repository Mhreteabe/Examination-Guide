package com.example.matricexaminationguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class ExamByYearAnalysis extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String subject;
    String year;
    ExamDatabaseHelper databaseHelper;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_by_year_analysis);

            Intent intent=getIntent();
            subject=intent.getStringExtra("subject").toLowerCase();

        Spinner spinner=findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.years, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        spinner.getOnItemSelectedListener();



        databaseHelper=new ExamDatabaseHelper(this);
        db=databaseHelper.getWritableDatabase();


        year = intent.getStringExtra("year");

        String[] cols = {"chapter", "grade", "number_of_attempts", "number_of_correct_attempts"};
        String where = "subject=?";
        String[] where_vals = {subject};
        Cursor cursor = databaseHelper.select(db, "QuestionStatistics", cols, where, where_vals, null);
        TextView tv = findViewById(R.id.chapter_textview);


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


    private void analysispage(View v) {
        Button btn=(Button)v;
        String year=btn.getText().toString();
        Intent i=new Intent(ExamByYearAnalysis.this,AnalysisPage.class);
        i.putExtra("subject",subject);
        i.putExtra("year",year);
        startActivity(i);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String year=adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(),year,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}