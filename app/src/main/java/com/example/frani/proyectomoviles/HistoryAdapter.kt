package com.example.frani.proyectomoviles

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.EditText
import android.widget.TextView

class HistoryAdapter(private val historyList: List<History>) : RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {

    private var position: Int = 0
    private var context: Context? = null

    fun getPosition(): Int {
        return position
    }

    fun setPosition(position: Int) {
        this.position = position
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {

        var id: TextView
        var text: EditText
        var translatedText: EditText
        lateinit var history: History

        init {
            id = view.findViewById(R.id.txt_1) as TextView
            text = view.findViewById(R.id.editTextFromRecycler) as EditText
            translatedText = view.findViewById(R.id.editTextToRecycler) as EditText
            view.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu?.add(Menu.NONE, R.id.item_menu_share, Menu.NONE, R.string.menu_share)
            menu?.add(Menu.NONE, R.id.item_menu_delete, Menu.NONE, R.string.menu_delete)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_layout, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val history = historyList[position]
        val entry = position + 1
        holder.id.text = "${context?.getString(R.string.entry)} $entry"
        holder.text.setText(history.text)
        holder.translatedText.setText(history.translatedText)
        holder.itemView.setOnLongClickListener {
            setPosition(holder.adapterPosition)
            false
        }
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

}