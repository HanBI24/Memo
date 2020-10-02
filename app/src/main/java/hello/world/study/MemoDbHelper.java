package hello.world.study;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class.
 * Make to easy that create DB and table and modify etc...
 * Follow google API's documents.
 */

public class MemoDbHelper extends SQLiteOpenHelper {
    private static MemoDbHelper sInstance;
    // DB version set 1, and if every time schema changes, version up.
    private static final int DB_VERSION = 1;
    // DB name.
    private static final String DB_NAME = "Memo.db";
    // Sentences to create table.
    private static final String SQL_CREATE_ENTRIES = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT)",
            MemoContract.MemoEntry.TABLE_NAME,
            MemoContract.MemoEntry._ID,
            MemoContract.MemoEntry.COLUMN_NAME_TITLE,
            MemoContract.MemoEntry.COLUMN_NAME_CONTENTS);
    // Sentences to delete table.
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " +MemoContract.MemoEntry.TABLE_NAME;

    private MemoDbHelper(Context context) {
        // Local location is made in smartphone.
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Make singleton pattern.
    // Because multi-threading setting.
    public static synchronized MemoDbHelper getInstance(Context context) {
        if(sInstance == null) {
            sInstance = new MemoDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Make table.
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    // If Schema version is updated, called this callback method.
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Do backup every time schema change,
        // After delete table, re-produce and restore.
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
