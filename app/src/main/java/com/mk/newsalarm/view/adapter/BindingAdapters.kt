package com.mk.newsalarm.view.adapter

import android.util.Log
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.mk.newsalarm.R


@BindingAdapter("imageUrl")
fun bindImage(imageView: ImageView,url:String?){
    url?.let {
        val uri = url.toUri().buildUpon().scheme("https").build()
        Log.i("TAG",uri.toString())
        Glide.with(imageView.context)
            .load(uri)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_baseline_broken_image_24)
            .into(imageView)
    }
}