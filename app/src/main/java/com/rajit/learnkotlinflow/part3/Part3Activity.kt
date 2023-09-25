package com.rajit.learnkotlinflow.part3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.rajit.learnkotlinflow.R
import com.rajit.learnkotlinflow.databinding.ActivityPart3Binding
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

private const val TAG = "PART3"

/**
 * Understanding the concept of TERMINAL, MAP, FILTER BUFFER operators
 */
class Part3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityPart3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPart3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * There are events where we need to do something when that event occurs
         * Kotlin Flow provides us those event triggers, like onStart(), onCompletion(), onEach(), etc.
         *
         * 1. onStart() -> Any code written in this block will be executed before the flow emits values
         * 2. onCompletion() -> Any code written in this block will be executed after the flow emits all the values
         * 3. onEach() -> Any code written in this block will be executed each time before emitting each value
         *
         * Example:
         */

//        lifecycleScope.launch {
//            producer()
//                .onStart {
//                    /** We can also emit values manually here */
////                    emit(-1)
//                    Log.d(TAG, "STARTING TO EMIT VALUES")
//
//                    /** USUALLY, WE USE onStart() TO SHOW LOADING UI */
//                }
//                .onCompletion {
//                    /** We can also emit values manually here */
////                    emit(11)
//                    Log.d(TAG, "COMPLETED EMITTING VALUES")
//
//                    /** USUALLY, WE CLEAR THE RESOURCES USED HERE */
//                }
//                .onEach { // Always executes before calling the collect function each time
//                    Log.d(TAG, "ABOUT TO EMIT - $it")
//                }
//                .collect {
//                    Log.d(TAG, "VALUE - $it")
//                }
//        }

        /**
         * Flow has 2 types of Operators:
         * 1. TERMINAL OPERATORS -> first(), collect(), toList() [All these Operators have a Suspend keyword attached to them]
         * 2. NON-TERMINAL OPERATORS -> map(), filter() [Don't have Suspend keyword and it returns a flow]
         *
         * TERMINAL OPERATORS EXAMPLE:
         */
        lifecycleScope.launch {
            /** TERMINAL OPERATORS EXAMPLE */
//            val result = producer().first() // Only receives the first value of the Flow
//            Log.d(TAG, "EMITTING FIRST VALUE OF THE FLOW: $result")

//            val result = producer().toList() // Converts Flow into List
//            Log.d(TAG, "EMITTING FLOW AS LIST: $result")


            /** NON_TERMINAL OPERATORS EXAMPLE */
//            producer()
//                .map {/** map function transforms one object into another */
//                    // Multiplying each value by 2
//                    it * 2
//                }
//                .filter {/** filter function filters the values based on some criteria/condition, returns a boolean */
//                    // only include even values
//                    it % 2 == 0
//                }
//                .collect {
//                    Log.d(TAG, "VALUE - $it")
//                }

            /** Non-Terminal Operators IN_ACTION */
            getNotes()
                .map {
                    // We want to transform the title of the Note to ALL UPPERCASE
                    // and map it to new data class FormattedNote
                    /** FormattedNote will be shown in the UI */
                    FormattedNote(it.isActive, it.title.uppercase(), it.description)
                }
                .filter {
                    // We want to only consume Notes that are ACTIVE (Not Completed)
                    it.isActive
                }
                .collect {
                    Log.d(TAG, it.toString())
                }

        }

        /** buffer() IN ACTION */
        lifecycleScope.launch {
            val time = measureTimeMillis {
                producer()
                    .buffer(3) /** With Buffer of 3 items, time reduced to 16.1 seconds [~35% Less Time Taken]*/
                    .collect {/** Normally, taking 25seconds to consume all the values */
                        delay(1500) // Simulating the behaviour consumer taking time to consume
                        Log.d(TAG, "VALUE: $it")
                    }

            }

            Log.d(TAG, "Total Time Taken: $time")
        }

    }

    private fun producer(): Flow<Int> {
        return flow<Int> {
            val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            list.forEach {
                delay(1000)
                emit(it)
            }
        }
    }
}

data class Note(
    val id: Int,
    val isActive: Boolean,
    val title: String,
    val description: String
)

data class FormattedNote(
    val isActive: Boolean,
    val title: String,
    val description: String
)

/** This function simulates the behavior of a network call */
private fun getNotes(): Flow<Note> {
    val list = listOf(
        Note(1, true, "First", "First Description"),
        Note(2, true, "Second", "Second Description"),
        Note(3, false, "Third", "Third Description")
    )

    return list.asFlow() // flow builder
}