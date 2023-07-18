package com.latribu.listadc.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.latribu.listadc.common.Constants.Companion.SEPARATOR_ITEM

class RecyclerViewItemDecoration(context: Context, resId: Int): RecyclerView.ItemDecoration() {
    private var divider: Drawable = ContextCompat.getDrawable(context, resId)!!

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val dividerLeft = 32
        val dividerRight: Int = parent.width - 16

        for (i in 0 until parent.childCount) {
            if (i != parent.childCount - 1) {
                val viewType = parent.adapter?.getItemViewType(i)
                if (viewType == SEPARATOR_ITEM) {
                    val child: View = parent.getChildAt(i)
                    val params = child.layoutParams as RecyclerView.LayoutParams

                    val dividerTop: Int = child.bottom + params.bottomMargin - 30
                    val dividerBottom: Int = dividerTop + divider.intrinsicHeight
                    divider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                    divider.draw(c)
                    break
                }
            }
        }
    }
}