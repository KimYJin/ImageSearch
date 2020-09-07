package org.androidtown.imagesearch.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import org.androidtown.imagesearch.databinding.ActivityMainBinding
import org.androidtown.imagesearch.viewmodel.MainViewModel
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.detail_image_view.*
import kotlinx.android.synthetic.main.detail_image_view.view.*
import kotlinx.android.synthetic.main.toolbar.view.*
import org.androidtown.imagesearch.*
import org.androidtown.imagesearch.model.Document
import org.androidtown.imagesearch.model.SortEnum

class MainActivity : AppCompatActivity(), CallEvent {

    private lateinit var binding: ActivityMainBinding

    private val viewModel = MainViewModel()

    //전체 이미지리스트
    private var documentList = ArrayList<Document>()

    //현재/첫번째/마지막 이미지의 포지션
    private var currentPosition: Int = 0
    private var firstPosition: Int = 0
    private var lastPosition: Int = 0

    //팝업 생성 여부
    private var isPopUp = false

    //viewModel.getImageSearch 에 넘겨줄 parameter
    private var searchWord = ""
    private var page = 1
    private var size = 20
    private var sort = SortEnum.Accuracy

    //상세보기 뷰
    private val detailView by lazy {
        (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.detail_image_view, null)
    }

    //리사이클러뷰 어댑터
    private val imageRecyclerViewAdapter by lazy { ImageRecyclerViewAdapter(this) }

    //뷰페이저 어댑터
    private val imageViewPagerAdapter by lazy { ImageViewPagerAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initView()
    }

