package com.mk.newsalarm.service

import android.app.Service
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mk.newsalarm.model.api.NetworkResponse
import com.mk.newsalarm.model.domain.DomainModel
import com.mk.newsalarm.model.isNetworkAvailable
import com.mk.newsalarm.repository.Repository
import com.mk.newsalarm.view.AlertActivity
import com.mk.newsalarm.view.dataStore
import kotlinx.coroutines.*
import java.util.*

class AlertService : Service(), TextToSpeech.OnInitListener {

    private val PENDING_ALARM = stringPreferencesKey("pending_alarm")
    private var tts: TextToSpeech? = null
    lateinit var ringtone : Ringtone
    lateinit var iterator: ListIterator<DomainModel.News>
    var ttsAvailable = true


    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO+job)
    private val repository = Repository()

    override fun onCreate() {
        super.onCreate()

        Log.i("TAG", "onCreate: service")
        //init a ringtone
        val ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(this,ringtoneUri)

        //init tts
        tts = TextToSpeech(this,this)
        setupTts()

        //check internet
        if(isNetworkAvailable(this)){
            //load news
            loadNewsList()
        }
        else ringTheAlarm()

        //start alert activity
        Intent(this, AlertActivity::class.java).let {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
        }
    }

    private fun setupTts(){
        //setup tts
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener(){
            override fun onStart(utteranceId: String?) {
            }
            override fun onDone(utteranceId: String?) {
                //read after done previous
                if(iterator.hasNext()) {
                    speak(iterator.next().title)
                }
                else {
                    Toast.makeText(
                        this@AlertService,
                        "news read complete",
                        Toast.LENGTH_SHORT
                    ).show()
                    ringTheAlarm()
                    stopSelf()
                }
            }
            override fun onError(utteranceId: String?) {
                Toast.makeText(this@AlertService, "error in text to speech", Toast.LENGTH_SHORT).show()
                ringTheAlarm()
            }
        })

        //clear pending alarm from data store
        clearDataStore()
    }

    private fun loadNewsList(){
        coroutineScope.launch(Dispatchers.IO) {
            when(val networkResponse = repository.getNewsList()){
                is NetworkResponse.Success -> withContext(Dispatchers.Main+job){readNews(networkResponse)}
                is NetworkResponse.Error -> {
                    ringTheAlarm()
                    withContext(Dispatchers.Main+job){
                        Toast.makeText(this@AlertService, "network error", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun readNews(networkResponse: NetworkResponse.Success) {
        if(networkResponse.response.isNullOrEmpty().not() && ttsAvailable)
        {
            if (::iterator.isInitialized.not() ) iterator = networkResponse.response.listIterator()
            //read news
            Log.i("TAG", "readNews: service")
            speak(iterator.next().title)
        }
        else ringTheAlarm()
    }


    private fun ringTheAlarm() {
        ringtone.play()
    }
    private fun stopAlarmRing(){
        if(ringtone.isPlaying) ringtone.stop()
    }
    private fun stopNewsRead(){
        tts?.let {
            it.stop()
            tts?.shutdown()
        }
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS)
        {
            val result = tts?.setLanguage(Locale.US)
            tts?.setSpeechRate(0.8F)
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
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH,null,"")
    }

    private fun clearDataStore() {
        coroutineScope.launch{
            this@AlertService.dataStore.edit {
                it[PENDING_ALARM] = ""
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        stopNewsRead()
        stopAlarmRing()
        Log.i("TAG", "onDestroy:service ")
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}