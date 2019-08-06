package org.androidtown.imagesearch

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.androidtown.imagesearch.model.Document

class ImageRecyclerViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var documentDataList = ArrayList<Document>()
    private lateinit var callOnClick:CallOnClick

    constructor(callOnClick: CallOnClick):this(){
        this.callOnClick = callOnClick
    }

    fun setItems(documentData: ArrayList<Document>) {
        this.documentDataList = documentData
        notifyDataSetChanged()
    }

    //factory 패턴 이용해 객체 생성해서 넘김
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = ImageViewHolder.newInstance(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ImageViewHolder)?.onBind(documentDataList,position, callOnClick)
    }

    override fun getItemCount() = documentDataList.size
}