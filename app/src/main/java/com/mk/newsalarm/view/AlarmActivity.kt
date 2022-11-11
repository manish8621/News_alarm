package com.mk.newsalarm.view

import android.media.Ringtone
import android.media.RingtoneManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.mk.newsalarm.databinding.ActivityAlarmBinding
import com.mk.newsalarm.model.domain.DomainModel
import com.mk.newsalarm.model.isNetworkAvailable
import com.mk.newsalarm.viewmodel.AlarmViewModel
import java.util.*
//TODO: close activity after alarm finished news


class AlarmActivity : AppCompatActivity(),OnInitListener {
    lateinit var viewModel: AlarmViewModel
    lateinit var binding: ActivityAlarmBinding
    lateinit var tts: TextToSpeech
    lateinit var ringtone :Ringtone
    lateinit var iterator: kotlin.collections.ListIterator<DomainModel.News>


    var ttsAvailable = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)

        //init a ringtone
        val ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(this,RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(this,ringtoneUri)

        //init tts
        tts = TextToSpeech(this,this)

        //view model
        viewModel = ViewModelProvider(this)[AlarmViewModel::class.java]

        //check internet
        if(isNetworkAvailable(this)){
           //load news
            viewModel.loadNewsList()
        }else ringTheAlarm()


        //setup tts
        tts.setOnUtteranceProgressListener(object :UtteranceProgressListener(){
            override fun onStart(utteranceId: String?) {
            }
            override fun onDone(utteranceId: String?) {
                //read after done previous
                if(iterator.hasNext())
                    speak(iterator.next().title)
                else {
                    Toast.makeText(
                        this@AlarmActivity,
                        "news read complete",
                        Toast.LENGTH_SHORT
                    ).show()
                    ringTheAlarm()
                    this@AlarmActivity.finish()
                }
            }
            override fun onError(utteranceId: String?) {
                Toast.makeText(this@AlarmActivity, "error in text to speech", Toast.LENGTH_SHORT).show()
                ringTheAlarm()
            }
        })

        //read news when available
        viewModel.newsList.observe(this){


            if(it.isNullOrEmpty().not() && ttsAvailable)
            {
                if (::iterator.isInitialized.not() ) iterator = it.listIterator()

                //read news
                speak(iterator.next().title)

            }
            else ringTheAlarm()
        }

        //stop btn
        binding.stopBtn.setOnClickListener{
            finish()
        }
    }

    private fun ringTheAlarm() {
         ringtone.play()
    }
    fun stopAlarmRing(){
        if(ringtone.isPlaying) ringtone.stop()
    }
    fun stopNewsRead(){
        if(tts.isSpeaking) tts.stop()
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS)
        {
            val result = tts.setLanguage(Locale.US)
            tts.setSpeechRate(0.8F)
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            {
                Toast.makeText(this, "tts not supported", Toast.LENGTH_SHORT).show()
                ttsAvailable = false
                ringTheAlarm()
            }
        }
        else ttsAvailable = false
    }

    fun speak(text:String){
        tts.speak(text, TextToSpeech.QUEUE_FLUSH,null,"")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopNewsRead()
        stopAlarmRing()
    }
}