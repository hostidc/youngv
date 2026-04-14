package com.example.ucbrowser.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ucbrowser.R
import com.example.ucbrowser.model.TabItem

class TabAdapter(
    private val tabs: MutableList<TabItem>,
    private val onTabClick: (TabItem) -> Unit,
    private val onTabClose: (TabItem) -> Unit
) : RecyclerView.Adapter<TabAdapter.TabViewHolder>() {

    class TabViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvUrl: TextView = view.findViewById(R.id.tvUrl)
        val btnClose: ImageView = view.findViewById(R.id.btnClose)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tab, parent, false)
        return TabViewHolder(view)
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        val tab = tabs[position]
        holder.tvTitle.text = tab.title
        holder.tvUrl.text = tab.url
        
        holder.itemView.setOnClickListener {
            onTabClick(tab)
        }
        
        holder.btnClose.setOnClickListener {
            onTabClose(tab)
        }
    }

    override fun getItemCount(): Int = tabs.size
    
    fun addTab(tab: TabItem) {
        tabs.add(tab)
        notifyItemInserted(tabs.size - 1)
    }
    
    fun removeTab(tab: TabItem) {
        val position = tabs.indexOfFirst { it.id == tab.id }
        if (position != -1) {
            tabs.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    
    fun updateTab(tab: TabItem) {
        val position = tabs.indexOfFirst { it.id == tab.id }
        if (position != -1) {
            tabs[position] = tab
            notifyItemChanged(position)
        }
    }
}
