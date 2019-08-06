package org.androidtown.imagesearch

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.androidtown.imagesearch.databinding.ItemMainImageBinding
import org.androidtown.imagesearch.model.Document

class ImageViewHolder(private val binding: ItemMainImageBinding, private val context: Context) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun newInstance(parent: ViewGroup): ImageViewHolder {
            return ImageViewHolder(
                ItemMainImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                , parent.context)
        }
    }

    fun onBind(documentList:ArrayList<Document>, position:Int, callOnClick: CallOnClick) {
        binding.itemMainImageView.run{
            Glide.with(context).load(documentList[position].image_url).into(this)
            setOnClickListener {
                callOnClick.onImageClick(documentList,position)
            }
        }
    }
}