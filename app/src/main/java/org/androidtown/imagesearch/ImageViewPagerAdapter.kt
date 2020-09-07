package org.androidtown.imagesearch

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import org.androidtown.imagesearch.databinding.ViewpagerImageBinding
import org.androidtown.imagesearch.model.Document

class ImageViewPagerAdapter : PagerAdapter() {

    private var documentDataList = ArrayList<Document>()

    /**
     * 이미지 데이터 리스트를 업데이트해준다.
     *
     * @param documentData 이미지 데이터 리스트
     */
    fun setItems(documentData: ArrayList<Document>) {
        this.documentDataList = documentData
        notifyDataSetChanged()
    }

    /**
     * item 뷰를 상위뷰에 추가후 item 뷰를 리턴한다.
     * 이미지를 로드하고, 클릭시 이벤트를 발생한다.
     *
     * @param container item 그려줄 상위 뷰
     * @param position 현재 아이템 포지션
     *
     * @return inflate 하여 가져온 뷰 리턴
     */
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = ViewpagerImageBinding.inflate(LayoutInflater.from(container.context), container, false)

        view.detailImageView.run {
            Glide.with(context)
                .load(documentDataList[position].image_url)
                .into(this)

            //이미지를 클릭하면, 웹 브라우저로 해당 이미지의 문서 URL 을 연다.
            setOnClickListener {
                ContextCompat.startActivity(
                    context,
                    Intent(Intent.ACTION_VIEW, Uri.parse(documentDataList[position].doc_url)),
                    null
                )
            }
        }

        container.addView(view.root)
        return view.root
    }

    /**
     * item 뷰를 제거한다.
     *
     * @param container item 삭제한 상위 뷰
     * @param position 현재 아이템 포지션
     * @param _object 지울 뷰
     */
    override fun destroyItem(container: ViewGroup, position: Int, _object: Any) {
        container.removeView(_object as View)
    }

    /**
     * 뷰 페이저 키 객체와 페이지뷰가 연관되는지 여부 확인
     * instantiateItem 리턴 객체와 비교한다.
     *
     * @param view instantiateItem 에서 넘어온 뷰
     * @param _object 뷰 페이저 키 객체
     *
     * @return 비교 값 (같으면 true 다르면 false)
     */
    override fun isViewFromObject(view: View, _object: Any): Boolean = (view == _object as View)

    /**
     * 전체 아이템 크기를 리턴한다.
     *
     * @return 전체 아이템 전체 사이즈
     */
    override fun getCount(): Int = documentDataList.size
}