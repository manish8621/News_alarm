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
import androidx.navigation.fragment.findNavController
import com.mk.newsalarm.MainActivity
import com.mk.newsalarm.R
import com.mk.newsalarm.databinding.FragmentHomeBinding
import com.mk.newsalarm.databinding.FragmentNewsListBinding
import com.mk.newsalarm.view.AlarmActivity
import com.mk.newsalarm.viewmodel.NewsListViewModel
import java.util.Calendar
import java.util.Date

class HomeFragment : Fragment() {

    private val REQ_CODE = 10

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


        return binding.root
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setBtn.setOnClickListener{
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

            Toast.makeText(context, "alarm set", Toast.LENGTH_SHORT).show()
        }


        binding.cancelBtn.setOnClickListener{
            val intent = Intent(context,AlarmActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            if (!::pendingIntent.isInitialized) pendingIntent = PendingIntent.getActivity(context,REQ_CODE,intent,PendingIntent.FLAG_UPDATE_CURRENT)

            //setAlarm
            alarmManager = (activity as MainActivity).getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            Toast.makeText(context, "alarm cancel", Toast.LENGTH_SHORT).show()
        }

        binding.newsBtn.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_newsListFragment)
        }
    }
}