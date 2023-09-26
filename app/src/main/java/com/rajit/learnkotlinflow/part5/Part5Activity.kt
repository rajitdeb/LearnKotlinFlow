package com.rajit.learnkotlinflow.part5

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rajit.learnkotlinflow.databinding.ActivityPart5Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

private const val TAG = "PART5"

/**
 * Understanding the concept of Shared Flow
 */
class Part5Activity : AppCompatActivity() {

    private lateinit var binding: ActivityPart5Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPart5Binding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * Shared Flow is a type of Flow but HOT in nature.
         * Generally, Flows are COLD in nature, but SharedFlow is designed to be HOT in nature
         *
         * Basically, SharedFlow is when the consumers don't a get independent flow objects, but a shared one
         * Which means if there are 2 consumers of 1 producer, and the 2nd consumer joins late, it will miss the previously emitted values
         *
         * DOUBT: If SharedFlow is doing the same thing as Channels, then why aren't we using Channels rather than creating a new type of Flow
         * ANSWER[AS FOUND IN GOOGLE]:
         * SharedFlow API is much simpler to use and has replay() and Overflow Strategy than Channels
         *
         */

        // CONSUMER 1
        lifecycleScope.launch {
            val result1 = producer()
            result1.collect {
                Log.d(TAG, "CON1 ITEM - $it")
            }
        }

        // CONSUMER 2
        lifecycleScope.launch {
            val result2 = producer()
            delay(3500)
            result2.collect {
                Log.d(TAG, "CON2 ITEM - $it")
            }
        }

    }

    private fun producer(): Flow<Int> {

        /** @replay is used to save the previously emitted [last x] values and emit them to the consumer that joins late */
        val mSharedFlow = MutableSharedFlow<Int>(replay = 2)
        val list = listOf<Int>(1, 2, 3, 4, 5)

        CoroutineScope(Dispatchers.IO).launch {
            list.forEach {
                delay(1000)
                mSharedFlow.emit(it)
            }
        }

        return mSharedFlow

    }

}