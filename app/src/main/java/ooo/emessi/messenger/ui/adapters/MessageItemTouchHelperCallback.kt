package ooo.emessi.messenger.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ooo.emessi.messenger.R
import ooo.emessi.messenger.constants.Constants
import ooo.emessi.messenger.data.model.view_item_model.message.MessageListViewItem
import ooo.emessi.messenger.utils.toPx
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.min

class MessageItemTouchHelperCallback(
    val adapter: MessagesAdapter,
    val swipeListener: (MessageListViewItem) -> Unit
) : ItemTouchHelper.Callback() {

    val BASE_OFFCET_DP = 80

    private lateinit var imageDrawable: Drawable
    private lateinit var shareRound: Drawable

    private var currentItemViewHolder: RecyclerView.ViewHolder? = null
    private lateinit var mView: View
    private var dX = 0f

    private var replyButtonProgress: Float = 0.toFloat()
    private var lastReplyButtonAnimationTime: Long = 0
    private var swipeBack = false
    private var isVibrate = false
    private var startTracking = false

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val vh = viewHolder as MessagesAdapter.AbstractMessageListItemsViewHolder
        mView = vh.v
        imageDrawable = recyclerView.context.getDrawable(R.drawable.ic_reply_black_24dp)!!
        shareRound = recyclerView.context.getDrawable(R.drawable.bg_indicator_all)!!
        return if (viewHolder is ItemSwipeble)
            makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.START)
        else
            makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.ACTION_STATE_IDLE)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = false
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            setTouchListener(recyclerView, viewHolder)
        }

        if (abs(mView.translationX) < (100).toPx(recyclerView.context) || dX < this.dX) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            this.dX = dX
            startTracking = true
        }
        if (abs(mView.translationX) > (100).toPx(recyclerView.context)) mView.translationX =
            (-100).toPx(recyclerView.context).toFloat()
        currentItemViewHolder = viewHolder
        drawReplyButton(c, recyclerView.context)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        recyclerView.setOnTouchListener { _, event ->
            swipeBack =
                event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            if (swipeBack) {
                if (abs(mView.translationX) >= (80).toPx(recyclerView.context)) {
                    swipeListener.invoke(adapter.messages[viewHolder.adapterPosition])
                }
            }
            false
        }
    }

    private fun drawReplyButton(canvas: Canvas, context: Context) {
        if (currentItemViewHolder == null) {
            return
        }
        val translationX = abs(mView.translationX)
        val newTime = System.currentTimeMillis()
//        val dt = min(17, newTime - lastReplyButtonAnimationTime)
//        lastReplyButtonAnimationTime = newTime
        val dt = 15
        val showing = translationX >= (20).toPx(context)
        Timber.i("sh - $showing")
        if (showing) {
            if (replyButtonProgress < 1.0f) {
                replyButtonProgress += dt / 80.0f
                if (replyButtonProgress > 1.0f) {
                    replyButtonProgress = 1.0f
                } else {
                    mView.invalidate()
                }
            }
        } else if (translationX <= 0.0f) {
            replyButtonProgress = 0f
            startTracking = false
            isVibrate = false
        } else {
            if (replyButtonProgress > 0.0f) {
                replyButtonProgress -= dt / 80.0f
                if (replyButtonProgress < 0.1f) {
                    replyButtonProgress = 0f
                } else {
                    mView.invalidate()
                }
            }
        }
        val alpha: Int
        val scale: Float
        if (showing) {
            scale = if (replyButtonProgress <= 0.8f) {
                1.2f * (replyButtonProgress / 0.8f)
            } else {
                1.2f - 0.2f * ((replyButtonProgress - 0.8f) / 0.2f)
            }
//            scale = 1.0f
            alpha = min(255f, 255 * (replyButtonProgress / 0.8f)).toInt()
        } else {
            scale = replyButtonProgress
            alpha = min(255f, 255 * replyButtonProgress).toInt()
        }
        shareRound.alpha = alpha
        imageDrawable.alpha = alpha
        if (startTracking) {
            if (!isVibrate && abs(mView.translationX) >= (80).toPx(context)) {
//                mView.performHapticFeedback(
//                    HapticFeedbackConstants.KEYBOARD_TAP,
//                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
//                )
                vibrate(context)
                isVibrate = true
            }
        }

        val x: Int = if (abs(mView.translationX) > (100).toPx(context)) {
            calcWidth(context) - (100).toPx(context) / 2
        } else {
            calcWidth(context) - (abs(mView.translationX) / 2).toInt()
        }

        val y = (mView.top + mView.measuredHeight / 2).toFloat()
//        shareRound.colorFilter =
//            PorterDuffColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)

        shareRound.setBounds(
            (x - (16).toPx(context) * scale).toInt(),
            (y - (16).toPx(context) * scale).toInt(),
            (x + (16).toPx(context) * scale).toInt(),
            (y + (16).toPx(context) * scale).toInt()
        )
        shareRound.draw(canvas)
        imageDrawable.setBounds(
            (x - (12).toPx(context) * scale).toInt(),
            (y - (12).toPx(context) * scale).toInt(),
            (x + (12).toPx(context) * scale).toInt(),
            (y + (12).toPx(context) * scale).toInt()
        )
        imageDrawable.draw(canvas)
        shareRound.alpha = 255
        imageDrawable.alpha = 255
    }

    private fun vibrate(context: Context) {
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(
                VibrationEffect.createOneShot(
                    Constants.VIBRATE_LENGTH,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            //deprecated in API 26
            v.vibrate(Constants.VIBRATE_LENGTH)
        }
    }

    private fun calcWidth(context: Context): Int {
        val w = context.resources.displayMetrics.widthPixels
        Timber.i(w.toString())
        return w
    }
}