package com.latribu.listadc.common
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.latribu.listadc.R
import com.latribu.listadc.main.ListFragment
import com.latribu.listadc.meals.MealFragment
import com.latribu.listadc.others.OtherFragment

val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3
)

class TabsAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    private val fragmentList = arrayOf(
        ListFragment(),
        OtherFragment(),
        MealFragment()
    )

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> { ListFragment() }
            1 -> { OtherFragment() }
            2 -> { MealFragment() }
            else -> { ListFragment() }
        }
    }
    override fun getItemCount(): Int {
        return fragmentList.size
    }
}