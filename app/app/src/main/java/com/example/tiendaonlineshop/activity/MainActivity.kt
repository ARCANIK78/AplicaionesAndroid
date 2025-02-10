package com.example.tiendaonlineshop.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.tiendaonlineshop.SliderAdapter
import com.example.tiendaonlineshop.ViewModel.MaiViewModel
import com.example.tiendaonlineshop.databinding.ActivityIntroBinding
import com.example.tiendaonlineshop.databinding.ActivityMainBinding
import com.example.tiendaonlineshop.model.sliderModel

class MainActivity : AppCompatActivity() {
    private val viewModel = MaiViewModel()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBanner()
    }
    private fun initBanner(){
        binding.progressBarBanner.visibility= View.VISIBLE
            viewModel.banners.observe(this, Observer{items->
                banners(items)
                binding.progressBarBanner.visibility=View.GONE
            })
        viewModel.loadBanners()
    }
    private fun banners(images:List<sliderModel>){
        binding.viewpagerSIider.adapter=SliderAdapter(images,binding.viewpagerSIider)
        binding.viewpagerSIider.clipToPadding=false
        binding.viewpagerSIider.clipChildren=false
        binding.viewpagerSIider.offscreenPageLimit=3
        binding.viewpagerSIider.getChildAt(0).overScrollMode=RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer=CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
        }
        binding.viewpagerSIider.setPageTransformer(compositePageTransformer)
        if (images.size>1){
            binding.dotlndicator.visibility=View.VISIBLE
            binding.dotlndicator.attachTo(binding.viewpagerSIider)
        }
    }
}