package uz.urgench.blog

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DataBase(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {

    //при создании БД
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "create table $TABLE_BLOGS"+"($KEY_ID integer primary key,$KEY_TEXT_NAME text,$KEY_TEXT text,$KEY_USER text)"
        )
    }
    //При обновлении БД
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("MyTag","UPGRADE!!!!!!!")
        db?.execSQL("drop table if exists "+TABLE_BLOGS)
        onCreate(db)
    }

    //Что то типо static в java
    companion object{
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "blogDB"
        const val TABLE_BLOGS = "blogs"

        const val KEY_ID = "id"
        const val KEY_TEXT_NAME = "text_name"
        const val KEY_TEXT = "text"
        const val KEY_USER = "user"
    }
}