package com.jesussoto.android.cabifyshop.ui.base

import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * Simple base activity to abstract life-cycle aware setup and clean-up logic of disposables
 * when subscribing to events from he ViewModel.
 */
abstract class BaseActivity: AppCompatActivity() {

    private var disposables: CompositeDisposable? = null

    override fun onStart() {
        super.onStart()
        this.internalBindViewModel()
    }

    private fun internalBindViewModel() {
        disposables = CompositeDisposable()
        bindViewModel(disposables!!)
    }

    override fun onStop() {
        super.onStop()
        unbindViewModel()
    }

    abstract fun bindViewModel(disposables: CompositeDisposable)

    private fun unbindViewModel() {
        disposables?.dispose()
        disposables = null
    }

    protected fun makeToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, text, duration).show()
    }

    protected fun makeToast(@StringRes textResId: Int, duration: Int = Toast.LENGTH_SHORT) {
        makeToast(getString(textResId), duration)
    }

    protected fun showSnackbar(view: View, text: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(view, text, duration).show()
    }

    protected fun showSnackbar(
        view: View, @StringRes textResId: Int, duration: Int = Snackbar.LENGTH_SHORT
    ) {
        showSnackbar(view, getString(textResId), duration)
    }

    protected fun navigateUp() {
        val callingIntent = NavUtils.getParentActivityIntent(this)
        if (callingIntent != null) {
            NavUtils.navigateUpTo(this, callingIntent)
        } else {
            finish()
        }
    }
}
