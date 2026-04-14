package com.example.ucbrowser.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ucbrowser.R
import com.example.ucbrowser.model.DownloadItem

class DownloadAdapter(
    private val downloadList: List<DownloadItem>,
    private val onItemClick: (DownloadItem) -> Unit
) : RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder>() {

    class DownloadViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        val tvFileName: TextView = view.findViewById(R.id.tvFileName)
        val tvFileSize: TextView = view.findViewById(R.id.tvFileSize)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        val btnAction: ImageView = view.findViewById(R.id.btnAction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_download, parent, false)
        return DownloadViewHolder(view)
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        val download = downloadList[position]
        holder.tvFileName.text = download.fileName
        holder.tvFileSize.text = "${download.getFileSizeString()} • ${download.getProgress()}%"
        holder.progressBar.progress = download.getProgress()
        
        when (download.status) {
            DownloadItem.DownloadStatus.COMPLETED -> {
                holder.progressBar.visibility = View.GONE
            }
            DownloadItem.DownloadStatus.DOWNLOADING -> {
                holder.progressBar.visibility = View.VISIBLE
            }
            else -> {
                holder.progressBar.visibility = View.VISIBLE
            }
        }
        
        holder.itemView.setOnClickListener {
            onItemClick(download)
        }
    }

    override fun getItemCount(): Int = downloadList.size
}
