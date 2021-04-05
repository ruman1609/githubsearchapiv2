package com.rudyrachman16.favoritegithubuser.view.fragments

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rudyrachman16.favoritegithubuser.R
import com.rudyrachman16.favoritegithubuser.data.api.models.SearchUser
import com.rudyrachman16.favoritegithubuser.databinding.FragmentTabBinding
import com.rudyrachman16.favoritegithubuser.view.TouchCallback
import com.rudyrachman16.favoritegithubuser.view.activities.DetailActivity
import com.rudyrachman16.favoritegithubuser.view.activities.FavoriteActivity
import com.rudyrachman16.favoritegithubuser.view.adapters.ListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

class TabFragment : Fragment(), CoroutineScope {
    companion object {
        private const val KEY = "tabs_keys"
        private const val POSITION = "tabs_position"

        @JvmStatic
        fun newInstance(json: String, position: Int) = TabFragment().apply {
            arguments = Bundle().apply {
                putString(KEY, json)
                putInt(POSITION, position)
            }
        }
    }

    private var binding: FragmentTabBinding? = null
    private val bind get() = binding!!

    private var adaptered: ListAdapter? = null
    private val adapterList get() = adaptered!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val json = arguments?.getString(KEY, null)
        val position = arguments?.getInt(POSITION, -1)
        if (json != null) {
            val gson = Gson()
            val results: ArrayList<SearchUser> = gson.fromJson(
                json,
                object : TypeToken<ArrayList<SearchUser>>() {}.type
            )
            if (results.isEmpty()) {
                bind.information.text =
                    getString(R.string.no_list, getString(DetailActivity.tabTitle[position!!]))
                bind.information.visibility = View.VISIBLE
            }
            adaptered = ListAdapter(requireContext(), { user, fav, pos ->
                launch(coroutineContext) {
                    val short = async {
                        FavoriteActivity.favoriteShort(
                            user,
                            fav,
                            pos,
                            adapterList,
                            requireContext(),
                            false
                        )
                    }
                    short.await()
                }
            }) { it, bind ->
                adapterList.closeMenu()
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    requireActivity(),
                    Pair.create(bind.imageView, FavoriteActivity.transitions[0]),
                    Pair.create(bind.username, FavoriteActivity.transitions[1])
                )
                val move = Intent(requireActivity(), DetailActivity::class.java).apply {
                    putExtra(FavoriteActivity.USER_KEY, it)
                }
                startActivity(move, options.toBundle())
            }
            adapterList.setList(results)
            bind.recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = adapterList
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setOnScrollChangeListener { _, _, _, _, _ ->
                        adapterList.closeMenu()
                    }
                }
            }
            object : ItemTouchHelper(
                TouchCallback(
                    adapterList,
                    requireContext()
                )
            ) {}.attachToRecyclerView(bind.recyclerView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}