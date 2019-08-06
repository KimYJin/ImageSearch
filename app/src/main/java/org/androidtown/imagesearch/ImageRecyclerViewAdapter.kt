package org.androidtown.imagesearch

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.androidtown.imagesearch.model.Document

class ImageRecyclerViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var documentDataList = ArrayList<Document>()
    private lateinit var callOnClickImage:CallOnClickImage

    constructor(callOnClickImage: CallOnClickImage):this(){
        this.callOnClickImage = callOnClickImage
    }

    fun setItems(documentData: ArrayList<Document>) {
        this.documentDataList = documentData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = ImageViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ImageViewHolder)?.onBind(documentDataList, position, callOnClickImage)
    }

    override fun getItemCount() = documentDataList.size
}