package org.androidtown.imagesearch.model

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

//query:검색어, sort:문서정렬방식, page:결과페이지번호, size:한 페이지에 보여질 문서 개수
interface APIService {

    @GET("/v2/search/image")
    fun searchImage(@Header("Authorization") auth:String, @Query("query") query:String, @Query("sort") sort:String, @Query("page") page:Int, @Query("size") size:Int
    ): Single<ImageSearchResponse>
}