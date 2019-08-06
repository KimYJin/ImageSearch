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
import org.androidtown.imagesearch.CallOnClick
import org.androidtown.imagesearch.R
import org.androidtown.imagesearch.model.Document

class MainActivity : AppCompatActivity(), CallOnClick {

    private lateinit var binding: ActivityMainBinding

    private val viewModel = MainViewModel()

    //이미지 Url
    private var docUrl: String = ""

    //팝업 생성 여부
    private var isPopUp = false

    //현재위치
    private var currentPosition: Int = 0

    //전체 이미지리스트
    private lateinit var documentList: ArrayList<Document>
    private var documentListSize: Int = 0
    private var firstDocumentPosition: Int = 0
    private var lastDocumentPosition: Int = 0

    //상세 이미지 띄울 뷰
    private val detailView by lazy {
        (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(org.androidtown.imagesearch.R.layout.detail_image_view, null)
    }

    //이미지 리사이클러뷰 어댑터
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

        val gridLayoutManager = GridLayoutManager(this, 6)

        //리사이클러뷰 초기화
        binding.imageRecyclerview.run {
            adapter = imageRecyclerViewAdapter
            layoutManager = gridLayoutManager
            setHasFixedSize(true)
        }

        //그리드 레이아웃 매니저 크기별 관리 (2칸 3칸)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position % 5 < 2) 3 else 2
            }
        }

        //if documentLiveData of ViewModel is changed, set documents to adapter
        viewModel.documentLiveData.observe(this, Observer { documents ->
            imageRecyclerViewAdapter.setItems(documents)
        })

        //searchBtn click -> order ImageSearchResult to ViewModel
        binding.searchBtn.setOnClickListener {
            onClickSearchBtn()
        }

        //상세보기->x버튼 클릭-> 상세보기 뷰 닫기
        detailView.exit_btn.setOnClickListener {
            removeDetailView()
        }

        //상세보기->이미지클릭-> 해당 이미지의 doc_Url로 연결
        detailView.detail_image_view.setOnClickListener {
            ContextCompat.startActivity(this, Intent(Intent.ACTION_VIEW, Uri.parse(docUrl)), null)
        }

        //상세보기 왼쪽 화살표
        detailView.arrow_left_button.setOnClickListener {
            onClickLeftBtn()
        }

        //상세보기 오른쪽 화살표
        detailView.arrow_right_button.setOnClickListener {
            onClickRightBtn()
        }
    }

    /**
     * 상세 페이지뷰를 만든다.
     */
    private fun initDetailView() {
        isPopUp = true
        binding.imageRecyclerview.visibility = View.GONE
        binding.detailImageContainer.run {
            visibility = View.VISIBLE
            addView(detailView)
        }
        onCheckLRBtn()
    }

    /**
     * 상세 페이지 뷰를 없앤다.
     */
    private fun removeDetailView() {
        isPopUp = false
        binding.detailImageContainer.removeAllViews()
        binding.imageRecyclerview.visibility = View.VISIBLE
    }

    /**
     * 검색 버튼 이벤트
     */
    private fun onClickSearchBtn() {
        //키보드 내리기
        onCloseKeyboard()

        //스크롤 상단으로
        binding.imageRecyclerview.layoutManager!!.scrollToPosition(0)

        //상세페이지가 떠있는 경우
        if (isPopUp) {
            removeDetailView()
        }

        //검색어를 입력할 경우
        if (binding.searchEdittxt.text.toString() != "") {
            viewModel.getImageSearch(binding.searchEdittxt.text.toString(), "accuracy")
        } else {
            Toast.makeText(this, "검색어를 입력해주세요", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 키보드 내리는 이벤트
     */
    private fun onCloseKeyboard() {
        this.currentFocus?.let { view ->
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * 이미지 클릭시
     */
    override fun onImageClick(documentList: ArrayList<Document>, position: Int) {
        initDoc(documentList, position)

        //상세페이지 뷰 초기화
        initDetailView()

        //이미지 변경
        Glide.with(this).load(documentList[position].image_url).into(detailView.detail_image_view)
    }

    /**
     * 이미지에 대한 정보 초기화
     */
    private fun initDoc(documentList: ArrayList<Document>, position: Int) {
        this.documentList = documentList
        this.docUrl = documentList[position].doc_url
        this.currentPosition = position
        this.documentListSize = documentList.size
        this.lastDocumentPosition = documentList.size - 1
    }

    /**
     * 현재 위치에 따른 오른쪽 왼쪽 이동 버튼 속성 변경
     */
    private fun onCheckLRBtn() {
        when (currentPosition) {
            firstDocumentPosition -> {
                detailView.arrow_left_button.visibility = View.GONE
                detailView.arrow_right_button.visibility = View.VISIBLE
            }

            lastDocumentPosition -> {
                detailView.arrow_left_button.visibility = View.VISIBLE
                detailView.arrow_right_button.visibility = View.GONE
            }

            else -> {
                detailView.arrow_left_button.visibility = View.VISIBLE
                detailView.arrow_right_button.visibility = View.VISIBLE
            }
        }

        Glide.with(this).load(documentList[currentPosition].image_url).into(detailView.detail_image_view)
        docUrl = documentList[currentPosition].doc_url
    }

    /**
     * 왼쪽 버튼 이벤트
     */
    private fun onClickLeftBtn() {
        currentPosition -= 1
        onCheckLRBtn()
    }

    /**
     * 오른쪽 버튼이벤트
     */
    private fun onClickRightBtn() {
        currentPosition += 1
        onCheckLRBtn()
    }

    /**
     * 뒤로가기 버튼 이벤트
     */
    override fun onBackPressed() {
        if (isPopUp) {
            removeDetailView()
        } else {
            super.onBackPressed()
        }
    }
}
