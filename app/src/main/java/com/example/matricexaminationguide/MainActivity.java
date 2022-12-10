package com.example.matricexaminationguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initalize and assign variable
        BottomNavigationView bottomNavigationView= findViewById(R.id.bottom_navigation);

        //set home selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        //perform itemselectedlistner
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        return true;
                    case R.id.analysis:
                    Intent i=new Intent(MainActivity.this,AllSubjectAnalysis.class);
                    startActivity(i);
                    overridePendingTransition(0,0);
                    return true;

                    case R.id.profile:
                        Intent a=new Intent(MainActivity.this,ProfilePage.class);
                        startActivity(a);
                        overridePendingTransition(0,0);
                        return true;


            }
                return false;
            }
        });



    }

    public  void allsubject(View v){
        Intent i=new Intent(MainActivity.this,AllSubject.class);
        startActivity(i);
    }

}
