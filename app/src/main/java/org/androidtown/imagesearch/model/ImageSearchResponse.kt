package org.androidtown.imagesearch.model

import java.io.Serializable

data class ImageSearchResponse(
    var documents: ArrayList<Document>,
    var meta: Meta
) : Serializable

data class Document(
    var collection: String,
    var thumbnail_url: String,
    var image_url: String,
    var width: Int,
    var height: Int,
    var display_sitename: String,
    var doc_url: String,
    var datetime: String
) : Serializable

data class Meta(
    var total_count: Int,
    var pageable_count: Int,
    var is_end: Boolean
) : Serializable


