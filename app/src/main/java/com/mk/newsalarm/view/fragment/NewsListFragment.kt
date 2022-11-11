package com.mk.newsalarm.view.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.speech.tts.Voice
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import com.mk.newsalarm.MainActivity
import com.mk.newsalarm.R
import com.mk.newsalarm.databinding.FragmentNewsListBinding
import com.mk.newsalarm.model.api.States
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

        val adapter = NewsAdapter()
        binding.recyclerView.adapter = adapter

        adapter.setOnClickListener {
            Toast.makeText(context, "speak", Toast.LENGTH_SHORT).show()
        }

        adapter.setReadMoreOnClickListener { news ->

            speak("Hello World")
//            Intent(android.content.Intent.ACTION_VIEW).also {
//                it.data = Uri.parse(news.url)
//                startActivity(it)
//            }
            findNavController().navigate(NewsListFragmentDirections.actionNewsListFragmentToWebviewFragment(news.url))
        }

        adapter.setSpeakOnClickListener { news,imgBtn ->
            if(imgBtn.tag=="ready"){
                imgBtn.tag = "speaking";
                imgBtn.setImageResource(R.drawable.ic_baseline_speaker_phone_24)
                speak(news.content)
            }
            else{
                imgBtn.tag = "ready";
                imgBtn.setImageResource(R.drawable.ic_baseline_speaker_24)
                tts.stop()

            }
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