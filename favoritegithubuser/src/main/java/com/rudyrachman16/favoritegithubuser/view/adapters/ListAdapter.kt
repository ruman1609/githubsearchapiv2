package com.rudyrachman16.favoritegithubuser.view.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rudyrachman16.favoritegithubuser.R
import com.rudyrachman16.favoritegithubuser.data.api.models.SearchUser
import com.rudyrachman16.favoritegithubuser.data.api.models.User
import com.rudyrachman16.favoritegithubuser.data.sqlite.FavUser
import com.rudyrachman16.favoritegithubuser.databinding.PerMenuBinding
import com.rudyrachman16.favoritegithubuser.databinding.PerViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ListAdapter(
    private val context: Context,
    private val favListener: (user: FavUser, isFav: Boolean, position: Int) -> Unit,
    private val clickListener: (user: User, binding: PerViewBinding) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), CoroutineScope {
    companion object {
        fun share(context: Context, user: SearchUser): Intent {
            val message =
                context.getString(R.string.share_value, user.id, user.username)
            return Intent().apply {
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
                action = Intent.ACTION_SEND
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
    }

    inner class ListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bind = PerViewBinding.bind(itemView)
        fun binding(user: SearchUser) {
            bind.username.text = user.username
            bind.id.text = user.id.toString()
            Glide.with(itemView.context)
                .load(user.picUrl)
                .apply(RequestOptions().override(100))
                .into(bind.imageView)
            bind.showMenu.setOnClickListener {
                showMenu(adapterPosition)
            }
        }
    }

    inner class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bind = PerMenuBinding.bind(itemView)
        fun binding(user: SearchUser) {
            bind.closeMenu.setOnClickListener {
                closeMenu(adapterPosition)
            }
            bind.shButton.setOnClickListener {
                val intent = share(context, user)
                context.startActivity(
                    Intent.createChooser(
                        intent,
                        context.getString(R.string.share_send)
                    ),
                )
            }
            var isFav = false
            launch(coroutineContext) {
                val job = launch(Dispatchers.IO) {
                    val uri = Uri.parse("${FavUser.CONTENT_URI}/${user.id}")
                    val cur = context.contentResolver.query(uri, null, null, null, null)!!.apply {
                        moveToFirst()
                        isFav = getInt(getColumnIndexOrThrow("result")) > 0
                    }
                    cur.close()
                }
                job.join()
                if (job.isCompleted) {
                    bind.favIcon.visibility = View.VISIBLE
                    bind.favText.visibility = View.VISIBLE
                    bind.loadingFav.visibility = View.GONE
                    setFavCond(isFav)
                    bind.favButton.setOnClickListener {
                        favListener(
                            FavUser(user.id, user.username, user.picUrl),
                            isFav,
                            adapterPosition
                        )
                        setFavCond(!isFav)
                    }
                }
            }
        }

        private fun setFavCond(fav: Boolean) {
            bind.favText.text =
                if (fav) context.getString(R.string.unfavorite) else context.getString(R.string.favorite)
            if (fav) bind.favIcon.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_favorite)
            ) else bind.favIcon.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_unfavorite)
            )
        }
    }

    private val results = ArrayList<SearchUser>()
    fun setList(result: ArrayList<SearchUser>) {
        results.clear()
        results.addAll(result)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == VIEWTYPE.SHOW.ordinal) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.per_menu, parent, false)
            MenuHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.per_view, parent, false)
            ListHolder(view)
        }

    override fun getItemViewType(position: Int): Int =
        if (results[position].showMenu) VIEWTYPE.SHOW.ordinal
        else VIEWTYPE.GONE.ordinal

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = results[position]
        when (holder) {
            is ListHolder -> {
                holder.binding(user)
                val userItem = User(user.id, user.username, user.picUrl)
                holder.itemView.setOnClickListener {
                    clickListener(userItem, PerViewBinding.bind(holder.itemView))
                }
            }
            is MenuHolder -> {
                holder.binding(user)
            }
        }
    }

    override fun getItemCount(): Int = results.size

    fun showMenu(position: Int) {
        closeMenu()
        results[position].showMenu = true
        notifyItemChanged(position)
    }

    fun isMenuShown(position: Int): Boolean = results[position].showMenu

    fun isMenuShown(): Boolean {
        results.forEach {
            if (it.showMenu) return true
        }
        return false
    }

    fun closeMenu(position: Int) {
        results[position].showMenu = false
        notifyItemChanged(position)
    }

    fun closeMenu() {
        results.forEachIndexed { i, it ->
            if (it.showMenu) {
                it.showMenu = false
                notifyItemChanged(i)
            }
        }
    }

    enum class VIEWTYPE {
        GONE, SHOW
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}