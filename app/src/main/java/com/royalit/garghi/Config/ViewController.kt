package com.royalit.garghi.Config

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.internal.ContextUtils

object ViewController {

    var mProgressDialog: ProgressDialog? = null


    fun changeStatusBarColor(activity: Activity, color: Int, isLight: Boolean) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        activity.window.statusBarColor = color
        WindowInsetsControllerCompat(activity.window, activity.window.decorView).isAppearanceLightStatusBars = isLight
    }

    @SuppressLint("RestrictedApi")
    fun removeStatusBar(context: Context) {
        val window = ContextUtils.getActivity(context)?.window

        window?.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            decorView.apply {
                systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            statusBarColor =
                ContextCompat.getColor(context, android.R.color.transparent)
        }
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showLoading(context: Context) {
        mProgressDialog = ProgressDialog(context).apply {
            setMessage("Loading...")
            show()
        }
    }
    fun hideLoading() {
        mProgressDialog?.dismiss()
    }


    fun noInterNetConnectivity(ctx: Context):Boolean
    {
        val connectivityManager= ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    fun hideKeyboard(context: Context, view: View) {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hasEditText(editText: EditText, errMsg: String??
    ): Boolean {
        if (editText.text.toString().trim { it <= ' ' }.isEmpty()) {
            editText.error = errMsg
            editText.requestFocus()//(editText, context)
            return false
        } else {
            // editText.isFocusable= false
        }
        return true
    }

}