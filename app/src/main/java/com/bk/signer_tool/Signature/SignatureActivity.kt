package com.bk.signer_tool.Signature

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.app.AlertDialog
import com.bk.signer_tool.Adapter.SignatureRecycleViewAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.bk.signer_tool.databinding.ComBkSignerActivitySignatureBinding
import com.bk.signer_tool.utils.ACTIVITY_ACTION
import com.bk.signer_tool.utils.FREE_HAND_FOLDER_NAME
import com.bk.signer_tool.utils.SUBMIT_FILE_NAME
import java.io.File


class SignatureActivity : AppCompatActivity(), SignatureRecycleViewAdapter.OnItemClickListener {

    private val FREE_HAND_FILE_PATH by lazy { "${filesDir.absolutePath}/$FREE_HAND_FOLDER_NAME" }
    private var mFreeHandIntentCallback: ActivityResultLauncher<Intent>? = null
    private var isNeedToBack: Boolean = false
    private val mAdapter: SignatureRecycleViewAdapter by lazy {
        SignatureRecycleViewAdapter(
            onClickListener = this
        )
    }

    private lateinit var mBinding: ComBkSignerActivitySignatureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ComBkSignerActivitySignatureBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        registerFreeHandCallback()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        mBinding.createSignatureFab.setOnClickListener {
            mFreeHandIntentCallback?.launch(
                Intent(
                    applicationContext,
                    FreeHandActivity::class.java
                )
            )
        }

        initRecycleViewerWithDataSource()
        isNeedToBack = intent.getBooleanExtra(ACTIVITY_ACTION, false)


    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }


    private fun registerFreeHandCallback() {

        mFreeHandIntentCallback =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                    it.data!!.getStringExtra(NEW_SIGN_URI)?.let {
                        mAdapter.addNewFile(it.toUri().toFile())
                        controlEmptyViewVisibility(false)
                    }
                }
            }

    }

    private fun initRecycleViewerWithDataSource() {


        mBinding.mainRecycleView.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(
                DividerItemDecoration(
                    this@SignatureActivity,
                    DividerItemDecoration.VERTICAL
                )
            )

            adapter = mAdapter
        }



        readItemsFromFile()
    }


    private fun controlEmptyViewVisibility(visibilityStatus: Boolean) {
        mBinding.toDoEmptyView.isVisible = visibilityStatus
    }


    private fun readItemsFromFile() {

        val myDir = File(FREE_HAND_FILE_PATH)

        if (!myDir.exists()) {
            myDir.mkdirs()
            return
        }

        val files = myDir.listFiles()

        if (!files.isNullOrEmpty()) {
            controlEmptyViewVisibility(false)
            files.sort()
            mAdapter.submitFiles(files.asList())
        }
    }


    private fun createDeleteDialog(obj: File) {

        AlertDialog.Builder(this@SignatureActivity)
            .setMessage("Are you sure you want to delete this Signature?")
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, id ->
                if (obj.exists()) {
                    obj.delete()

                    val isEmptyResult = mAdapter.removeItem(obj)
                    if (isEmptyResult)
                        controlEmptyViewVisibility(true)
                }
            }
            .setNegativeButton(
                android.R.string.cancel
            ) { dialog, id -> dialog.dismiss() }.create().show()

    }


    override fun onItemClick(view: View?, obj: File, pos: Int) {
        if (isNeedToBack) {
            val resultIntent = Intent()
            resultIntent.putExtra(SUBMIT_FILE_NAME, obj.path)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun onDeleteItemClick(view: View?, obj: File, pos: Int) {
        createDeleteDialog(obj)
    }

}