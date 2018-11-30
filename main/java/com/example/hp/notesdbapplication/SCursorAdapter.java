package com.example.hp.notesdbapplication;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SCursorAdapter extends SimpleCursorAdapter {

    private LayoutInflater mLayoutInflater;
    private Context context;
    private int layout;

    private class ViewHolder {
        TextView titleText,timeText;
        ImageView imageView;

        ViewHolder(View v) {
            titleText = (TextView) v.findViewById(R.id.title);
            imageView = (ImageView) v.findViewById(R.id.photo);
            timeText = (TextView) v.findViewById(R.id.time);
        }
    }

    public SCursorAdapter (Context ctx, int layout, Cursor c, String[] from, int[] to) {
        super(ctx, layout, c, from, to);
        this.context = ctx;
        this.layout = layout;
        mLayoutInflater = LayoutInflater.from(ctx);
    }



    @Override
    public View newView(Context ctx, Cursor cursor, ViewGroup parent) {
        View vView = mLayoutInflater.inflate(layout, parent, false);
        vView.setTag( new ViewHolder(vView) );
        return vView;
    }

    @Override
    public void bindView(View v, Context ctx, Cursor c) {

        int iCol_Text = c.getColumnIndex(DatabaseHelper.COLUMN_NAME);
        int iCol_Image = c.getColumnIndex(DatabaseHelper.COLUMN_IMAGE);
        int iCol_Time = c.getColumnIndex(DatabaseHelper.COLUMN_DATE);

        String sText = c.getString(iCol_Text);
        String sFileAndPath_Image = c.getString (iCol_Image);  //// path & file
        String sTime = c.getString (iCol_Time);
        ViewHolder vh = (ViewHolder) v.getTag();

        vh.titleText.setText(sText);
        vh.imageView.setImageURI(Uri.parse(sFileAndPath_Image));
        vh.timeText.setText(sTime);
    }
}