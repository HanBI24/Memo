package hello.world.study;

import android.provider.BaseColumns;

/**
 * Contract class
 * Have one table
 */

public class MemoContract {
    // No instance
    private MemoContract() {

    }

    // Table information define to InnerClass
    public static class MemoEntry implements BaseColumns {
        // No extends (final)
        public static final String TABLE_NAME = "memo";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENTS = "contents";
    }

}
