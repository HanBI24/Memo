package hello.world.study;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_INSERT_CODE = 1000;
    private MemoAdapter memoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, MemoActivity.class), REQUEST_INSERT_CODE);
            }
        });

        ListView listView = (ListView)findViewById(R.id.list);
        MemoDbHelper dbHelper = MemoDbHelper.getInstance(this);
        Cursor cursor = dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME, null, null, null, null, null, MemoContract.MemoEntry._ID+ " DESC");
        memoAdapter = new MemoAdapter(this, getMemoCursor());
        listView.setAdapter(memoAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MemoActivity.class);
                Cursor cursor = (Cursor)memoAdapter.getItem(position);
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_TITLE));
                String contents = cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_CONTENTS));

                intent.putExtra("id", id);
                intent.putExtra("title", title);
                intent.putExtra("contents", contents);

                startActivityForResult(intent, REQUEST_INSERT_CODE);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final long deleteId = id;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("삭제")
                        .setMessage("메모를 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SQLiteDatabase db = MemoDbHelper.getInstance(MainActivity.this).getWritableDatabase();
                                int deleteCount = db.delete(MemoContract.MemoEntry.TABLE_NAME, MemoContract.MemoEntry._ID + " = " + deleteId, null);

                                if (deleteCount == 0) {
                                    Toast.makeText(MainActivity.this, "삭제에 문제가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    memoAdapter.swapCursor(getMemoCursor());
                                    Toast.makeText(MainActivity.this, "메모가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                builder.setNegativeButton("취소", null)
                        .show();

                return true;
            }
        });
    }

    private static class MemoAdapter extends CursorAdapter {

        public MemoAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView titleText = (TextView)view.findViewById(android.R.id.text1);
            titleText.setText(cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_TITLE)));
        }
    }

    private Cursor getMemoCursor() {
        MemoDbHelper dbHelper = MemoDbHelper.getInstance(this);
        return dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME, null, null, null, null, null, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // If memo is inserted to normal, update memo list.
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_INSERT_CODE && resultCode == RESULT_OK) {
            memoAdapter.swapCursor(getMemoCursor());
        }
    }
}