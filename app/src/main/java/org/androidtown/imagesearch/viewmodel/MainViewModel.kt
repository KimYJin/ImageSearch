package org.androidtown.imagesearch.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.androidtown.imagesearch.model.APIService
import org.androidtown.imagesearch.model.ImageSearchModel
import org.androidtown.imagesearch.model.ImageSearchResponse
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel(): ViewModel() {

    val imageSearchResponseLiveData = MutableLiveData<ImageSearchResponse>()
    private val compositeDisposable = CompositeDisposable()

    private val service: APIService = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://dapi.kakao.com")
        .build()
        .create(APIService::class.java)

    private val model:ImageSearchModel=ImageSearchModel(service)

    // RxJava - Observing
    fun getImageSearch(query: String, sort:String) {
        addDisposable(model.getData(query, sort)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.run {
                    if (documents.size > 0) {
                        Log.d("MainViewModel", "documents : $documents")
                        imageSearchResponseLiveData.postValue(this)
                    }
                    Log.d("MainViewModel", "meta : $meta")
                }
            }, {
                Log.d("MainViewModel", "response error, message : ${it.message}")
            }))
    }

    private fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

}