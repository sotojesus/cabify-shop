package com.jesussoto.android.cabifyshop.data.repository

import io.reactivex.rxjava3.core.Completable

/**
 * Syncable interface should be implemented tby repositories that are syncable from network.
 */
interface Syncable {

    /**
     * Triggers sync operation from network, this would very likely be called by a SyncWorker.
     */
    fun sync(): Completable
}
