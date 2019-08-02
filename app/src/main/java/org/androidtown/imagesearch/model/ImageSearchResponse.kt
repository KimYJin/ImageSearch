package org.androidtown.imagesearch.model

data class ImageSearchResponse(var documents:ArrayList<Document>, var meta:Meta) {
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

