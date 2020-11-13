package page.chungjungsoo.easyqrcheck.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class MyCookieDatabaseHelper(context: Context)
    : SQLiteOpenHelper(context,
    DB_NAME, null, DB_VERSION) {

    companion object {
        private val DB_NAME = "CookieDB"
        private val DB_VERSION = 1
        private val ID = "id"
        private val TABLE_NAME = "cookielist"
        private val NID_JKL = "jkl_cookie"
        private val NID_AUT = "aut_cookie"
        private val NID_SES = "ses_cookie"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
                "CREATE TABLE $TABLE_NAME" +
                "($ID INTEGER PRIMARY KEY, $NID_JKL TEXT, $NID_AUT TEXT, $NID_SES TEXT)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) { }

    fun addCookies(jkl: String, aut: String, ses: String) : Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(NID_JKL, jkl)
        values.put(NID_AUT, aut)
        values.put(NID_SES, ses)

        val success = db.insert(TABLE_NAME, null, values)
        db.close()
        return (Integer.parseInt("$success") != -1)
    }

    fun updateCookies(jkl: String, aut: String, ses: String) : Boolean {
        return true
    }

    fun getCookies() : String {
        val db = readableDatabase
        val selectALLQuery = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectALLQuery, null)
        val jkl : String
        val aut : String
        val ses : String
        var result = ""

        if (cursor == null) {
            Log.d("DATABASE_NULL", "CURSOR IS NULL")
            return result
        }

        if (cursor.moveToFirst()) {
                jkl = cursor.getString(cursor.getColumnIndex(NID_JKL))
                aut = cursor.getString(cursor.getColumnIndex(NID_AUT))
                ses = cursor.getString(cursor.getColumnIndex(NID_SES))
                result = "NID_JKL=$jkl; NID_AUT=$aut; NID_SES=$ses;"
        }
        else {
            Log.e("DATABASE_ERROR", "GETTING COOKIES HAS FAILED.")
            result = ""
        }
        cursor.close()
        db.close()

        return result
    }

    fun deleteCookies() : Boolean {
        val db = this.writableDatabase
        val query = db.rawQuery("DELETE FROM $TABLE_NAME", null)
        val num = query.count
        query.close()
        db.close()
        return num == 0
    }


}