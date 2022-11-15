package com.mk.newsalarm.view.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.mk.newsalarm.MainActivity
import com.mk.newsalarm.databinding.FragmentNewsListBinding
import com.mk.newsalarm.model.domain.DomainModel
import com.mk.newsalarm.model.isNetworkAvailable
import com.mk.newsalarm.view.adapter.NewsAdapter
import com.mk.newsalarm.viewmodel.NewsListViewModel
import java.util.*

class NewsListFragment : Fragment() ,OnInitListener{

    private lateinit var viewModel: NewsListViewModel
    private lateinit var binding: FragmentNewsListBinding
    private lateinit var tts: TextToSpeech


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[NewsListViewModel::class.java]
        binding = FragmentNewsListBinding.inflate(inflater,container,false)
        tts = TextToSpeech(requireActivity() as MainActivity,this)
        binding.lifecycleOwner = viewLifecycleOwner


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = NewsAdapter().also {

            it.setOnClickListeners(object:NewsAdapter.ClickListener{

                override fun onReadMoreOnClicked(news: DomainModel.News) {
                    findNavController().navigate(NewsListFragmentDirections.actionNewsListFragmentToWebviewFragment(news.url))
                }

                override fun onSpeakerClicked(news: DomainModel.News, speakBtn: ImageButton) {
                    speakBtn.animate().scaleX(1.3F).scaleY(1.3F).setDuration(500L).withEndAction {
                        speakBtn.animate().scaleX(1F).scaleY(1F).setDuration(500L).start()
                    }.start()
                    if(speakBtn.tag=="ready"){
                        speakBtn.tag = "speaking";
                        speak(news.content)
                    }
                    else{
                        speakBtn.tag = "ready";
                        tts.stop()
                    }
                }

            })
        }

        binding.recyclerView.also{
            it.adapter = adapter
        }

        viewModel.newsList.observe(viewLifecycleOwner){
            adapter.submitList(it)
            hideLoading()
        }
        binding.reloadBtn.setOnClickListener{
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
            loadNews()
        }

        loadNews()


    }

    fun loadNews(){

        if(isNetworkAvailable(requireContext()))
        {
            viewModel.loadNews()
            hideReloadState()
            showLoading()
        }
        else{
            hideLoading()
            showReloadState()
            Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show()
        }
    }
    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showReloadState() {
        binding.reloadBtn.visibility = View.VISIBLE
    }
    private fun hideReloadState() {
        binding.reloadBtn.visibility = View.GONE
    }


    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS)
        {
            val result = tts.setLanguage(Locale.US)
            tts.setSpeechRate(0.8F)
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            {
                Toast.makeText(context, "tts not supported", Toast.LENGTH_SHORT).show()
            }
            else
            {
                //enable button
            }
        }
    }
    fun speak(text:String){
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }

    override fun onStop() {
        super.onStop()
        tts.stop()
        tts.shutdown()
    }
}