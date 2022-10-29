package com.example.matricexaminationguide;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.io.File;
import androidx.appcompat.app.AppCompatActivity;
public class ExamDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME="EXAMS";
    private static final int DB_VERSION=1;
    private  Context context;
    ExamDatabaseHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
        this.context=context;
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
                "chapter integer,"+
                "grade integer,"+
                "question_no integer,"+
                "answer text);";
        String create_userstate_table="Create Table UserState("+
                "_id Integer primary key autoincrement,"+
                "subject text,"+
                "year integer,"+
                "question_no integer,"+
                "answer integer);";
        //for nahom
        //you should use the following 2 tables to do the analysis
        String create_question_statistics_table="Create Table QuestionStatistics("+
                "_id Integer primary key autoincrement,"+
                "subject text,"+
                "year integer,"+
                "chapter integer,"+
                "grade integer,"+
                "question_no integer,"+
                "number_of_attempts integer,"+
                "number_of_correct_attempts integer);";
        String create_exam_statistics_table="Create Table ExamStatistics("+
                "_id Integer primary key autoincrement,"+
                "subject text,"+
                "year integer,"+
                "attempt_number integer,"+
                "number_of_attempted_questions integer,"+
                "correct_number_of_questions integer"+");";

        String [] queries = new String[]{
                create_info_table,
                create_info_table2,
                create_question_table,
                create_userstate_table,
                create_question_statistics_table,
                create_exam_statistics_table
        };
        for (String query:queries){
            db.execSQL(query);
        }
        Map<String, Object> subject_to_year = new HashMap<String, Object>();
        subject_to_year.put("math",new int[]{2010,2011,2012,2013,2014});
        subject_to_year.put("physics",new int[]{2010,2011,2012,2013,2014});
        subject_to_year.put("aptitude",new int[]{2010,2011,2012,2013,2014});
        ContentValues values=new ContentValues();
        for (String subject : subject_to_year.keySet()) {
            for (int year:(int[])subject_to_year.get(subject)){
             values.put("subject",subject);
             values.put("year",year);
             insert(db,"Info",values);
            }
            //lets initialize the ExamStatistics table
            values.put("attempt_number",0);
            values.put("number_of_attempted_questions",0);
            values.put("correct_number_of_questions",0);
            insert(db,"ExamStatistics",values);
            //now lets remove the cols
            values.remove("attempt_number");
            values.remove("number_of_attempted_questions");
            values.remove("correct_number_of_questions");

        }

        Map<String, Object> subject_to_numquestions = new HashMap<String, Object>();
        subject_to_numquestions.put("math",20);
        subject_to_numquestions.put("physics",50);
        subject_to_numquestions.put("aptitude",60);
        values=new ContentValues();
        for (String subject : subject_to_numquestions.keySet()) {
            values.put("subject",subject);
            values.put("num_questions",(int)subject_to_numquestions.get(subject));
            insert(db,"Info2",values);
            }
        //selectall(db,"Info");
        //selectall(db,"Info2");

        //lets try to add the answers to the db
        addAnswer(this,db);

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
                        new InputStreamReader(context.getAssets().open(path)));
                String header[]=reader.readLine().split(",");
                String subject=header[0];
                String year=header[1];
                //System.out.println("i_nside of add answer for"+year+subject);
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
                    //we are gone load our statistics table
                    //to do that we are gone remove the answer column
                    //also we are gone add number_of_attempts and number_of_correct_attempts column
                    values.remove("answer");
                    values.put("number_of_attempts",0);
                    values.put("number_of_correct_attempts",0);
                    helper.insert(db,"QuestionStatistics",values);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
