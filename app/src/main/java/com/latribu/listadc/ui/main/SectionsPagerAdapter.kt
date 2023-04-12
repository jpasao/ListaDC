package com.latribu.listadc.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.latribu.listadc.ListFragment
import com.latribu.listadc.MealFragment
import com.latribu.listadc.OtherFragment
import com.latribu.listadc.R

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3
)

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> { ListFragment() }
            1 -> { OtherFragment() }
            2 -> { MealFragment() }
            else -> { ListFragment() }
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }
}