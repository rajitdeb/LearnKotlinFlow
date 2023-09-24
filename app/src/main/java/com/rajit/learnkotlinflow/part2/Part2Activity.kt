package com.rajit.learnkotlinflow.part2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rajit.learnkotlinflow.R
import com.rajit.learnkotlinflow.databinding.ActivityPart2Binding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

private const val TAG = "FLOW"

/**
 * Part 2 - Understanding Flow Builder, Cold Stream and Cancellation
 */
class Part2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityPart2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPart2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * Advantages of Cold Stream
         * 1. Prevents wastage of resources [Hot streams keep on producing data, even when not required]
         * 2. Doesn't need manual cancellation [REQUIRED FOR HOT STREAMS]
         * 3. Preferred over HOT streams
         *
         */

        /**
         * STREAMS have a MAJOR problem called, "CONSUMER-PRODUCER" problem. [USUAL PROBLEM WITH STREAMS]
         * In this problem, we have two types of bottlenecks:
         * 1. Consumer Bottleneck -> Where the PRODUCER is producing data fast, but CONSUMER is not consuming it at that pace
         * 2. Producer Bottleneck -> Where the CONSUMER is consuming data fast, but PRODUCER is not able to keep up with the pace of CONSUMER
         */

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val data: Flow<Int> = producer()
                /** Flow Producer Block will only emit data if there is at least one consumer */
//            data.collect {
//                Log.d(TAG, "Flow Data: $it")
//            }
                /** If there are no consumers for a producer, the producer will be automatically CANCELLED */
            }
        }

        /** Demonstrating multiple consumer */
        // CONSUMER 1
        val newJob = lifecycleScope.launch {
            val data: Flow<Int> = producer()
            data.collect {
                Log.d(TAG, "Flow Data - 1: $it")
            }
        }

        // CONSUMER 2
        val new2Job = lifecycleScope.launch {
            val data: Flow<Int> = producer()
            /** Here, we add 2500ms delay to check if this consumer gets data from the start or the previously emitted data is lost */
            delay(2500)
            data.collect {
                Log.d(TAG, "Flow Data - 2: $it")
            }
        }

        /** Cancelling a coroutine, will also cancel the consumer in ti */
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                delay(6500)
                newJob.cancel()
            }
        }

    }

    private fun producer() = flow<Int> {
        val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

        list.forEach {
            delay(1000)
            emit(it)
        }
    }

}