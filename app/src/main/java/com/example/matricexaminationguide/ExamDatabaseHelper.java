package com.example.matricexaminationguide;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.io.File;
public class ExamDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME="EXAMS";
    private static final int DB_VERSION=1;

    ExamDatabaseHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
        //System.out.println("in examdatabase helper constructor");
    }


    public void onCreate(SQLiteDatabase db){
        //System.out.println("in onCreate");
        String create_info_table="Create Table Info("+
                                 "_id Integer primary key autoincrement,"+
                                 "subject text,"+
                                 "year integer);";
        String create_info_table2="Create Table Info2("+
                "_id Integer primary key autoincrement,"+
                "subject text,"+
                "num_questions integer);";
        String create_question_table="Create Table Question("+
                "_id Integer primary key autoincrement,"+
                "subject text,"+
                "year integer,"+
                "chapter text,"+
                "grade integer,"+
                "question_no integer,"+
                "answer text);";

        String [] queries = new String[]{
                create_info_table,
                create_info_table2,
                create_question_table
        };
        for (String query:queries){
            db.execSQL(query);
        }
        Map<String, Object> subject_to_year = new HashMap<String, Object>();
        subject_to_year.put("Maths",new int[]{2010,2011,2012,2013,2014});
        subject_to_year.put("Physics",new int[]{2010,2011,2012,2013,2014});
        subject_to_year.put("Aptitude",new int[]{2010,2011,2012,2013,2014});
        ContentValues values=new ContentValues();
        for (String subject : subject_to_year.keySet()) {
            for (int year:(int[])subject_to_year.get(subject)){
             values.put("subject",subject);
             values.put("year",year);
             insert(db,"Info",values);
            }
        }

        Map<String, Object> subject_to_numquestions = new HashMap<String, Object>();
        subject_to_numquestions.put("Maths",20);
        subject_to_numquestions.put("Physics",50);
        subject_to_numquestions.put("Aptitude",60);
        values=new ContentValues();
        for (String subject : subject_to_numquestions.keySet()) {
            values.put("subject",subject);
            values.put("num_questions",(int)subject_to_numquestions.get(subject));
            insert(db,"Info2",values);
            }
        //selectall(db,"Info");
        //selectall(db,"Info2");


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldversion, int newversion) {

    }

    public  long insert(SQLiteDatabase db, String table_name, ContentValues values){
        long res=db.insert(table_name,null,values);
        return res;
    }
    public void  selectall(SQLiteDatabase db,String table_name){
        Cursor cursor=db.query(table_name,
                null,
                null,
                null,
                null,
                null,
                null);
        //System.out.println("In select all");
        while (cursor.moveToNext()){
            int numcols=cursor.getColumnCount();
            for(int i=0;i<numcols;i++){
                System.out.print(cursor.getString(i)+"  ");
            }
            //System.out.println();
        }
    }

    public   void update(SQLiteDatabase db,String table_name,ContentValues values,String where_cols,String[] where_vals){
        db.update(table_name,
                values,
                where_cols,
                where_vals);
    }
    public   void delete(SQLiteDatabase db,String table_name,String where_cols,String[] where_vals){
        db.delete(table_name,
                where_cols,
                where_vals);
    }

    public Cursor select(SQLiteDatabase db,String table_name,String[] selected_cols,String where_cols,String[] where_vals,String orderby){
        //System.out.println("in select method");
        Cursor cursor=db.query(table_name,
                selected_cols,
                where_cols,
                where_vals,
                null,
                null,
        orderby);

        return cursor;
    }
}
