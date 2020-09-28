package com.devashish.machinetest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import com.devashish.machinetest.fragment.StockListFragment
import com.devashish.machinetest.viewmodel.ActivityViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), StockListFragment.CommunicateWithActivityViewModel {

    private val activityViewModel by lazy {
        ViewModelProvider(this).get(ActivityViewModel::class.java)
    }

    private val fragmentName = arrayOf("TAB A", "TAB B", "TAB C")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        tabLayout.setupWithViewPager(viewPagerContainer)

        viewPagerContainer.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(tabLayout)
        )

        tabLayout.addOnTabSelectedListener(
            TabLayout.ViewPagerOnTabSelectedListener(viewPagerContainer)
        )

        val adapter = StockListViewAdapter(supportFragmentManager, fragmentName)
        viewPagerContainer.adapter = adapter
    }

    class StockListViewAdapter(
        fragmentManager: FragmentManager, private val fragmentName: Array<String>
    ) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return StockListFragment.newInstance(fragmentName[position])
        }

        override fun getCount(): Int {
            return fragmentName.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentName[position]
        }

    }

    override fun setView1ScripData(scriptCodeData: HashMap<String, Int>) {
        activityViewModel.setView1ScripData(scriptCodeData)
    }

    override fun getView1ScripData(tabName: String): Int? {
        return activityViewModel.getView1ScripData(tabName)
    }

    override fun setView2ScripData(scriptCodeData: HashMap<String, Int>) {
        activityViewModel.setView2ScripData(scriptCodeData)
    }

    override fun getView2ScripData(tabName: String): Int? {
        return activityViewModel.getView2ScripData(tabName)
    }
}