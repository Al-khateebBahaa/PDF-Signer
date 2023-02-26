package com.bk.signer_tool.demo

import android.app.Activity
import kotlin.Throws
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.view.GravityCompat
import android.os.*
import android.view.*
import android.viewbinding.library.activity.viewBinding
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.isVisible
import com.bk.signer_tool.core.Errors.PdfSignerInitialingException
import com.bk.signer_tool.core.callbacks.SignerCallback
import com.bk.signer_tool.core.signer.SignBuilder
import com.bk.signer_tool.core.signer.SignManager
import com.bk.signer_tool.demo.Adapter.MainRecycleViewAdapter
import com.bk.signer_tool.demo.databinding.ComBkSignerActivityMainBinding
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener, PickiTCallbacks, SignerCallback,
    MainRecycleViewAdapter.OnItemClickListener {

    private var mFilesContent: ActivityResultLauncher<Intent>? = null
    private val mBinding: ComBkSignerActivityMainBinding by viewBinding()
    private var mSignManager: SignManager? = null
    private val mAdapter: MainRecycleViewAdapter by lazy {
        MainRecycleViewAdapter(
            mOnItemClickListener = this
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SignBuilder.onCreate(this)

        registerFileContentPicker()
        startSigner()

        setSupportActionBar(mBinding.pdfSignAppBar.pdfSignToolbar)

        mBinding.pdfSignAppBar.pdfSignFab.setOnClickListener {

            if (AppUtils.checkStoragePermission(this, this))
                mFilesContent?.launch(createOpenFileIntent())

        }

        val toggle = ActionBarDrawerToggle(
            this,
            mBinding.drawerLayout,
            mBinding.pdfSignAppBar.pdfSignToolbar,
            R.string.com_bk_signer_navigation_drawer_open,
            R.string.com_bk_signer_navigation_drawer_close
        )
        mBinding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        mBinding.pdfSignNavView.setNavigationItemSelectedListener(this)
        mBinding.pdfSignAppBar.contentInclude.mainRecycleView.setHasFixedSize(true)
        mBinding.pdfSignAppBar.contentInclude.mainRecycleView.adapter = mAdapter

    }


    private fun startSigner() {

        try {
            mSignManager = SignBuilder.setCallback(this).build()

        } catch (e: PdfSignerInitialingException) {
            e.printStackTrace()
        }
    }

    private fun createOpenFileIntent() = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "application/pdf"
        addCategory(Intent.CATEGORY_OPENABLE)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }


    private fun registerFileContentPicker() {

        mFilesContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                if (it.resultCode == Activity.RESULT_OK && it.data != null) {

                    val uri = it.data

                    uri?.data?.let {

                        PickiT(this, this, this).getPath(
                            it,
                            Build.VERSION.SDK_INT
                        )
                    }


                }
            }

    }


    override fun onBackPressed() {
        val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        if (item.itemId == R.id.nav_signatures) {
            moveToUserSignCollection()
        }

        mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    private fun moveToUserSignCollection() =
        mSignManager?.openSigningLibrary(this)

    @Throws(UnsupportedOperationException::class)
    override fun PickiTonUriReturned() {
    }

    @Throws(UnsupportedOperationException::class)
    override fun PickiTonStartListener() {
    }

    @Throws(UnsupportedOperationException::class)
    override fun PickiTonProgressUpdate(progress: Int) {
    }

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {

        if (wasSuccessful && !path.isNullOrBlank()) {
            mSignManager?.startSigning(File(path), "test1", this)
        } else {
            Toast.makeText(this, "Failed to pick file", Toast.LENGTH_SHORT).show()
        }

    }

    @Throws(UnsupportedOperationException::class)
    override fun PickiTonMultipleCompleteListener(
        paths: ArrayList<String>?,
        wasSuccessful: Boolean,
        Reason: String?
    ) {

    }


    //Sign methods
    override fun onSignResult(isSuccess: Boolean, resultFile: File?) {
        if (isSuccess) {
            resultFile?.let {
                mBinding.pdfSignAppBar.contentInclude.toDoEmptyView.isVisible = false
                mAdapter.addNewFile(it)
            }
        }


    }

    override fun onSignFailed(error: String) {

        Toast.makeText(this, "Failed to sign file with $error", Toast.LENGTH_SHORT).show()

    }

    override fun onItemClick(view: View?, value: File, position: Int) {

        mSignManager?.startSigning(value, returnedFileName = "anyName.pdf", this)
    }

}