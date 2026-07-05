package dev.fg.buildyourownx.libs.my_room

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.provider.BaseColumns
import androidx.activity.ComponentActivity
import kotlin.system.measureNanoTime

class MyRoomActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getCreateTableQuery(DBUser::class)

//        val dbHelper: MyDbHelper
//        measureNanoTime {
//            dbHelper = MyDbHelper(this)
//        }.also { println("Initialization of dbHelper took $it nanos, or ${it/1_000_000F} ms") }
//
//        /** Put information into a database */
////        // Gets the data repository in write mode
////        val db: SQLiteDatabase
////        measureNanoTime {
////            db = dbHelper.writableDatabase
////        }.also { println("Initialization of writableDatabase took $it nanos, or ${it/1_000_000F} ms") }
////
////// Create a new map of values, where column names are the keys
////        val values = ContentValues().apply {
////            put(MyEntryContract.MyEntry.COLUMN_NAME_TITLE, "test title")
////            put(MyEntryContract.MyEntry.COLUMN_NAME_SUBTITLE, "test subtitle")
////        }
////
////        measureNanoTime {
////// Insert the new row, returning the primary key value of the new row
////            val newRowId = db.insert(MyEntryContract.MyEntry.TABLE_NAME, null, values)
////        }.also { println("Insertion took $it nanos, or ${it/1_000_000F} ms") }
//
//
//        /** Read information from a database */
//        val db = dbHelper.readableDatabase
//
//// Define a projection that specifies which columns from the database
//// you will actually use after this query.
//        val projection = arrayOf(BaseColumns._ID, MyEntryContract.MyEntry.COLUMN_NAME_TITLE, MyEntryContract.MyEntry.COLUMN_NAME_SUBTITLE)
//
//// Filter results WHERE "title" = 'My Title'
//        val selection = "${MyEntryContract.MyEntry.COLUMN_NAME_TITLE} = ?"
//        val selectionArgs = arrayOf("My Title")
//
//// How you want the results sorted in the resulting Cursor
//        val sortOrder = "${MyEntryContract.MyEntry.COLUMN_NAME_SUBTITLE} DESC"
//
//        val cursor = db.query(
//            MyEntryContract.MyEntry.TABLE_NAME,   // The table to query
//            projection,             // The array of columns to return (pass null to get all)
//            selection,              // The columns for the WHERE clause
//            selectionArgs,          // The values for the WHERE clause
//            null,                   // don't group the rows
//            null,                   // don't filter by row groups
//            sortOrder               // The sort order
//        )
//
//        val itemIds = mutableListOf<Long>()
//        with(cursor) {
//            while (moveToNext()) {
//                val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
//                val itemTitle = getLong(getColumnIndexOrThrow(MyEntryContract.MyEntry.COLUMN_NAME_TITLE))
//                val itemSubtitle = getLong(getColumnIndexOrThrow(MyEntryContract.MyEntry.COLUMN_NAME_SUBTITLE))
//                itemIds.add(itemId)
//            }
//        }
//        cursor.close()
////
//        /**Delete information from a database*/
//
//        // Define 'where' part of query.
//        val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} LIKE ?"
//// Specify arguments in placeholder order.
//        val selectionArgs = arrayOf("MyTitle")
//// Issue SQL statement.
//        val deletedRows = db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, selection, selectionArgs)
//
//
//        /**Update a database*/
//        val db = dbHelper.writableDatabase
//
//// New value for one column
//        val title = "MyNewTitle"
//        val values = ContentValues().apply {
//            put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, title)
//        }
//
//// Which row to update, based on the title
//        val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} LIKE ?"
//        val selectionArgs = arrayOf("MyOldTitle")
//        val count = db.update(
//            FeedReaderContract.FeedEntry.TABLE_NAME,
//            values,
//            selection,
//            selectionArgs)

    }
}