package com.example.remainder

import android.app.*
import android.content.Context
import android.os.Bundle
import java.util.*
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.getSystemService
import com.example.remainder.database.DataBaseSQLite
import java.text.SimpleDateFormat

class GetDateAndTimeActivity : AppCompatActivity() {

    lateinit var tvDate: TextView
    lateinit var tvTime: TextView
    lateinit var btnClearData:Button
    lateinit var btnUpdateData:Button
    lateinit var btnSaveData:Button
    lateinit var etEvent:AutoCompleteTextView
    lateinit var alarmManager:AlarmManager
    lateinit var pendingIntent:PendingIntent
    lateinit var toolbar:Toolbar
    lateinit var calendar:Calendar
    lateinit var strEvent:String
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datetime)
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.status_bar_color)
        }
        createNotificationChannel()
        toolbar=findViewById(R.id.toolBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable._173002_alarm_alert_bell_internet_notice_icon)
        tvDate = findViewById(R.id.tvDatePicker)
        tvTime = findViewById(R.id.tvTimePicker)
        btnClearData=findViewById(R.id.btnClear)
        btnSaveData=findViewById(R.id.btnSave)
        btnUpdateData=findViewById(R.id.btnUpdateData)
        etEvent=findViewById(R.id.etEvent)
        calendar=Calendar.getInstance()
        val strID: Int? =intent.getIntExtra(_ID,-1)
        val strTime: String? =intent.getStringExtra(TIME)
        val strDate: String? =intent.getStringExtra(DATE)
        strEvent= intent.getStringExtra(EVENT).toString()
        if (strID!=-1)
        {
            btnSaveData.visibility=View.INVISIBLE
            strTime.also { tvTime.text = it }
            strDate.also { tvDate.text=it }
            etEvent.setText(strEvent)
            btnUpdateData.visibility=View.VISIBLE
            btnUpdateData.setOnClickListener {
                val dataBaseSQLite:DataBaseSQLite = DataBaseSQLite(this@GetDateAndTimeActivity)
                dataBaseSQLite.writableDatabase
                val strData= tvDate.text as String
                val strTime = tvTime.text as String
                strEvent = etEvent.text.toString()
                if (strData.isNotEmpty() && strTime.isNotEmpty())
                {
                    dataBaseSQLite.updateEntry(strID,strData,strTime,strEvent)
                    val shifter=Intent()
                    shifter.putExtra(DATE,strData)
                    shifter.putExtra(TIME,strTime)
                    shifter.putExtra(EVENT,strEvent)
                    setResult(Activity.RESULT_OK,shifter)
                    finish()
                    Toast.makeText(this@GetDateAndTimeActivity, SUCCESSFULL,Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this@GetDateAndTimeActivity, EMPTY_FIELDS,Toast.LENGTH_SHORT).show()
                }
            }
        }
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        tvDate!!.setOnClickListener(View.OnClickListener {
                DatePickerDialog(this@GetDateAndTimeActivity,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()

        })
        tvTime!!.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                tvTime!!.text = SimpleDateFormat("HH:mm").format(calendar.time)
            }
            TimePickerDialog(this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }
        if (strID==-1)
        {
            btnSaveData.visibility=View.VISIBLE
            btnSaveData.setOnClickListener{
                val dataBaseSQLite= DataBaseSQLite(this)
                if (tvDate?.text.isNotEmpty() && tvTime?.text.isNotEmpty())
                {
                    strEvent=etEvent.text.toString()
                    setAlarm()
                    var strData:String= tvDate?.text as String
                    var strTime:String= tvTime?.text as String
                    dataBaseSQLite.insertData(strData,strTime,strEvent)
                    val shifter=Intent()
                    shifter.putExtra(DATE,strData)
                    shifter.putExtra(TIME,strTime)
                    shifter.putExtra(EVENT,strEvent)
                    setResult(Activity.RESULT_OK,shifter)
                    finish()
                }
                else {
                    Toast.makeText(this@GetDateAndTimeActivity,EMPTY_FIELDS,Toast.LENGTH_SHORT).show()
                }
        }}
        btnClearData.setOnClickListener {
            tvDate?.text = ""
            tvTime?.text = ""
            etEvent.text = null
        }
    }

    private fun setAlarm() {
        alarmManager=getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent:Intent= Intent(this@GetDateAndTimeActivity,AlarmReceiver::class.java)
        intent.putExtra(EVENT,strEvent)
        pendingIntent=PendingIntent.getBroadcast(this,0,intent,0)
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),AlarmManager.INTERVAL_DAY,pendingIntent)
        //alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(1L,pendingIntent),pendingIntent)
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            var name:CharSequence=CHANNEL_NAME
            val importance:Int=NotificationManager.IMPORTANCE_HIGH
            val channel:NotificationChannel= NotificationChannel(CHANNEL_ID,name,importance)
            channel.description= CHANNEL_DESCRIPTION
            val notificationManager:NotificationManager= getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        tvDate!!.text = sdf.format(calendar.getTime())
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}