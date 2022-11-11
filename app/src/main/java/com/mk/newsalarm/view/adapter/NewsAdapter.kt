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

    var clickListener:((news:DomainModel.News)->Unit)? = null
    var readMoreClickListener:((news:DomainModel.News)->Unit)? = null
    var speakClickListener:((news:DomainModel.News,imageButton:ImageButton)->Unit)? = null

    class ItemViewHolder(private val binding:NewsItemLayoutBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(news: DomainModel.News
                 ,clickListener:((news:DomainModel.News)->Unit)?
                 ,readMoreClickListener:((news:DomainModel.News)->Unit)?
                 ,speakClickListener:((news:DomainModel.News,imageButton:ImageButton)->Unit)?){
            binding.news = news
            clickListener?.let {
                binding.speakerIb.setOnClickListener{ _->
                    it.invoke(news)
                }
            }
            readMoreClickListener?.let {
                binding.readMoreIb.setOnClickListener{ _->
                    it.invoke(news)
                }
            }
            speakClickListener?.let {
                binding.speakerIb.setOnClickListener{ _->
                    it.invoke(news,binding.speakerIb)
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
        holder.bind(news = getItem(position), clickListener = clickListener, readMoreClickListener = readMoreClickListener,speakClickListener)
    }
    fun setOnClickListener(clickListener: ((news:DomainModel.News)->Unit))
    {
        this.clickListener = clickListener
    }
    fun setReadMoreOnClickListener(readMoreClickListener:((news:DomainModel.News)->Unit)){
        this.readMoreClickListener = readMoreClickListener
    }
    fun setSpeakOnClickListener(speakClickListener:((news:DomainModel.News,imageButton:ImageButton)->Unit)){
        this.speakClickListener = speakClickListener
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