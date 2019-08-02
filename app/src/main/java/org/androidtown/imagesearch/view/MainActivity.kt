package org.androidtown.imagesearch.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import org.androidtown.imagesearch.R
import org.androidtown.imagesearch.databinding.ActivityMainBinding
import org.androidtown.imagesearch.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initView()
    }

    private fun initView() {
        setSupportActionBar(main_toolbar)

        viewModel.imageSearchResponseLiveData.observe(this, Observer {
            Log.d("View:MainActivity", it.documents.toString())
        })

        binding.searchBtn.setOnClickListener{
            viewModel.getImageSearch(binding.searchEdittxt.text.toString(),"accuracy")
        }
    }
}
