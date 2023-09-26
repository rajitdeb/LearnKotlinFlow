package com.rajit.learnkotlinflow.part4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.rajit.learnkotlinflow.R
import com.rajit.learnkotlinflow.databinding.ActivityPart4Binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "PART4"

/**
 * Understanding the concept of Context Preservation, flowOn, Exception Handling and catch
 */
class Part4Activity : AppCompatActivity() {

    private lateinit var binding: ActivityPart4Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPart4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * By Default, Kotlin Flow assumes the thread on which the producer is to be run is same as that of the consumer's thread
         * For example, if consumer collects data on MAIN thread, then producer will also produce on MAIN thread
         *
         * NOTE:
         * If we try to collect on MAIN thread and emit on IO thread via withContext() provided by Coroutines, this will throw exception and crash
         * So, we make use of flowOn() to explicitly mention on which we want our producer to run, in our case, IO Thread
         *
         *
         */

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                producer()
//                    .map {
//                        delay(2500)
//                        Log.d(TAG, "MAPPER THREAD - ${Thread.currentThread().name}")
//                        it * 2
//                    }
//                    .filter {
//                        delay(1000)
//                        Log.d(TAG, "FILTER THREAD - ${Thread.currentThread().name}")
//                        it % 2 == 0
//                    }
                    /**
                     * This line tells the compiler to execute the producer() on IO Thread,
                     * all methods above flowOn will be executed on thread specified by flowOn()
                     */
                    .flowOn(Dispatchers.IO)
                    .collect {/** Here we execute the collect() on MAIN Thread */
                        Log.d(TAG, "COLLECTOR THREAD - ${Thread.currentThread().name}, $it")
                        /**
                         * To understand exception handling in Flow, expecting error in Collector
                         */
//                        throw Exception("Error in COLLECTOR")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "ERROR: ${e.message}")
            }
        }

    }

    private fun producer(): Flow<Int> {
        return flow<Int> {
            /** We can't use withContext() while using Flow */
//            withContext(Dispatchers.IO) { /** This will throw us an error */
//                val list = listOf(1, 2, 3, 4, 5)
//                list.forEach {
//                    delay(1000)
//                    Log.d(TAG, "EMITTER THREAD - ${Thread.currentThread().name}")
//                    emit(it)
//                }
//            }

            val list = listOf(1, 2, 3, 4, 5)
            list.forEach {
                delay(1000)
                Log.d(TAG, "EMITTER THREAD - ${Thread.currentThread().name}")
                emit(it)
            }

            /** To understand exception handling in Flow */
            throw Exception("Error in EMITTER")

        }.catch {
            /**
             * Sometimes we want to catch the exception happening at the producer end, there itself
             * So, we make use of the catch() to do that.
             *
             * IT WORKS UPSTREAM, basically it is only used for methods defined above it
             * MULTIPLE catch() can be used for a PRODUCER
             *
             * ANOTHER ADVANTAGE:
             * We can also FALLBACK ELEMENTS when error occurs (if any)
             *
             * EXAMPLE:
             */

            Log.d(TAG, "EMITTER CATCH - ${it.message}")
            emit(-1) // Signifying FALLBACK ELEMENT, ERROR OCCURRED

            /**
             * NOTE:
             * We have used map function in the collector, if that's the case we won't be getting -1 in the collect, we get -2
             */

        }
    }

}