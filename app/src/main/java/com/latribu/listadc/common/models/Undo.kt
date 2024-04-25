package com.latribu.listadc.common.models

import com.latribu.listadc.common.Constants.Companion.QUEUE_LIMIT
import com.latribu.listadc.common.Constants.Companion.TAB_MAINLIST
import com.latribu.listadc.common.Constants.Companion.TAB_MEALS
import com.latribu.listadc.common.Constants.Companion.TAB_OTHERS
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object Undo {
    private fun <E> dequeLimiter(): ReadWriteProperty<Any?, ArrayDeque<E>> =
    object : ReadWriteProperty<Any?, ArrayDeque<E>> {
        private var deque: ArrayDeque<E> = ArrayDeque(QUEUE_LIMIT)

        private fun applyLimit() {
            while (deque.size > QUEUE_LIMIT) {
                deque.removeFirst()
            }
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): ArrayDeque<E> {
            applyLimit()
            return deque
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: ArrayDeque<E>) {
            this.deque = value
            applyLimit()
        }
    }

    private val mainLimitedList: ArrayDeque<ProductItem> by dequeLimiter()
    private val mealLimitedList: ArrayDeque<Meal> by dequeLimiter()
    private val otherLimitedList: ArrayDeque<Other> by dequeLimiter()

    fun <T> addElement(element: T) {
        when (element) {
            is ProductItem -> {
                val toggledObject = element.copy(
                    id = element.id,
                    name = element.name,
                    isChecked = if (element.isChecked == "0") "1" else "0",
                    quantity = element.quantity,
                    comment = element.comment
                )
                this.mainLimitedList.add(toggledObject)
            }
            is Meal -> {
                val toggledObject = element.copy(
                    mealId = element.mealId,
                    name = element.name,
                    isLunch = element.isLunch,
                    isChecked = if (element.isChecked == 0) 1 else 0
                )
                this.mealLimitedList.add(toggledObject)
            }
            is Other -> {
                val toggledObject = element.copy(
                    id = element.id,
                    parentId = element.parentId,
                    parentName = element.parentName,
                    name = element.name,
                    isChecked = if (element.isChecked == 0) 1 else 0
                )
                this.otherLimitedList.add(toggledObject)
            }
        }
    }
    fun getElement(tab: Int): Serializable? {
        return when (tab) {
            TAB_MAINLIST -> { this.mainLimitedList.removeLastOrNull() }
            TAB_MEALS -> { this.mealLimitedList.removeLastOrNull() }
            TAB_OTHERS -> { this.otherLimitedList.removeLastOrNull() }
            else -> { this.mainLimitedList.removeLastOrNull() }
        }
    }
}