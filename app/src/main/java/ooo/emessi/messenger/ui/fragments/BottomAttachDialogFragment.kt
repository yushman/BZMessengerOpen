package ooo.emessi.messenger.ui.fragments

import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ooo.emessi.messenger.App
import ooo.emessi.messenger.R
import ooo.emessi.messenger.ui.adapters.ImagesAdapter
import java.lang.Exception

class BottomAttachDialogFragment(private val listener:(View, List<String>) -> Unit): BottomSheetDialogFragment() {

    private lateinit var ivCamera: ImageView
    private lateinit var ivGallery: ImageView
    private lateinit var ivStorage: ImageView
    private lateinit var ivSend: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var imagesAdapter: ImagesAdapter
    val selectedPaths = arrayListOf<String>()
    var paths = mutableListOf<Pair<Boolean, String>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_bottom_attach_dialog, container, false)
        ivCamera = v.findViewById(R.id.iv_bottom_attach_camera)
        ivGallery = v.findViewById(R.id.iv_bottom_attach_gallery)
        ivStorage = v.findViewById(R.id.iv_bottom_attach_storage)
        ivSend = v.findViewById(R.id.iv_bottom_attach_send)
        recyclerView = v.findViewById(R.id.rv_bottom_attach)
        imagesAdapter = ImagesAdapter{ pair, i ->  updateUI(pair, i)}

        val lm = GridLayoutManager(this.context, 3)
        recyclerView.layoutManager = lm
        recyclerView.itemAnimator = null

        recyclerView.adapter = imagesAdapter


        ivSend.isEnabled = false

        ivCamera.setOnClickListener { ivClick(it) }
        ivGallery.setOnClickListener { ivClick(it) }
        ivStorage.setOnClickListener { ivClick(it) }
        ivSend.setOnClickListener { ivClick(it) }
        loadGalleryPhotosAlbums()
        return v
    }

    private fun updateUI(it: Pair<Boolean, String>, i: Int) {
        paths[i] = !it.first to it.second
        if (!it.first) selectedPaths.add(it.second)
        else selectedPaths.remove(it.second)
//        if (it.size == 10) Toast.makeText(this.context, "Maximum 10 images", Toast.LENGTH_LONG).show()
        ivSend.isEnabled = selectedPaths.isNotEmpty()
        imagesAdapter.update(paths as ArrayList<Pair<Boolean, String>>)
        imagesAdapter.notifyItemChanged(i)
    }

    private fun ivClick(it: View) {
        listener.invoke(it, selectedPaths)
        dismiss()
    }

    fun loadGalleryPhotosAlbums() = CoroutineScope(Dispatchers.IO).launch{
        withContext(Dispatchers.Main) {
            val columns = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
            var cursor: Cursor? = null
            val paths = arrayListOf<String>()
            try {
//                if (SDK_INT > O) {
//                    cursor = App.applicationContext()!!.contentResolver.query(
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        columns, null, null
//                    )
//                    if (cursor != null) {
//                        val dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
//
//                        while (cursor.moveToNext()) {
//                            val path = cursor.getString(dataColumn)
//                            if (!TextUtils.isEmpty(path)) {
//                                paths.add(path)
//                            }
//                        }
//                    }
//                } else {
                cursor = MediaStore.Images.Media.query(
                    App.applicationContext()!!.contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    columns, null, "date_added Desc"
                )
                if (cursor != null) {
                    val dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA)

                    while (cursor.moveToNext()) {
                        val path = cursor.getString(dataColumn)
                        if (!TextUtils.isEmpty(path)) {
                            paths.add(path)
                        }
                    }
                }
//                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            paths.forEach {
                this@BottomAttachDialogFragment.paths.add(false to it)
            }

            imagesAdapter.update(this@BottomAttachDialogFragment.paths as ArrayList<Pair<Boolean, String>>)
        }
    }
}