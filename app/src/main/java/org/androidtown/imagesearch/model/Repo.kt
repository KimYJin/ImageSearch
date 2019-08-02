package org.androidtown.imagesearch.model

//total count:검색된 문서 수
// page_count: 노출가능 문서 수,
// is_end:현재 페이지가 마지막 페이지인지 여부. 값이 false이면 page를 증가시켜 다음 페이지를 요청할 수 있음.

data class Repo(var documents:ArrayList<Document>, var meta:Meta) {
    data class Document(
        var collection:String,
        var datetime:String,
        var display_sitename:String,
        var doc_url:String,
        var height:Int,
        var image_url:String,
        var thumbnail_url:String,
        var width:Int
    )
    data class Meta(
        var total_count:Int,
        var pageable_count:Int,
        var is_end:Boolean
    )
}