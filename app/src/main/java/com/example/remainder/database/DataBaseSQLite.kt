package com.example.remainder.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build.ID
import android.widget.Toast
import com.example.remainder.*
import com.example.remainder.dataclass.ModelClass

class DataBaseSQLite(var context: Context):SQLiteOpenHelper(context, DATABASE_NAME,null,1) {
    lateinit var modelClass: ModelClass
    var modelClassArray=ArrayList<ModelClass>()
    fun insertData(strDate:String, strTime:String, strEvent:String){
        val db=writableDatabase
        val contentValues=ContentValues()
        contentValues.put(DATE,strDate)
        contentValues.put(TIME,strTime)
        contentValues.put(EVENT,strEvent)
        val insertResult=db.insert(TABLE_NAME,ID,contentValues)
        if(insertResult<0)
            Toast.makeText(context, ERRORINRECORD,Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context, SUCCESSFULL,Toast.LENGTH_SHORT).show()
    }
    fun readData():ArrayList<ModelClass>
    {
        val db=readableDatabase
        val cursor:Cursor=db.rawQuery(READ_TABLE_QUERY, null)
        val size=cursor.count
        var columnIndexID=0
        var columnIndexDate=0
        var columnIndexTime=0
        var columnIndexEvent=0
        var id=0
        var date=""
        var time=""
        var event=""
        cursor.moveToFirst()
        for (counter in 0 until size)
        {
            columnIndexID =cursor.getColumnIndex(_ID)
            columnIndexDate=cursor.getColumnIndex(DATE)
            columnIndexTime=cursor.getColumnIndex(TIME)
            columnIndexEvent=cursor.getColumnIndex(EVENT)
            id=cursor.getInt(columnIndexID)
            date=cursor.getString(columnIndexDate)
            time=cursor.getString(columnIndexTime)
            event=cursor.getString(columnIndexEvent)
            modelClass = ModelClass(id,date,time,event)
            modelClassArray.add(modelClass)
            cursor.moveToNext()
        }
        return modelClassArray
    }
    fun deleteEntry(id:Int){
        val db=writableDatabase
        Toast.makeText(context,DELETE_RECORD,Toast.LENGTH_SHORT).show()
        db?.execSQL(DELETE_QUERY+id)
    }
    fun updateEntry(id:Int?,date:String?,time:String?,event:String?){
        val db=writableDatabase
        val contValues:ContentValues= ContentValues()
        contValues.put(DATE,date)
        contValues.put(TIME,time)
        contValues.put(EVENT,event)
        val result=db.update(TABLE_NAME,contValues,"$_ID = $id", null)
        if (result==-1)
            Toast.makeText(context, ERRORINRECORD,Toast.LENGTH_SHORT).show()
    }
    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(createTableQuery)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

        p0?.execSQL(DROP_TABLE_QUERY)
        onCreate(p0)
    }
}