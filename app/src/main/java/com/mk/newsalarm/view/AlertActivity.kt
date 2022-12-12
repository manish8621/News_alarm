package com.mk.newsalarm.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.mk.newsalarm.R
import com.mk.newsalarm.databinding.ActivityAlertBinding
import com.mk.newsalarm.service.AlertService

class AlertActivity : AppCompatActivity() {
    lateinit var binding:ActivityAlertBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.stopBtn.setOnClickListener{
            finish()
        }
    }

    private fun stopAlertService(){
        Intent(this, AlertService::class.java).also {
            stopService(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlertService()
    }
}