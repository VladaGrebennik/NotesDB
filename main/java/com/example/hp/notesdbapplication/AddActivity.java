package com.example.hp.notesdbapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import java.util.Date;

import java.io.IOException;
import java.io.InputStream;


public class AddActivity extends AppCompatActivity {
    int noteclass=1;
    EditText titleText, descText;
    static final int GALLERY_REQUEST = 1;
    Bitmap bitmap;
    String uriStr="";
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.getWritableDatabase();

        titleText = (EditText) findViewById(R.id.titleText);
        descText = (EditText) findViewById(R.id.descText);
        RadioGroup radGrp = (RadioGroup)findViewById(R.id.radios);
        radGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int id) {
                switch(id) {
                    case R.id.not_important:
                        noteclass=1;
                        break;
                    case R.id.important:
                        noteclass=2;
                        break;
                    case R.id.very_important:
                        noteclass=3;
                    default:
                        break;
                }
            }});

        Button button = (Button)findViewById(R.id.addPhoto);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        ImageView imageView = (ImageView) findViewById(R.id.photoView);
        //Uri selectedImage;
        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    // imageReturnedIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri selectedImage = imageReturnedIntent.getData();
                    uriStr=selectedImage.toString();
                    try {
                        final InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        bitmap= BitmapFactory.decodeStream(imageStream);
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    public void saveNote(View view){

        Date date = new Date();
        java.sql.Date datesql = new java.sql.Date(date.getTime());
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, titleText.getText().toString());
        cv.put(DatabaseHelper.COLUMN_DESC, descText.getText().toString());
        cv.put(DatabaseHelper.COLUMN_CLASS, noteclass);
        cv.put(DatabaseHelper.COLUMN_DATE, datesql.toString());
        cv.put(DatabaseHelper.COLUMN_IMAGE, uriStr);

        db.insert(DatabaseHelper.TABLE, null, cv);

        goHome();
    }
    private void goHome(){
        // закрываем подключение
        db.close();
        // переход к главной activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
