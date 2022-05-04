package com.example.remainder.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build.ID
import android.widget.Toast
import com.example.remainder.*

class dbSQLite(var context: Context):SQLiteOpenHelper(context, DATABASE_NAME,null,1) {
    var data=ArrayList<Data>()
    fun insertData(remainderDate:String,remainderTime:String,remainderEvent:String){
        val db=writableDatabase
        val contValues:ContentValues=ContentValues()
        contValues.put(DATE,remainderDate)
        contValues.put(TIME,remainderTime)
        contValues.put(EVENT,remainderEvent)
        val insertResult=db.insert(TABLE_NAME,ID,contValues)
        if(insertResult<0)
            Toast.makeText(context, ERRORINRECORD,Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context, SUCCESSFULL,Toast.LENGTH_SHORT).show()
    }
    fun readData():ArrayList<Data>
    {
        val db=readableDatabase
        var allData:Cursor=db.rawQuery(READ_TABLE_QUERY, null)
        var size=allData.count
        allData.moveToFirst()
        var x=0;
        while (x<size)
        {
            val columnData0 =allData.getColumnIndex(_ID)
            val columnData1=allData.getColumnIndex(DATE)
            val columnData2=allData.getColumnIndex(TIME)
            val columnData3=allData.getColumnIndex(EVENT)
            var id:Int=allData.getInt(columnData0)
            var date=allData.getString(columnData1)
            var time=allData.getString(columnData2)
            var event=allData.getString(columnData3)
            var obj:Data=Data(id,date,time,event)
            data.add(obj)
            allData.moveToNext()
            x++
        }
        return data
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