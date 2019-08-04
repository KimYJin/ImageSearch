package org.androidtown.imagesearch.view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.androidtown.imagesearch.model.Document

class ImageRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var documentData = ArrayList<Document>()

    fun setItems(documentData: ArrayList<Document>) {
        this.documentData = documentData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = ImageViewHolder.newInstance(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ImageViewHolder)?.onBind(documentData[position])
    }

    override fun getItemCount() = documentData.size
}