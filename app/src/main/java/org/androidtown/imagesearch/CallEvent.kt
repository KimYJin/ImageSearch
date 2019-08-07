package org.androidtown.imagesearch

interface CallEvent {
   fun onClickImage(position:Int)
   fun onFinishScroll(position: Int)
}