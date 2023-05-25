package com.jesussoto.android.cabifyshop.sync

import io.reactivex.rxjava3.core.Observable

interface SyncManager {

    fun isSyncing(): Observable<Boolean>

    fun requestSync()
}