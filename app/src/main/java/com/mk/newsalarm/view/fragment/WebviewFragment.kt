package com.mk.newsalarm.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.mk.newsalarm.R
import com.mk.newsalarm.databinding.FragmentHomeBinding
import com.mk.newsalarm.databinding.FragmentWebviewBinding
import com.mk.newsalarm.model.isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class WebviewFragment : Fragment() {
    lateinit var binding: FragmentWebviewBinding
    var url = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWebviewBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //
        binding.reloadBtn.setOnClickListener{
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
            loadPage()
        }
        binding.progressBar.setOnClickListener{
            Toast.makeText(context, binding.webView.progress.toString(), Toast.LENGTH_SHORT).show()
        }

        val args : WebviewFragmentArgs by navArgs()
        url = args.url
        loadPage()

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

    private fun loadPage() {
        if(isNetworkAvailable(requireContext()))
        {
            binding.webView.loadUrl(url)
            hideReloadState()
            showLoading()

            //to hide loading view after page load
            lifecycleScope.launch(){
                while (isNetworkAvailable(requireContext()))
                {
                    if (binding.webView.progress == 100) {
                        withContext(Dispatchers.Main) { hideLoading() }
                    }
                    delay(1000L)
                }
            }
        }
        else{
            hideLoading()
            showReloadState()
            Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show()
        }
    }
}