package org.androidtown.imagesearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_main_image.view.*
import org.androidtown.imagesearch.model.Document

class ImageViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_main_image, parent, false)
) {
    fun onBind(documentList: ArrayList<Document>, position: Int, callEvent: CallEvent) {

        val lastDocumentPosition = documentList.size -1

        //스크롤을 내릴 때 마지막 포지션일 경우, 다음 페이지를 읽어들이기 위해 onFinishScroll 시행
        if(position == lastDocumentPosition){
            callEvent.onFinishScroll(position)
        }

        itemView.run {
            //뷰홀더 아이템 뷰에 이미지 load95yj
            Glide.with(context).load(documentList[position].thumbnail_url).into(item_main_image_view)

            //해당 아이템뷰 클릭 시, onClickImage 에 position 넘겨줌 (상세보기 뷰 구현을 위해)
            setOnClickListener {
                callEvent.onClickImage(position)
            }
        }
    }
}