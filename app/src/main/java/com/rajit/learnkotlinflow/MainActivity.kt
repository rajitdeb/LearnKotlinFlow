package com.rajit.learnkotlinflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.rajit.learnkotlinflow.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

private const val TAG = "CHANNELS"

/** Understanding the concept of Kotlin Flow & Channels*/
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val channel = Channel<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * So, the entire story revolves around streaming of data.
         * Whenever there is a need for streaming data asynchronously, Kotlin provides us with two approaches:
         * 1. Channels (Send & Receive methods)
         * 2. Flow (Emit & Collect methods)
         *
         * CHANNELS (Send & Receive methods)
         *    ->  The producer (of data) keeps on producing, irrespective of the state of the consumer.
         *    ->  Basically, the producer keeps producing data, even when the consumer is not present or in a state to consume.
         *    ->  Channels are hot. (Once the data is sent, it cannot be read from the start)
         *    ->  Think of it, as a MOVIE THEATRE, where a show starts @ 4PM, whoever is present at that point will be able to watch it from the start, others will start from when they arrive (let's say @ 4:40PM)
         *
         * FLOWS (Emit & Collect methods) [WE MOSTLY USE FLOWS]
         *    ->  The producer (of data) produces ONLY WHEN THERE IS A CONSUMER
         *    ->  Flows are cold. (The data can be received from the start, everytime)
         */

        producer()
        consumer()
        
    }

    /** Channels - This function produces data */
    fun producer() {
        CoroutineScope(Dispatchers.IO).launch {
            channel.send(1)
            channel.send(2)
        }
    }

    /** Channels - This function consumes dat, produced/supplied by the producer through Channels */
    fun consumer() {
        CoroutineScope(Dispatchers.Main).launch {

            Log.d(TAG, "consumer: ${channel.receive()}") // for 1
            Log.d(TAG, "consumer: ${channel.receive()}") // for 2
        }
    }

}