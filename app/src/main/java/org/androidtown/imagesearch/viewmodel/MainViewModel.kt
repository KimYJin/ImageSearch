package org.androidtown.imagesearch.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.androidtown.imagesearch.model.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel : ViewModel() {

    val documentLiveData = MutableLiveData<ArrayList<Document>>()
    val nextDocumentLiveData = MutableLiveData<ArrayList<Document>>()

    lateinit var metaData: Meta

    private val compositeDisposable = CompositeDisposable()

    private val service: APIService = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://dapi.kakao.com")
        .build()
        .create(APIService::class.java)

    private val model: ImageSearchModel = ImageSearchModel(service)

    /**
     * RxJava Observing
     * 모델에 요청해서 이미지 검색결과 데이터를 받아, LiveData 갱신
     */
    fun getImageSearch(query: String, sort: SortEnum, page: Int, size: Int) {
        addDisposable(
            model.getData(query, sort, page, size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.run {
                        metaData = meta     //해당 메소드 호출 전에 meta.is_end 검사를 위해 값 넣어줌.
                        Log.d("MainViewModel", "meta : $meta")
                        Log.d("MainViewModel", "documents : $documents")

                        if (page > 1) {  // 다음 페이지의 데이터를 가져온 경우, nextDocumentLiveData 를 갱신
                            nextDocumentLiveData.postValue(documents)
                        } else {    //첫 번째 검색 결과 페이지인 경우, documentLiveData 를 갱신
                            documentLiveData.postValue(documents)
                        }
                    }
                }, {
                    Log.d("MainViewModel", "response error, message : ${it.message}")
                })
        )
    }

    private fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}

