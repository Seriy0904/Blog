package uz.urgench.blog

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class CustomFrame : FrameLayout {
    private var xDown: Float = 0F

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!,
        attrs,
        defStyle
    ) {
        // TODO Auto-generated constructor stub
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        // TODO Auto-generated constructor stub
    }

    constructor(context: Context?) : super(context!!) {
        // TODO Auto-generated constructor stub
    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        if (e != null) {
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                        xDown = e.x
                }
                MotionEvent.ACTION_MOVE -> {
                    if (xDown in 40F..240F && e.x-xDown>=80F) {
                        findViewById<DrawerLayout>(R.id.drawer_layout).openDrawer(GravityCompat.START)
                        return true
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(e)
    }
}