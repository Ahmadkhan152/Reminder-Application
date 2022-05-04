package com.example.remainder

import android.app.*
import android.content.Context
import android.os.Bundle
import android.view.View
import java.util.*
import android.content.Intent
import android.os.Build
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.remainder.db.dbSQLite
import java.text.SimpleDateFormat

class DateTimeActivity : AppCompatActivity() {

    private var textview_date: TextView? = null
    private var textview_time: TextView? = null
    lateinit var alarmManager:AlarmManager
    lateinit var pendingIntent:PendingIntent
    lateinit var btnClearData:Button
    lateinit var btnUpdateData:Button
    lateinit var btnSaveData:Button
    lateinit var etEvent:AutoCompleteTextView
    var cal:Calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datetime)
        createNotificationChannel()
        var toolBar:Toolbar=findViewById(R.id.toolBar)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        textview_date = findViewById(R.id.text_view_date_1)
        textview_time = findViewById(R.id.text_view_time_1)
        btnClearData=findViewById(R.id.btnClearData)
        btnSaveData=findViewById(R.id.btnSaveData)
        btnUpdateData=findViewById(R.id.btnUpdateData)
        etEvent=findViewById(R.id.etEvent)
        val idFromActivity: Int? =intent.getIntExtra(_ID,-1)
        val timeFromActivity: String? =intent.getStringExtra(TIME)
        val dateFromActivity: String? =intent.getStringExtra(DATE)
        val eventFromActivity: String? =intent.getStringExtra(EVENT)
        if (idFromActivity!=-1)
        {
            btnSaveData.setVisibility(View.INVISIBLE)
            textview_time?.text = timeFromActivity
            textview_date?.text = dateFromActivity
            etEvent.setText(eventFromActivity)
            btnUpdateData.setVisibility(View.VISIBLE)
            btnUpdateData.setOnClickListener {
                var myDB:dbSQLite = dbSQLite(this@DateTimeActivity)
                myDB.writableDatabase
                var strData:String= textview_date?.text as String
                var strTime:String= textview_time?.text as String
                var strEvent:String= etEvent.text.toString()
                if (strData!="" && strTime!="")
                {
                    myDB.updateEntry(idFromActivity,strData,strTime,strEvent)
                    val shifter=Intent()
                    shifter.putExtra(DATE,strData)
                    shifter.putExtra(TIME,strTime)
                    shifter.putExtra(EVENT,strEvent)
                    setResult(Activity.RESULT_OK,shifter)
                    finish()
                    Toast.makeText(this@DateTimeActivity, SUCCESSFULL,Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this@DateTimeActivity, EMPTY_FIELDS,Toast.LENGTH_SHORT).show()
                }
            }
        }
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }
        textview_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@DateTimeActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }

        })
        textview_time!!.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                textview_time!!.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        }
        if (idFromActivity==-1)
        {
            btnSaveData.setVisibility(View.VISIBLE)
            btnSaveData.setOnClickListener{
                val myDB= dbSQLite(this)
                if (textview_date?.text.toString().length!= 0 && textview_time?.text.toString().length!= 0)
                {
                    setAlarm()
                    var strEventText:String=etEvent.text.toString()
                    var strData:String= textview_date?.text as String
                    var strTime:String= textview_time?.text as String
                    myDB.insertData(strData,strTime,strEventText)
                    val shifter=Intent()
                    shifter.putExtra(DATE,strData)
                    shifter.putExtra(TIME,strTime)
                    shifter.putExtra(EVENT,strEventText)
                    setResult(Activity.RESULT_OK,shifter)
                    finish()
                }
                else
                    Toast.makeText(this@DateTimeActivity,EMPTY_FIELDS,Toast.LENGTH_SHORT).show()
        }}
        btnClearData.setOnClickListener {
            textview_date?.text = ""
            textview_time?.text = ""
            etEvent.text = null
        }
    }

    private fun setAlarm() {
        alarmManager=getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent:Intent= Intent(this@DateTimeActivity,AlarmReceiver::class.java)
        pendingIntent=PendingIntent.getBroadcast(this,0,intent,0)
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,cal.timeInMillis,AlarmManager.INTERVAL_DAY,pendingIntent)
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
        textview_date!!.text = sdf.format(cal.getTime())
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}