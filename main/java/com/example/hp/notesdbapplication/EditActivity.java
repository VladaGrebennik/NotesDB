package com.example.hp.notesdbapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    int noteclass=1;
    EditText titleText,descText;
    static final int GALLERY_REQUEST = 1;
    Bitmap bitmap;
    String uriStr="";
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    long noteId;
    Cursor notesCursor;
    RadioButton one,two,three;
    ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.getWritableDatabase();

//        sqlHelper = new DatabaseHelper(this);
//        db = sqlHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            noteId = extras.getLong("id");
        }
        one = (RadioButton) findViewById(R.id.not_importantEdit);
        two = (RadioButton) findViewById(R.id.importantEdit);
        three = (RadioButton) findViewById(R.id.very_importantEdit);


        titleText = (EditText) findViewById(R.id.titleEditText);
        descText = (EditText) findViewById(R.id.descTextEdit);
        photo = (ImageView) findViewById(R.id.photoViewEdit);
        RadioGroup radGrp = (RadioGroup)findViewById(R.id.radiosEdit);
        radGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int id) {
                switch(id) {
                    case R.id.not_importantEdit:
                        noteclass=1;
                        break;
                    case R.id.importantEdit:
                        noteclass=2;
                        break;
                    case R.id.very_importantEdit:
                        noteclass=3;
                    default:
                        break;
                }
            }});

        Button button = (Button)findViewById(R.id.editPhoto);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });

        notesCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(noteId)});
        notesCursor.moveToFirst();
        titleText.setText(notesCursor.getString(1));
        descText.setText(notesCursor.getString(2));
        uriStr = notesCursor.getString(5);
        photo.setImageURI(Uri.parse(uriStr));

        if(notesCursor.getInt(3)==1){
            one.setChecked(true);
            //one.isChecked();
        }
        else if(notesCursor.getInt(3)==2){
            two.setChecked(true);
        }
        else if(notesCursor.getInt(3)==3){
            three.setChecked(true);
        }
        notesCursor.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        ImageView imageView = (ImageView) findViewById(R.id.photoViewEdit);
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {

                    Uri selectedImage = imageReturnedIntent.getData();
                    uriStr = selectedImage.toString();

                    try {
                        final InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        bitmap = BitmapFactory.decodeStream(imageStream);
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    public void editNote(View view) {
        Date date = new Date();
        java.sql.Date datesql = new java.sql.Date(date.getTime());
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, titleText.getText().toString());
        cv.put(DatabaseHelper.COLUMN_DESC, descText.getText().toString());
        cv.put(DatabaseHelper.COLUMN_CLASS, noteclass);
        cv.put(DatabaseHelper.COLUMN_DATE, datesql.toString());
        cv.put(DatabaseHelper.COLUMN_IMAGE, uriStr);
        db.update(DatabaseHelper.TABLE, cv, DatabaseHelper.COLUMN_ID + "=" + String.valueOf(noteId), null);
        goHome();
    }

    private void goHome(){
        db.close();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
