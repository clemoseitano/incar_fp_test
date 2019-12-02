package com.example.bt_fp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Copyright (c) 2019 Farmerline LTD. All rights reserved.
 * Created by Clement Osei Tano K on 02/12/2019.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_USER_VERSION = 1;
    private static final String DB_NAME = "sample_db";

    // users table fields for enrolment
    public static final String USERS_TABLE = "users";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "name";
    public static final String CREATED_AT = "created_at";


    private static final String CREATE_FP_RESPONDENT_TABLE = "CREATE TABLE " +
            USERS_TABLE +
            " ( " +
            " `id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
            USER_ID +
            " TEXT NOT NULL UNIQUE, " +
            USER_NAME +
            " INTEGER NOT NULL, " +
            CREATED_AT +
            " DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP);";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_USER_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_FP_RESPONDENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}