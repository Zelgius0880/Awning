package com.zelgius.awning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AwningRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.startListening()
        }
    }

    val progress = repository.awningFlow.map { it.progress }.distinctUntilChanged()
    val network = repository.awningFlow.map {
        it.network.let { network ->
            // dBm to Quality:
            if (network <= -100)
                0;
            else if (network >= -50)
                100;
            else
                2 * (network + 100);
        }
    }.distinctUntilChanged()
    val status = repository.awningFlow.map { it.status }.distinctUntilChanged()
    val closingTime = repository.awningFlow.map { it.closingDuration }.distinctUntilChanged()
    val openingTime = repository.awningFlow.map { it.openingDuration }.distinctUntilChanged()

    fun close() {
        viewModelScope.launch {
            repository.setTime(closingTime.first())
            delay(100)
            repository.setStatus(Status.Closed)
        }
    }

    fun open() {
        viewModelScope.launch {
            repository.setTime(openingTime.first())
            delay(100)
            repository.setStatus(Status.Opened)
        }
    }

    fun stop() {
        viewModelScope.launch {
            repository.setStatus(Status.Stopped)
        }
    }

    fun setOpeningTime(time: Long) {
        viewModelScope.launch {
            repository.setOpeningTime(time)
        }
    }


    fun setClosingTime(time: Long) {
        viewModelScope.launch {
            repository.setClosingTime(time)
        }
    }

    fun forceStatus(status: Status) {
        viewModelScope.launch {
            repository.forceStatus(status)
        }
    }
}