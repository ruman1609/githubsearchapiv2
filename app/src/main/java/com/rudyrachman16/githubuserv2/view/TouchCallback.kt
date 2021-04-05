package com.rudyrachman16.githubuserv2.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.rudyrachman16.githubuserv2.R
import com.rudyrachman16.githubuserv2.view.adapters.ListAdapter

class TouchCallback(
    private val adapter: ListAdapter,
    private val context: Context,
    swipeDirs: Int = ItemTouchHelper.LEFT,
    dragDirs: Int = 0
) :
    ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
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
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val view = viewHolder.itemView
        val bg = ColorDrawable(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                ContextCompat.getColor(context, R.color.white25)
            else context.resources.getColor(R.color.black25)
        )
        if (dX < 0) bg.setBounds(view.right + dX.toInt(), view.top, view.right, view.bottom)
        else bg.setBounds(0, 0, 0, 0)
        bg.draw(c)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (!adapter.isMenuShown(viewHolder.adapterPosition))
            adapter.showMenu(viewHolder.adapterPosition)
        else adapter.closeMenu(viewHolder.adapterPosition)
    }
}