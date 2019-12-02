package com.example.bt_fp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fgtit.FingerprintReader;
import com.fgtit.fpcore.FPMatch;
import com.fgtit.reader.Constants;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button registerButton = findViewById(R.id.register);
        Button verifyButton = findViewById(R.id.verify);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FingerprintReader reader = new FingerprintReader(MainActivity.this)
                        .recordImages(true)
                        .setFingers(Constants.FINGERS.LEFT_HAND_INDEX_FINGER, Constants.FINGERS.RIGHT_HAND_INDEX_FINGER)
                        .setRequestCode(14).setVerificationCount(2);
                try {
                    reader.enroll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FingerprintReader reader = new FingerprintReader(MainActivity.this)
                        .recordImages(true)
                        .setFingers(Constants.FINGERS.LEFT_HAND_INDEX_FINGER, Constants.FINGERS.RIGHT_HAND_INDEX_FINGER)
                        .setRequestCode(15).setVerificationCount(2);
                try {
                    reader.verify();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 14) {
            if (data != null) {
                String userId = data.getStringExtra(Constants.RESPONDENT_RESULT);

                showInputDialog(userId);
            }
        } else if (requestCode == 15) {
            String userId = null;
            if (data != null) {
                userId = data.getStringExtra(Constants.RESPONDENT_RESULT);
                Log.e("DEBUG_APP", "User id is: "+userId!=null?userId:"null");
                // get the users in our database
                DBHelper userDBHelper = new DBHelper(this);
                SQLiteDatabase userDB = userDBHelper.getWritableDatabase();
                Cursor cursor = userDB.query(DBHelper.USERS_TABLE, null, com.example.bt_fp.DBHelper.USER_ID + " = '" + userId + "'",
                        null, null, null, null, null);
                ((TextView) findViewById(R.id.messages)).setText("Could not get match");
                if (cursor != null) {
                    if (cursor.getCount() > 0)
                        while (cursor.moveToNext()) {
                            String respondent = cursor.getString(cursor.getColumnIndex(DBHelper
                                    .USER_NAME));
                            if (!TextUtils.isEmpty(respondent)) {
                                ((TextView) findViewById(R.id.messages)).setText("Got a match: " + respondent);
                                break;
                            }
                        }
                    cursor.close();
                }
            }
        }
    }


    private void showInputDialog(String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter user's name");

        // Set up the input
        View view = getLayoutInflater().inflate(R.layout.input_dialog_edit_text, null);
        final EditText input = view.findViewById(R.id.id_input_edit_text);
        // Specify the type of input expected;
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(40);
        input.setFilters(filters);
        builder.setView(view);

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String title = input.getText().toString();
            if (!TextUtils.isEmpty(title.trim())) {

                //save into database
                DBHelper userDBHelper = new DBHelper(this);
                SQLiteDatabase userDB = userDBHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(com.example.bt_fp.DBHelper.USER_ID, userId);
                values.put(DBHelper.USER_NAME, title.trim());
                userDB.insert(DBHelper.USERS_TABLE, null, values);

                ((TextView) findViewById(R.id.messages)).setText("New user has been added");
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
