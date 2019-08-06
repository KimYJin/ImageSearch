package org.androidtown.imagesearch.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import org.androidtown.imagesearch.ImageRecyclerViewAdapter
import org.androidtown.imagesearch.databinding.ActivityMainBinding
import org.androidtown.imagesearch.viewmodel.MainViewModel
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.detail_image_view.view.*
import org.androidtown.imagesearch.CallOnClickImage
import org.androidtown.imagesearch.R
import org.androidtown.imagesearch.databinding.DetailImageViewBinding
import org.androidtown.imagesearch.model.Document

class MainActivity : AppCompatActivity(), CallOnClickImage {


    private lateinit var binding: ActivityMainBinding

    private val viewModel = MainViewModel()

    //전체 이미지리스트
    private lateinit var documentList: ArrayList<Document>

    //현재/첫번째/마지막 이미지의 포지션
    private var currentPosition: Int = 0
    private var firstPosition: Int = 0
    private var lastPosition: Int = 0

    //팝업 생성 여부
    private var isPopUp = false

    //정렬 방식
    private var sort = "accuracy"

    //상세보기 뷰
    private val detailView by lazy {
        (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.detail_image_view, null)
    }

    //리사이클러뷰 어댑터
    private val imageRecyclerViewAdapter by lazy { ImageRecyclerViewAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initView()
    }

    /**
     * 뷰 초기화
     */
    private fun initView() {

        setSupportActionBar(binding.mainToolbar)

        //그리드 레이아웃 매니저
        val gridLayoutManager = GridLayoutManager(this, 6)

        //리사이클러뷰 초기화
        binding.imageRecyclerview.run {
            adapter = imageRecyclerViewAdapter
            layoutManager = gridLayoutManager
            setHasFixedSize(true)
        }

        // 포지션 별로 다른 SpanSize 를 차지하도록 아이템 뷰 배치
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position % 5 < 2) 3 else 2
            }
        }

        //뷰모델의 documentLiveData 가 바뀌면, 리사이클러뷰 어댑터에 넘겨줌
        viewModel.documentLiveData.observe(this, Observer { documents ->
            imageRecyclerViewAdapter.setItems(documents)
            this.documentList = documents
        })

        //검색 버튼 클릭 -> 뷰모델에 검색결과 요청
        binding.searchBtn.setOnClickListener {
            onClickSearchBtn()
        }

        //상세보기 뷰에서 X 버튼 클릭 -> 상세보기 뷰 닫기
        detailView.exit_btn.setOnClickListener {
            removeDetailView()
        }

        //상세보기 뷰에서 해당 이미지 클릭 -> 해당 이미지의 doc_url 로 연결
        detailView.detail_image_view.setOnClickListener {
            ContextCompat.startActivity(this, Intent(Intent.ACTION_VIEW, Uri.parse(documentList[currentPosition].doc_url)), null)
        }

        //상세보기 뷰에서 왼쪽 화살표 클릭 -> 이전 이미지를 띄움
        detailView.arrow_left_button.setOnClickListener {
            currentPosition -= 1
            onClickLeftRightBtn()
        }

        //상세보기 뷰에서 오른쪽 화살표 클릭 -> 다음 이미지를 띄움
        detailView.arrow_right_button.setOnClickListener {
            currentPosition += 1
            onClickLeftRightBtn()
        }
    }

    /**
     * 검색 버튼 클릭 시, 뷰모델에 이미지 검색 명령
     */
    private fun onClickSearchBtn() {

        //키보드 내리기
        closeKeyboard()

        //스크롤 최상단으로 올리기
        binding.imageRecyclerview.layoutManager!!.scrollToPosition(0)

        //상세보기 뷰가 떠있는 경우, 상세보기 뷰 제거
        if (isPopUp) {
            removeDetailView()
        }

        //뷰모델에 이미지 검색 명령
        if (binding.searchEdittxt.text.toString() != "") {
            viewModel.getImageSearch(binding.searchEdittxt.text.toString(), sort)
        } else {
            Toast.makeText(this, "검색어를 입력해주세요", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 특정 이미지 클릭 시, 상세보기 뷰에 해당 이미지를 load
     * @param position ImageViewHolder
     */
    override fun onClickImage(position: Int) {
        //클릭한 이미지의 정보 초기화
        this.currentPosition = position
        this.lastPosition = documentList.size - 1

        //상세보기 뷰를 띄우고, 해당 이미지를 load
        addDetailView()
        Glide.with(this).load(documentList[position].image_url).into(detailView.detail_image_view)
    }

    /**
     * 상세보기 뷰를 띄움
     */
    private fun addDetailView() {
        isPopUp = true
        binding.imageRecyclerview.visibility = View.GONE
        binding.detailImageContainer.run {
            visibility = View.VISIBLE
            addView(detailView)
        }
        onClickLeftRightBtn()
    }

    /**
     * 상세보기 뷰를 제거
     */
    private fun removeDetailView() {
        isPopUp = false
        binding.detailImageContainer.removeAllViews()
        binding.imageRecyclerview.visibility = View.VISIBLE
    }

    /**
     * [다음/이전 이미지]를 Glide를 사용해 load
     * 현재 포지션에 따라 오른쪽/왼쪽 화살표 버튼의 visibility 변경
     */
    private fun onClickLeftRightBtn() {
        when (currentPosition) {
            firstPosition -> {
                detailView.arrow_left_button.visibility = View.GONE
                detailView.arrow_right_button.visibility = View.VISIBLE
            }

            lastPosition -> {
                detailView.arrow_left_button.visibility = View.VISIBLE
                detailView.arrow_right_button.visibility = View.GONE
            }

            else -> {
                detailView.arrow_left_button.visibility = View.VISIBLE
                detailView.arrow_right_button.visibility = View.VISIBLE
            }
        }
        Glide.with(this).load(documentList[currentPosition].image_url).into(detailView.detail_image_view)
    }

    /**
     * 상세보기 뷰가 띄워진 상태에서 백버튼 클릭시, 상세보기 뷰 제거
     */
    override fun onBackPressed() {
        if (isPopUp) {
            removeDetailView()
        } else {
            super.onBackPressed()
        }
    }

    /**
     * 키보드 내리기
     */
    private fun closeKeyboard() {
        this.currentFocus?.let { view ->
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}