package com.mk.newsalarm.view.adapter

import android.content.DialogInterface.OnClickListener
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mk.newsalarm.databinding.NewsItemLayoutBinding
import com.mk.newsalarm.model.domain.DomainModel

class NewsAdapter:ListAdapter<DomainModel.News,NewsAdapter.ItemViewHolder>(DiffUtilItemCallback()) {

    var clickListener : ClickListener? = null
    class ItemViewHolder(private val binding:NewsItemLayoutBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(news: DomainModel.News
                 ,clickListener:ClickListener?
                 ) {
            binding.news = news
            clickListener?.let {

                binding.readMoreIb.setOnClickListener { _ ->
                    it.onReadMoreOnClicked(news)
                }
                binding.speakerIb.setOnClickListener { _ ->
                    it.onSpeakerClicked(news, binding.speakerIb)
                }
            }
        }

        companion object{
            fun from(parent: ViewGroup): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = NewsItemLayoutBinding.inflate(layoutInflater,parent,false)
                return ItemViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(news = getItem(position), clickListener = clickListener)
    }

    fun setOnClickListeners(clickListener: ClickListener){
        this.clickListener = clickListener
    }
    interface ClickListener{
        fun onReadMoreOnClicked(news: DomainModel.News)
        fun onSpeakerClicked(news: DomainModel.News,speakBtn:ImageButton)
    }
}
class DiffUtilItemCallback():DiffUtil.ItemCallback<DomainModel.News>(){
    override fun areItemsTheSame(oldItem: DomainModel.News, newItem: DomainModel.News): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: DomainModel.News, newItem: DomainModel.News): Boolean {
        return oldItem == newItem
    }
}

