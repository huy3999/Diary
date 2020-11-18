package com.huy3999.diary;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import petrov.kristiyan.colorpicker.ColorPicker;

public class EntryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private ImageButton mSelectImage;
    private EditText mEntryTitle;
    private EditText mEntryContent;
        private Button mAddEntryBtn;

    private Uri mImageUri = null;

    private static final int GALLERY_REQUEST = 1;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    BottomNavigationView bottomNavigationView;
    private String date;
    private String time;
    private int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();


        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Entries");


        //mSelectImage = (ImageButton) findViewById(R.id.imgBtn);
        mEntryTitle = findViewById(R.id.titleField);
        mEntryContent = findViewById(R.id.contentField);
        mAddEntryBtn = findViewById(R.id.addBtn);
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mProgress = new ProgressDialog(this);



        mAddEntryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createEntry();
            }
        });
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_date:
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                    DatePickerDialog dialog = new DatePickerDialog(EntryActivity.this, dateSetListener,
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
                    dialog.show();
                    return true;
                case R.id.action_time:
                    timePicker();
                    return true;
                case R.id.action_color:
                    final ColorPicker colorPicker = new ColorPicker(EntryActivity.this);
                    ArrayList<String> colors = new ArrayList<>();
                    colors.add("#82B926");
                    colors.add("#a276eb");
                    colors.add("#6a3ab2");
                    colors.add("#666666");
                    colors.add("#FFFF00");
                    colors.add("#3C8D2F");
                    colors.add("#FA9F00");
                    colors.add("#FF0000");
                    colors.add("#3f51b5");
                    colorPicker
                            .setRoundColorButton(true)
                            .setDefaultColorButton(Color.parseColor("#f84c44"))
                            .setColors(colors);
                    colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                        @Override
                        public void onChooseColor(int position, int returnColor) {
                            color = returnColor;
                            Toast.makeText(EntryActivity.this, " "+color, Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancel() {

                        }
                    });

                    colorPicker.show();
                    return true;
                case R.id.action_delete:

                    finish();
                    return true;
            }
            return false;
        }

    };
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {

            date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
            Toast.makeText(EntryActivity.this, date, Toast.LENGTH_SHORT).show();
        }
    };

    private void timePicker(){
        boolean is24HView = true;
        int selectedHour = 10;
        int selectedMinute = 20;

// Time Set Listener.
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                time = i + ":" + i1;
//                selectedMinute = i;
//                selectedHour = i1;
                Toast.makeText(EntryActivity.this, time, Toast.LENGTH_SHORT).show();
            }

        };

// Create TimePickerDialog:
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,

                timeSetListener, selectedMinute, selectedHour, is24HView);

// Show
        timePickerDialog.show();
    }
    private void createEntry() {

        mProgress.setMessage("Adding to Diary....");
        final String title_val = mEntryTitle.getText().toString().trim();
        final String content_val = mEntryContent.getText().toString().trim();

        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(content_val)){  //Check if all content is provided
            mProgress.show();
                    DatabaseReference newEntry = mDatabase.push();

                    newEntry.child("title").setValue(title_val);
                    newEntry.child("content").setValue(content_val);
                    //newEntry.child("image").setValue(dowloadUri.toString());
                    newEntry.child("uid").setValue(mCurrentUser.getUid());
                    newEntry.child("date").setValue(date+" " +time);
                    newEntry.child("color").setValue(color);

                    mProgress.dismiss();

                    startActivity(new Intent(EntryActivity.this, DisplayActivity.class));

            
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(9);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

    }
}
