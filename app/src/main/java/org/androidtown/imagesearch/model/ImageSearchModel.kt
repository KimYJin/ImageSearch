package org.androidtown.imagesearch.model

import android.graphics.Color
import io.reactivex.Single

class ImageSearchModel(private val service:APIService){

    private val REST_API_KEY = "f81f0fdcbef3e47beca18a2ad6e8eeeb"

    fun getData(query:String, sort:SortEnum, page: Int, size: Int): Single<ImageSearchResponse> {
        return service.searchImage(auth = "KakaoAK $REST_API_KEY", query = query, sort = sort.sort, page = page, size = size)
    }


}
