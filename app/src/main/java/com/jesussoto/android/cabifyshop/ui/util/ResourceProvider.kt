package com.jesussoto.android.cabifyshop.ui.util

import android.content.Context
import androidx.annotation.StringRes
import com.jesussoto.android.cabifyshop.data.di.qualifier.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simple wrapper around Resources to abstract the need of directly referencing [Context]
 * to resolve resources from the system
 */
interface ResourcesProvider {

    fun getString(@StringRes resId: Int): String

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
}

@Singleton
class AndroidResourcesProvider @Inject constructor(
    @ApplicationContext private val context: Context
): ResourcesProvider {

    override fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    override fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}
