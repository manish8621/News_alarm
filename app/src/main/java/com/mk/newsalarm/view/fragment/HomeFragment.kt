package com.mk.newsalarm.view.fragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.mk.newsalarm.MainActivity
import com.mk.newsalarm.R
import com.mk.newsalarm.databinding.FragmentHomeBinding
import com.mk.newsalarm.databinding.FragmentNewsListBinding
import com.mk.newsalarm.view.AlarmActivity
import com.mk.newsalarm.view.dataStore
import com.mk.newsalarm.viewmodel.NewsListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

class HomeFragment : Fragment() {

    private val REQ_CODE = 10
    private val PENDING_ALARM = stringPreferencesKey("pending_alarm")

    //    private lateinit var viewModel: NewsListViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)

        lifecycleScope.launch{ readFromDataStore() }
        return binding.root
    }
    private fun setAlarm() {
        calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = binding.timePicker.hour
        calendar[Calendar.MINUTE] = binding.timePicker.minute
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0


        val intent = Intent(context,AlarmActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        pendingIntent = PendingIntent.getActivity(context,REQ_CODE,intent,PendingIntent.FLAG_UPDATE_CURRENT)

        //setAlarm
        alarmManager = (activity as MainActivity).getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC ,calendar.timeInMillis ,pendingIntent)

        lifecycleScope.launch{ writeToDataStore() }
        Toast.makeText(context, "alarm set", Toast.LENGTH_SHORT).show()
    }

    private suspend fun readFromDataStore(){
        val data = requireContext().dataStore.data.map { pref->pref[PENDING_ALARM]?:"null" }.asLiveData().observe(viewLifecycleOwner){
            if (it!=null && it.isNotEmpty()) updatePendingAlarmUi(it)
            else hideCancelUi()
        }

    }

    private fun updatePendingAlarmUi(time: String) {
        "pending alarm : $time".also {
            binding.pendingAlarmTv.text = it
            showCancelUi()
        }
    }

    private fun showCancelUi() {
        binding.pendingAlarmTv.visibility = View.VISIBLE
        binding.cancelBtn.visibility = View.VISIBLE
    }
    private fun hideCancelUi() {
        binding.pendingAlarmTv.visibility = View.GONE
        binding.cancelBtn.visibility = View.GONE
    }

    private suspend fun writeToDataStore() {
        val time = StringBuilder("")
        val min = binding.timePicker.minute
        val hour = binding.timePicker.hour.let{
            if(it>12) {
                time.append("${ it - 12 } : $min PM")
                it - 12
            }
            else
                time.append("$it : $min AM")
                it
        }

        requireContext().dataStore.edit {
            it[PENDING_ALARM] = time.toString()
        }
        readFromDataStore()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setBtn.setOnClickListener{
                setAlarm()
        }


        binding.cancelBtn.setOnClickListener{
            val intent = Intent(context,AlarmActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            if (!::pendingIntent.isInitialized) pendingIntent = PendingIntent.getActivity(context,REQ_CODE,intent,PendingIntent.FLAG_UPDATE_CURRENT)

            //setAlarm
            alarmManager = (activity as MainActivity).getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            hideCancelUi()
            clearDataStore()
            Toast.makeText(context, "alarm cancel", Toast.LENGTH_SHORT).show()
        }

        binding.newsBtn.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_newsListFragment)
        }
    }

    private fun clearDataStore() {
        lifecycleScope.launch{
            requireContext().dataStore.edit {
                it[PENDING_ALARM] = ""
            }
        }
    }
}