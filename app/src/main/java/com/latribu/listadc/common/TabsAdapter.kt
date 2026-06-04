package com.latribu.listadc.common
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants.Companion.TAB_BREAKFASTS
import com.latribu.listadc.common.Constants.Companion.TAB_MAINLIST
import com.latribu.listadc.common.Constants.Companion.TAB_MEALS
import com.latribu.listadc.common.Constants.Companion.TAB_OTHERS
import com.latribu.listadc.main.ListFragment
import com.latribu.listadc.meals.MealFragment
import com.latribu.listadc.others.OtherFragment

val TAB_ICONS = arrayOf(
    R.drawable.twotone_receipt_24,
    R.drawable.twotone_local_cafe_24,
    R.drawable.twotone_soup_kitchen_24,
    R.drawable.twotone_shopping_bag_24
)

class TabsAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    private val fragmentList = arrayOf(
        ListFragment(),
        MealFragment(true),
        MealFragment(false),
        OtherFragment(),
    )

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            TAB_MAINLIST -> { ListFragment() }
            TAB_BREAKFASTS -> { MealFragment(true) }
            TAB_MEALS -> { MealFragment(false) }
            TAB_OTHERS -> { OtherFragment() }
            else -> { ListFragment() }
        }
    }
    override fun getItemCount(): Int {
        return fragmentList.size
    }
}