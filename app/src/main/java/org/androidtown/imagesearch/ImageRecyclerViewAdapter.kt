package org.androidtown.imagesearch

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.androidtown.imagesearch.model.Document

class ImageRecyclerViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var documentDataList = ArrayList<Document>()
    private lateinit var callOnClickImage:CallEvent

    constructor(callOnClickImage: CallEvent):this(){
        this.callOnClickImage = callOnClickImage
    }

    fun setItems(documentDataList: ArrayList<Document>) {
        this.documentDataList = documentDataList
        notifyDataSetChanged()
    }

    /**
     * 아이템 뷰를 위한 뷰홀더 객체 생성
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = ImageViewHolder(parent)

    /**
     * position 에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ImageViewHolder)?.onBind(documentDataList, position, callOnClickImage)
    }

    /**
     * 	@return 전체 아이템 개수
     */
    override fun getItemCount() = documentDataList.size
}