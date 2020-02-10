package ooo.emessi.messenger.utils.helpers

import android.widget.ImageView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import ooo.emessi.messenger.R
import java.io.File

object AvatarHelper {
    fun placeRoundRectAvatar(imageView: ImageView, inputSrc:String? = null,  inputName: String = "", inputColor: String = ""){
        if (inputSrc == null) {

            val colorGen = ColorHelper.MATERIAL
            val color = colorGen.getColor(inputColor)

            val drawable = TextDrawable.builder()
                .buildRoundRect(inputName, color, 30)
            imageView.setImageDrawable(drawable)
        } else {
            val t = RoundedCornersTransformation(30, 0)
            Glide.with(imageView.context).load(File(inputSrc))
                .apply(RequestOptions.bitmapTransform(t))
                .error(R.drawable.ic_broken_image_black_24dp)
                .into(imageView)
        }
    }

    fun placeEmptyRoundRectAvatar(imageView: ImageView){
        val colorGen = ColorHelper.MATERIAL
        val color = colorGen.randomColor
        val drawable = TextDrawable.builder()
            .buildRoundRect("NAME", color, 30)
        imageView.setImageDrawable(drawable)
    }

    fun placeEmptyRoundAvatar(imageView: ImageView){
        val colorGen = ColorHelper.MATERIAL
        val color = colorGen.randomColor
        val drawable = TextDrawable.builder()
            .buildRound("Name", color)
        imageView.setImageDrawable(drawable)
    }

    fun placeRoundAvatar(imageView: ImageView, inputSrc:String? = null,  inputName: String? = null, inputColor: String? = null){
        if (inputSrc == null) {

            val colorGen = ColorHelper.MATERIAL
            val color = colorGen.getColor(inputColor)

            val drawable = TextDrawable.builder()
                .buildRound(inputName, color)
            imageView.setImageDrawable(drawable)
        } else {
            Glide.with(imageView.context).load(File(inputSrc))
                .error(R.drawable.ic_broken_image_black_24dp)
                .circleCrop()
                .into(imageView)
        }
    }
}