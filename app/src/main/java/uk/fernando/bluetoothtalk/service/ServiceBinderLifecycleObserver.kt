package uk.fernando.bluetoothtalk.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.flow.MutableStateFlow
import uk.fernando.bluetoothtalk.ext.TAG

class ServiceBinderLifecycleObserver(private val context: Context) : DefaultLifecycleObserver, ServiceConnection {

    val mService: MutableStateFlow<MyBleService?> = MutableStateFlow(null)

    override fun onStart(owner: LifecycleOwner) {
        Log.e(TAG, "onStart")
        val intent = Intent(context, MyBleService::class.java)
        context.startService(intent)
        context.bindService(
            intent,
            this,
            Service.BIND_ADJUST_WITH_ACTIVITY
        )
    }

    override fun onStop(owner: LifecycleOwner) {
        context.unbindService(this)

        super.onStop(owner)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (service is MyBleService.BleBinder) {
            mService.tryEmit(service.getService())
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        mService.tryEmit(null)
    }
}