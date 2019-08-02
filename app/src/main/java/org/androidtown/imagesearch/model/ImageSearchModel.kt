package org.androidtown.imagesearch.model

import io.reactivex.Single

class ImageSearchModel(private val service:APIService){

    private val REST_API_KEY = "REST_API_KEY"

    fun getData(query:String, sort:String ): Single<ImageSearchResponse> {
        return service.searchImage(auth = "KakaoAK $REST_API_KEY", query = query, sort = sort, page = 1, size = 80)
    }
}
