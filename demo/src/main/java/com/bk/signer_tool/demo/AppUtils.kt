package com.bk.signer_tool.demo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build

import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


const val PERMISSION_REQUEST = 100

object AppUtils {


    fun checkStoragePermission(
        activity: Activity,
        context: Context
    ): Boolean {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            return checkPermission(
                context,
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE

            )

        }

        return true

    }


    private fun checkPermission(context: Context, activity: Activity, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        if (activity.shouldShowRequestPermissionRationale(permission)) {
            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.permission_request))
                .setMessage(context.getString(R.string.give_app_permission))
                .setPositiveButton(
                    android.R.string.ok
                ) { dialog: DialogInterface?, which: Int ->
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(permission),
                        PERMISSION_REQUEST
                    )
                }.setNegativeButton(
                    android.R.string.cancel
                ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }.create()
                .show()
            return false
        }

        ActivityCompat.requestPermissions(
            activity, arrayOf(permission),
            PERMISSION_REQUEST
        )

        return false
    }




}