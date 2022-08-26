package com.example.remainder

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remainder.dataclass.ModelClass
import com.example.remainder.database.DataBaseSQLite

class HomeActivity : AppCompatActivity() , RecyclerInterface{
    lateinit var tvNoRecord:TextView
    lateinit var btnAddData:TextView
    lateinit var myDB:DataBaseSQLite
    lateinit var toolbar:Toolbar
    lateinit var recyclerView:RecyclerView
    lateinit var recyclerViewAdapter:MyRecyclerViewAdapter
    lateinit var data:ArrayList<ModelClass>
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result:ActivityResult? ->

        if (result?.resultCode == Activity.RESULT_OK)
        {
            data.clear()
            data=myDB.readData()
            if (data.count()>0)
                tvNoRecord.text = ""
            recyclerViewAdapter=MyRecyclerViewAdapter(this,data,this)
            recyclerView.adapter=recyclerViewAdapter
            recyclerViewAdapter.notifyDataSetChanged()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        val recyclerInterface:RecyclerInterface
        toolbar = findViewById(R.id.toolBar)
        setSupportActionBar(toolbar)
        btnAddData=findViewById(R.id.btnAddData)
        tvNoRecord=findViewById(R.id.tvNoRecordFound)
        recyclerView=findViewById(R.id.recyclerView)
        myDB = DataBaseSQLite(this)
        data=myDB.readData()
        if (data.isEmpty())
            tvNoRecord.text= NO_RECORD
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerViewAdapter=MyRecyclerViewAdapter(this,data,this)
        recyclerView.adapter=recyclerViewAdapter
        //Click Listener
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.status_bar_color)
        }
        btnAddData.setOnClickListener {
            val shifter=Intent(this@HomeActivity,GetDateAndTimeActivity::class.java)
            getContent.launch(shifter)
        }
    }

    override fun displayNoRecord() {
        tvNoRecord.text = NO_RECORD
    }

    override fun getContent(shifter:Intent) {
        getContent.launch(shifter)
    }
}