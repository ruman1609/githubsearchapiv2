package com.rudyrachman16.githubuserv2.view.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rudyrachman16.githubuserv2.view.activities.DetailActivity
import com.rudyrachman16.githubuserv2.view.fragments.TabFragment

class TabAdapter(activity: AppCompatActivity, private val userPlace: Array<String>) :
    FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = DetailActivity.tabTitle.size

    override fun createFragment(position: Int): Fragment =
        TabFragment.newInstance(userPlace[position], position)
}