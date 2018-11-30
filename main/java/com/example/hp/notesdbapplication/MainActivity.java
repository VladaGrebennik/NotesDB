package com.example.hp.notesdbapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView notesList;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor notesCursor;
    final int MENU_ITEM_EDIT=1;
    final int MENU_ITEM_DELETE=2;
    //String addString,deleteString,editString;
    SCursorAdapter noteAdapter;
    TextView result;
    long noteId;
    SearchView searchView;
    String query;
    int fC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notesList = (ListView) findViewById(R.id.notesList);
        searchView = (SearchView) findViewById(R.id.app_bar_search);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        registerForContextMenu(notesList);
        result = (TextView) findViewById(R.id.result1);
        notesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                noteId = id;
                return false;
            }
        });

        fC=0;
    }

    private  void CreateNoteAdapter(){
        db = databaseHelper.getReadableDatabase();
        notesCursor =  db.rawQuery("select * from "+ DatabaseHelper.TABLE, null);
        String[] headers = new String[] {DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_DATE,DatabaseHelper.COLUMN_IMAGE};
        noteAdapter = new SCursorAdapter(this,R.layout.list_item,notesCursor,headers,new int[]{});

        noteAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {

                if (constraint == null || constraint.length() == 0) {

                    return db.rawQuery("select * from " + DatabaseHelper.TABLE, null);
                }
                if(constraint.equals("1") ||constraint.equals("2")|| constraint.equals("3")){
                    return db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                            DatabaseHelper.COLUMN_CLASS + " =" + constraint.toString(), null);
                }

                else {
                    return db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                            DatabaseHelper.COLUMN_NAME + " like ?", new String[]{"%" + constraint.toString() + "%"});
                }
            }
        });

        notesList.setAdapter(noteAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        CreateNoteAdapter();
        if(query!=null){
            noteAdapter.getFilter().filter(query);
        }
        if(fC!=0){
            noteAdapter.getFilter().filter(Integer.toString(fC));
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        db.close();
        notesCursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.setActivated(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                noteAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                noteAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch(id){
            case R.id.add_settings :
                Intent intent = new Intent(this, AddActivity.class);
                startActivity(intent);
                return true;

            case R.id.filter_settings:
                return true;

            case R.id.menu_imp_n:
                fC=1;
                noteAdapter.getFilter().filter("1");
                return true;

            case R.id.menu_imp_i:
                fC=2;
                noteAdapter.getFilter().filter("2");
                return true;

            case R.id.menu_imp_v:
                fC=3;
                noteAdapter.getFilter().filter("3");
                return true;

            case R.id.allNotes:
                fC = 0;
                CreateNoteAdapter();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.notesList) {
            menu.add(0,MENU_ITEM_EDIT,0,"edit");
            menu.add(0,MENU_ITEM_DELETE,0,"delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {

            case MENU_ITEM_EDIT:
                Intent intent = new Intent(this,EditActivity.class);
                intent.putExtra("id",noteId);
                startActivity(intent);
                break;
            case MENU_ITEM_DELETE:
                db.delete(DatabaseHelper.TABLE, "_id = ?", new String[]{String.valueOf(noteId)});
                onResume();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!searchView.getQuery().toString().isEmpty()){
            outState.putString("search",searchView.getQuery().toString());
        }
        if(fC!=0){
            outState.putInt("filter",fC);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        query = savedInstanceState.getString("search");
        fC = savedInstanceState.getInt("filter");

    }
}


