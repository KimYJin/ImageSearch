package org.androidtown.imagesearch.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.androidtown.imagesearch.databinding.ItemMainImageBinding
import org.androidtown.imagesearch.model.Document

class ImageViewHolder(private val binding: ItemMainImageBinding, private val context: Context) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun newInstance(parent: ViewGroup): ImageViewHolder {
            return ImageViewHolder(
                ItemMainImageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                parent.context
            )
        }
    }

    fun onBind(documentData: Document) {
        documentData.run {
            Picasso.with(context).load(documentData.image_url).into(binding.itemMainImageView)
        }
    }
}