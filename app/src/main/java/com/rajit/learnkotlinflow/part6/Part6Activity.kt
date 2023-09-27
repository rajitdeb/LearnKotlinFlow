package com.rajit.learnkotlinflow.part6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.rajit.learnkotlinflow.R
import com.rajit.learnkotlinflow.databinding.ActivityPart6Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "PART6"

/**
 * Understanding the concept of StateFlow
 */
class Part6Activity : AppCompatActivity() {

    private lateinit var binding: ActivityPart6Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPart6Binding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * StateFlow is similar to SharedFlow (how values are emitted, and consumers get data)
         * It is also of HOT nature (once data is emitted, if the consumer is not available then, the consumer won't receive that value
         *
         * BUT THE MAJOR DIFFERENCE OF STATEFLOW - It saves the last emitted element and the consumer whenever joins gets it (if joined late)
         *
         */

        lifecycleScope.launch {
            val result = producer()
//            delay(6000) // Simulating the behavior that the consumer joined late
//            result
//                .collect {
//                    Log.d(TAG, "ITEM - $it") /** It would still receive the last element */
//                }

            /**
             * Since the producer is returning the SharedFlow object,
             * the consumer will be able to access whatever value present at that time, when consumer joins
             */
            Log.d(TAG, "VALUE - ${result.value}")
        }

        /**
         * DIFFERENCE BETWEEN LIVEDATA and STATEFLOW
         *
         * ANSWER -
         * 1. In LiveData, all the transformations (like map(), filter()) that are done on the data are executed on MAIN thread.
         *    But StateFlow is a type of Flow, so there we have the flowOn() method where we can offload these transformations to IO thread.
         *
         * 2. Very Limited no. of operators are present in LiveData as compared to StateFlow or Flow.
         *
         * 3. LiveData is dependent on LifeCycle, it would always require a lifecycle to be executed. We [CAN'T] use it in Repository file.
         *    StateFlow, on the other hand, just needs a CoroutineScope to be executed. We [CAN] use it in Repository.
         *
         */

    }

    /**
     * This function returns a Flow object
     */
//    private fun producer(): Flow<Int> {
//         val mStateFlow = MutableStateFlow<Int>(-1)
//
//        CoroutineScope(Dispatchers.Main).launch {
//            delay(2000)
//            mStateFlow.emit(1)
//
//            delay(2000)
//            mStateFlow.emit(2)
//        }
//
//        return mStateFlow
//
//    }

    /** This function returns a SharedFlow object */
    private fun producer(): StateFlow<Int> {
        val mStateFlow = MutableStateFlow<Int>(-1)

        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            mStateFlow.emit(1)

            delay(2000)
            mStateFlow.emit(2)
        }

        return mStateFlow

    }

}