package com.example.remainder

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remainder.db.dbSQLite

class RecyclerviewActivity : AppCompatActivity() , RecyclerInterface{
    lateinit var tvNoRecord:TextView
    lateinit var btnAddData:TextView
    lateinit var myDB:dbSQLite
    lateinit var toolBar:androidx.appcompat.widget.Toolbar
    lateinit var myRecyclerView:RecyclerView
    lateinit var myAdapter:MyRecyclerViewAdapter
    lateinit var data:ArrayList<Data>
    val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result:ActivityResult? ->

        if (result?.resultCode == Activity.RESULT_OK)
        {
            data.clear()
            data=myDB.readData()
            if (data.count()>0)
                tvNoRecord.text = ""
            myAdapter=MyRecyclerViewAdapter(this,data,this)
            myRecyclerView.adapter=myAdapter
            myAdapter.notifyDataSetChanged()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        val recyclerInterface:RecyclerInterface
        var toolBar: androidx.appcompat.widget.Toolbar =findViewById(R.id.toolBar)
        setSupportActionBar(toolBar)
        btnAddData=findViewById(R.id.btnAddData)
        btnAddData.setOnClickListener {
            val shifter=Intent(this@RecyclerviewActivity,DateTimeActivity::class.java)
            getContent.launch(shifter)
        }
        tvNoRecord=findViewById(R.id.tvNoRecordFound)
        myDB = dbSQLite(this)
        data=myDB.readData()
        if (data.count()<1)
            tvNoRecord.setText(NO_RECORD)
        myRecyclerView=findViewById(R.id.recyclerView)
        myRecyclerView.layoutManager=LinearLayoutManager(this)
        myAdapter=MyRecyclerViewAdapter(this,data,this)
        myRecyclerView.adapter=myAdapter
    }

    override fun displayNoRecord() {
        tvNoRecord.text = NO_RECORD
    }

    override fun getContent(shifter:Intent) {
        getContent.launch(shifter)

    }
}