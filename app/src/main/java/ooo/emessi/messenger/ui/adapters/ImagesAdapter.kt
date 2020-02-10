package ooo.emessi.messenger.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.DiffUtil
import ooo.emessi.messenger.R


class ImagesAdapter(val listener: (Pair<Boolean, String>, Int) -> Unit) : RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>(){


    var imagePaths = arrayListOf<Pair<Boolean, String>>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.bottom_attach_image_item, parent, false)
        return ImagesViewHolder(v)
    }

    override fun getItemCount(): Int {
        return imagePaths.size
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        holder.bind(imagePaths[position], listener, position)
    }


//    private fun performCheck(path: String, isChecked: Boolean) {
//        if (isChecked) selectedPaths.add(path)
//        else selectedPaths.remove(path)
//        d("BOTTOM SELECT IMAGES", selectedPaths.toString())
//    }

    fun update(paths: ArrayList<Pair<Boolean, String>>){
        val diffCallback = object : DiffUtil.Callback(){
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return imagePaths[oldItemPosition] == paths[newItemPosition]
            }

            override fun getOldListSize(): Int {
                return imagePaths.size
            }

            override fun getNewListSize(): Int {
                return paths.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return (imagePaths[oldItemPosition].second == paths[newItemPosition].second) && (imagePaths[oldItemPosition].first == paths[newItemPosition].first)
            }

        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        imagePaths = paths
        diffResult.dispatchUpdatesTo(this)
    }



    inner class ImagesViewHolder(view: View): RecyclerView.ViewHolder(view){
        val v = view
        private val ivItem = view.findViewById<ImageView>(R.id.iv_bottom_attach_item)
        private val cbItem = view.findViewById<CheckBox>(R.id.cb_bottom_attach_item)

        fun bind(
            path: Pair<Boolean, String>,
            listener: (Pair<Boolean, String>, Int) -> Unit,
            position: Int
        ){

            Glide.with(v)
                .load(path.second)
                .centerCrop()
                .placeholder(R.drawable.ic_image_black_24dp)
                .into(ivItem)
            v.setOnClickListener { listener.invoke(path, position) }
//            ivItem.setOnClickListener { cbItem.isChecked = !cbItem.isChecked }
            cbItem.isChecked = path.first
//            cbItem.setOnCheckedChangeListener { _, b -> performCheck(path, b) }

//            listener.invoke(selectedPaths)
        }
    }
}