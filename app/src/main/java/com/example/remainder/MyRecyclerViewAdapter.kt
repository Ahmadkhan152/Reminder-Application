package com.example.remainder

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.remainder.dataclass.ModelClass
import com.example.remainder.database.DataBaseSQLite

class MyRecyclerViewAdapter(var context: Context, val data:ArrayList<ModelClass>, val recyclerInterface:RecyclerInterface): RecyclerView.Adapter<MyRecyclerViewAdapter.viewholder>() {
    class viewholder(itemView:View):RecyclerView.ViewHolder(itemView)
    {
        val tvDate:TextView=itemView.findViewById(R.id.tvDate)
        val tvTime:TextView=itemView.findViewById(R.id.tvTime)
        val tvEvent:TextView=itemView.findViewById(R.id.tvEvent)
        val tvNoRecord:TextView?=itemView.findViewById(R.id.tvNoRecordFound)
        val ivEdit:ImageView=itemView.findViewById(R.id.ivEdit)
        val ivDelete:ImageView=itemView.findViewById(R.id.ivDelete)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {

        var myView=LayoutInflater.from(context).inflate(R.layout.viewlayout,parent,false)
        return viewholder(myView);
    }
    override fun onBindViewHolder(holder: viewholder, position: Int) {
        var id =0
        id = data[position].id
        holder.tvDate.text = data[position].date
        holder.tvTime.text = data[position].time
        holder.tvEvent.text = data[position].event
        holder.ivDelete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Deleted")
            builder.setMessage("Do You Want To Continue")
            builder.setPositiveButton("Yes") { a, b ->
                deleteEntry(position, id)
                if (position == 0 && data.size==0) {
                    Toast.makeText(context, NO_RECORD, Toast.LENGTH_SHORT).show()
                    recyclerInterface.displayNoRecord()
                }
            }
            builder.setNegativeButton("No",DialogInterface.OnClickListener { dialogInterface, i ->  })
            builder.show()}
        holder.ivEdit.setOnClickListener{
            val shifter:Intent= Intent(context,GetDateAndTimeActivity::class.java)
            val id=data[position].id
            val date=data[position].date
            val time=data[position].time
            val event=data[position].event
            shifter.putExtra(_ID,id)
            shifter.putExtra(DATE,date)
            shifter.putExtra(TIME,time)
            shifter.putExtra(EVENT,event)
            recyclerInterface.getContent(shifter)
        }
    }
    override fun getItemCount(): Int {
        return data.size
    }
    fun deleteEntry(postion:Int,id:Int)
    {
        var myDB:DataBaseSQLite = DataBaseSQLite(context)
        myDB.writableDatabase
        myDB.deleteEntry(id)
        myDB.readData()
        data.removeAt(postion)
        notifyDataSetChanged()
    }
}