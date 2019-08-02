package org.androidtown.imagesearch.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.androidtown.imagesearch.R
import org.androidtown.imagesearch.databinding.ActivityMainBinding
import org.androidtown.imagesearch.model.APIService
import org.androidtown.imagesearch.model.Repo
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initView()
    }

    private fun initView() {
        setSupportActionBar(main_toolbar)

        val service: APIService = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://dapi.kakao.com")
            .build()
            .create(APIService::class.java)

        val repos: Single<Repo> = service.searchImage("KakaoAK f81f0fdcbef3e47beca18a2ad6e8eeeb", "설현", "accuracy", 1, 80)

        repos.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //handle success
                Log.d("Success message", it.documents.toString())
            }, {
                //handle fail
                Log.d("Fail message",it.message)
            })

    }
}
