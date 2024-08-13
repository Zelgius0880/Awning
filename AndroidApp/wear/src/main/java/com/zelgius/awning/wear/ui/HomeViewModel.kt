package com.zelgius.awning.wear.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zelgius.awning.AwningRepository
import com.zelgius.awning.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
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
    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()

    val status = repository.awningFlow.map {
        _loading.value = false
        it.status
    }.distinctUntilChanged()
    val time = repository.awningFlow.map { it.duration }.distinctUntilChanged()

    fun close() {
        viewModelScope.launch {
            repository.setStatus(Status.Closed)
        }
    }

    fun open() {
        viewModelScope.launch {
            repository.setStatus(Status.Opened)
        }
    }

    fun stop() {
        viewModelScope.launch {
            repository.setStatus(Status.Stopped)
        }
    }

    fun forceStatus(status: Status) {
        viewModelScope.launch {
            repository.forceStatus(status)
        }
    }
}