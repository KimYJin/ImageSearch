package org.androidtown.imagesearch

import org.androidtown.imagesearch.model.Document

interface CallOnClick {
   // fun onImageClick(eachDocument:Document)
   fun onImageClick(documentList:ArrayList<Document>, position:Int)
}