    /**
     * 뷰 초기화
     */
    private fun initView() {

        //툴바
        setSupportActionBar(main_toolbar.toolbar)

        //정확도, 최신 버튼 초기화
        binding.sortAccuracyTextBtn.isSelected = true
        binding.sortAccuracyTextBtn.isClickable = false
        binding.sortRecencyTextBtn.isSelected = false

        //그리드 레이아웃 매니저
        val gridLayoutManager = GridLayoutManager(this, 6)

        //리사이클러뷰 초기화
        binding.imageRecyclerview.run {
            adapter = imageRecyclerViewAdapter
            layoutManager = gridLayoutManager
            setHasFixedSize(true)
        }

        //뷰페이저 초기화
        detailView.vp_image.run {
            adapter = imageViewPagerAdapter
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                /**
                 * 페이지가 변경되고 확정이 될때 이벤트 발생
                 * 현재 위치 값을 바꾸고, 이미지 이동버튼 속성을 변경한다.
                 *
                 * @param position 현재 선태된 페이지 위치
                 */
                override fun onPageSelected(position: Int) {
                    currentPosition = position
                    onClickLeftRightBtn()
                }
            })
        }

        // 포지션 별로 다른 SpanSize 를 차지하도록 아이템 뷰 배치
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position % 5 < 2) 3 else 2
            }
        }

        /**
         * 뷰모델의 documentLiveData 가 바뀌면, 리사이클러뷰 어댑터에 넘겨줌
         */
        viewModel.documentLiveData.observe(this, Observer { documents ->
            if (documents.size == 0) {
                Toast.makeText(this, "검색결과가 없습니다.", Toast.LENGTH_SHORT).show()
            }
                documentList = documents

                imageRecyclerViewAdapter.setItems(documentList)
        })

        /**
         * 뷰모델의 nextDocumentLiveData 바뀌면,
         * 리사이클러뷰 어댑터에 다음 페이지의 이미지 리스트가 추가된 리스트 넘겨줌
         * * */
        viewModel.nextDocumentLiveData.observe(this, Observer { nextDocuments ->
            documentList.addAll(nextDocuments)
            imageRecyclerViewAdapter.setItems(documentList)
        })

        //검색 버튼 클릭 -> 뷰모델에 검색결과 요청
        binding.searchBtn.setOnClickListener {
            onClickSearchBtn()
        }

        //상세보기 뷰에서 X 버튼 클릭 -> 상세보기 뷰 닫기
        detailView.exit_btn.setOnClickListener {
            removeDetailView()
        }

        //상세보기 뷰에서 왼쪽 화살표 클릭 -> 이전 이미지를 띄움
        detailView.arrow_left_button.setOnClickListener {
            currentPosition -= 1
            vp_image.currentItem = currentPosition
        }

        //상세보기 뷰에서 오른쪽 화살표 클릭 -> 다음 이미지를 띄움
        detailView.arrow_right_button.setOnClickListener {
            currentPosition += 1
            vp_image.currentItem = currentPosition
        }

        //최신 클릭 시
        binding.sortRecencyTextBtn.setOnClickListener {
            onClickSortRecencyBtn()
        }

        //정확도 클릭 시
        binding.sortAccuracyTextBtn.setOnClickListener {
            onClickSortAccuracyBtn()
        }

        //스크롤 최상단 바로가기 버튼 클릭 시
        binding.topButton.setOnClickListener {
            //스크롤 최상단으로 올리기
            binding.imageRecyclerview.layoutManager!!.scrollToPosition(0)
        }

        //디바이스 키보드 엔터시 검색
        binding.searchEdittxt.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onClickSearchBtn()
                handled = true
            }
            handled
        }
    }


    /**
     * 검색 버튼 클릭 시, 뷰모델에 이미지 검색 명령
     */
    private fun onClickSearchBtn() {

        //키보드 내리기
        onCloseKeyboard()

        //스크롤 최상단으로 올리기
        binding.imageRecyclerview.layoutManager!!.scrollToPosition(0)

        //상세보기 뷰가 떠있는 경우, 상세보기 뷰 제거
        if (isPopUp) {
            removeDetailView()
        }

        //검색창에 검색어가 있으면,
        if (binding.searchEdittxt.text.toString() != "") {

            //뷰모델에 이미지 검색 명령
            searchWord = binding.searchEdittxt.text.toString()
            viewModel.getImageSearch(searchWord, sort, 1, size) //결과 페이지 번호 page=1
            Log.d("MainActivity", "searchWord: $searchWord")

            //스크롤 최상단 바로가기 버튼 활성화
            binding.topButton.visibility = View.VISIBLE

        } else {    //검색창에 검색어가 없으면 검색어를 입력하라는 토스트 메세지 띄우기
            Toast.makeText(this, "검색어를 입력해주세요", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 다음 페이지의 이미지 데이터 추가를 viewModel 에 명령
     */
    override fun onFinishScroll(position: Int) {

        // meta 의 is_end 가 false 이고, 최대 page(50) 미만인 경우, 다음 페이지의 이미지 데이터 추가를 viewModel 에 명령
        if (!viewModel.metaData.is_end && page < 50) {
            viewModel.getImageSearch(searchWord, sort, page++, size)
        }
    }

    /**
     * 특정 이미지 클릭 시, 상세보기 뷰에 해당 이미지를 load
     *
     * @param position 클릭한 아이템뷰의 position 을 ImageViewHolder 로 부터 넘겨 받음
     */
    override fun onClickImage(position: Int) {
        //클릭한 이미지의 정보 초기화
        this.currentPosition = position
        this.lastPosition = documentList.size - 1

        //상세보기 뷰를 띄우고, 해당 이미지를 load
        addDetailView()

        imageViewPagerAdapter.setItems(documentList)
        detailView.vp_image.currentItem = currentPosition
    }

    /**
     * 상세보기 뷰를 띄움
     */
    private fun addDetailView() {
        isPopUp = true
        binding.imageRecyclerview.visibility = View.GONE
        binding.topButton.visibility = View.GONE
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
        binding.topButton.visibility = View.VISIBLE
    }

    /**
     * 정확도 버튼 클릭시, 정확도 순으로 검색 수행
     */
    private fun onClickSortAccuracyBtn() {
        binding.sortAccuracyImageBtn.visibility = View.VISIBLE
        binding.sortAccuracyTextBtn.isSelected = true
        binding.sortAccuracyTextBtn.isClickable = false

        binding.sortRecencyImageBtn.visibility = View.INVISIBLE
        binding.sortRecencyTextBtn.isSelected = false
        binding.sortRecencyTextBtn.isClickable = true

        sort = SortEnum.Accuracy
        onClickSearchBtn()
    }

    /**
     * 최신 버튼 클릭시, 최신 순으로 검생 수행
     */
    private fun onClickSortRecencyBtn() {
        binding.sortAccuracyImageBtn.visibility = View.INVISIBLE
        binding.sortAccuracyTextBtn.isSelected = false
        binding.sortAccuracyTextBtn.isClickable = true

        binding.sortRecencyImageBtn.visibility = View.VISIBLE
        binding.sortRecencyTextBtn.isSelected = true
        binding.sortRecencyTextBtn.isClickable = false

        sort = SortEnum.Recency
        onClickSearchBtn()
    }

    /**
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
    private fun onCloseKeyboard() {
        this.currentFocus?.let { view ->
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}