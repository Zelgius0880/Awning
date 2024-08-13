package com.zelgius.awning

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.zelgius.awning.AwningStatus.Companion.CLOSE
import com.zelgius.awning.AwningStatus.Companion.OPEN
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AwningRepository {
    private val auth by lazy { Firebase.auth }
    private val database by lazy { Firebase.database.reference }

    private val _awningFlow = MutableStateFlow<AwningStatus?>(null)
    val awningFlow = _awningFlow.filterNotNull()

    private val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.getValue<FirebaseAwningStatus>()?.let {

                val  status = when {
                    it.requested_status == OPEN && it.status == CLOSE -> Status.Opening
                    it.requested_status == CLOSE && it.status == OPEN -> Status.Closing
                    it.requested_status == OPEN && it.status == OPEN -> Status.Opened
                    it.requested_status == CLOSE && it.status == CLOSE -> Status.Closed
                    else -> Status.Stopped
                }
                val duration = it.duration ?: 0L
                _awningFlow.value = AwningStatus(
                    duration = duration,
                    network = it.network ?: 0,
                    progress = if(status == Status.Closed || status == Status.Opened) null else
                        (it.progress ?: 0).coerceAtLeast(0).coerceAtMost(100),
                    status = status,
                    openingDuration = it.opening_duration ?: duration,
                    closingDuration = it.closing_duration ?: duration
                )
            }

        }

        override fun onCancelled(error: DatabaseError) {
            error.toException().printStackTrace()
        }

    }

    suspend fun startListening() {
        signIn()
        database.child("awning").addValueEventListener(listener)
    }

    fun stopListening() {
        database.removeEventListener(listener)
    }

    private suspend fun signIn(): Boolean {
        if (auth.currentUser != null) return true
        return auth.signInWithEmailAndPassword(BuildConfig.EMAIL, BuildConfig.PASSWORD)
            .wait()
    }

    suspend fun setStatus(requestStatus: Status) {
        signIn()

        when (requestStatus) {
            Status.Opened -> 2
            Status.Closed -> 1
            Status.Stopped -> 0
            else -> null
        }?.let {
            database.child("awning").child("requested_status").setValue(it).wait()
        }
    }

    suspend fun setTime(time: Long) {
        signIn()
        database.child("awning").child("duration").setValue(time).wait()
    }


    suspend fun setOpeningTime(time: Long) {
        signIn()
        database.child("awning").child("opening_duration").setValue(time).wait()
    }


    suspend fun setClosingTime(time: Long) {
        signIn()
        database.child("awning").child("closing_duration").setValue(time).wait()
    }

    suspend fun forceStatus(status: Status) {
        signIn()

        val statusInt = when(status) {
            Status.Opened -> 2
            Status.Closed -> 1
            else -> return
        }
        awningFlow.first().let {
            database.child("awning").child("requested_status").setValue(statusInt).wait()
            database.child("awning").child("status").setValue(statusInt).wait()
            database.child("awning").child("progress").setValue(0).wait()

            /*database.child("awning").updateChildren(
                mapOf(
                    "duration" to it.duration,
                    "network" to it.network,
                    "progress" to it.progress,
                    "requested_status" to statusInt,
                    "status" to statusInt,
                )
            ).wait()*/
        }
    }

    private suspend fun <T> Task<T>.wait(): Boolean = suspendCancellableCoroutine { continuation ->
        addOnCompleteListener {
            if (!it.isSuccessful) it.exception?.printStackTrace()

            continuation.resume(it.isSuccessful)
        }
    }

    data class FirebaseAwningStatus(
        val duration: Long? = null,
        val opening_duration: Long? = null,
        val closing_duration: Long? = null,
        val network: Int? = null,
        val progress: Int? = null,
        val requested_status: Int? = null,
        val status: Int? = null
    )
}