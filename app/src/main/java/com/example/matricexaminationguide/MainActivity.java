package com.example.matricexaminationguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public  void ViewByYear(View v){
        Button clicked= (Button)v;
        String subject=clicked.getText().toString();
        Intent i=new Intent(MainActivity.this,ExamsByYear.class);
        i.putExtra("subject",subject);
        startActivity(i);
    }
}