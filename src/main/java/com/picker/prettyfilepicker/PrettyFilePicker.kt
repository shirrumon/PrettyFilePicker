package com.picker.prettyfilepicker

import android.app.Activity
import android.app.AlertDialog
import android.os.Environment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.picker.prettyfilepicker.adapter.AdapterReDraw
import com.picker.prettyfilepicker.adapter.PickerListAdapter

class PrettyFilePicker(
    private val activity: Activity,
    private val title: String,
    private val returnAsDocumentFile: Boolean = false,
    private val filters: ArrayList<String> = arrayListOf()
) {
    private val builder = AlertDialog.Builder(activity)
    private lateinit var createdPickerInstance: AlertDialog
    var returnedData: MutableLiveData<Any> = MutableLiveData()

    fun runFilePicker(callback: (Any) -> Unit) {
        this.build()

        returnedData.observe(activity as LifecycleOwner) { returnedData ->
            callback.invoke(returnedData)
        }
    }

    private fun build() {
        val view = activity.layoutInflater.inflate(R.layout.alert_dialog_layout, null)
        val defaultPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                .toString()
        val adapter = PickerListAdapter(
            view,
            this,
            returnAsDocumentFile
        )
        val adapterReDrawer = AdapterReDraw(adapter)

        builder.setTitle(title)

        val recyclerView: RecyclerView = view.findViewById(R.id.alertDialogRecyclerView)

        recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        recyclerView.adapter = adapter

        adapter.submitList(adapterReDrawer.getFileList(defaultPath, view))
        builder.setView(view)

        adapterReDrawer.reDrawList(view)

        createdPickerInstance = builder.create()
        createdPickerInstance.show()
    }

    fun destroy() {
        createdPickerInstance.dismiss()
    }
}