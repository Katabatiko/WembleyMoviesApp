package com.gonpas.wembleymoviesapp.utils

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher

class ItemChildViewVisibilityAssertion {
    companion object {
        /**
         * Comprueba que una vista de un item de un RecyclerView es visible
         * @param viewId el id de la vista a comprobar
         */
        fun isItemChildViewVisible(viewId: Int): ViewAssertion {
            return RecyclerViewItemChildVisibilityAssertion(viewId)
        }

        /**
         * Comprueba que una vista de un item de un RecyclerView NO es visible
         * @param viewId el id de la vista a comprobar
         */
        fun isItemChildViewNotVisible(viewId: Int): ViewAssertion {
            return !RecyclerViewItemChildVisibilityAssertion(viewId)
        }
    }

    class RecyclerViewItemChildVisibilityAssertion(val viewId: Int): ViewAssertion{
        override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
            if (noViewFoundException != null) {
                throw noViewFoundException
            }
            if (view !is RecyclerView) {
                throw IllegalStateException("The asserted view is not RecyclerView")
            }

            val childView = view.findViewById<View>(viewId)
            ViewMatchers.assertThat("RecyclerView child item's is visible: ",
                childView?.visibility,
                CoreMatchers.equalTo(View.VISIBLE)
            )
        }

        operator fun not(): ViewAssertion  {

            return ViewAssertion{ view, _ ->
                val childView = view.findViewById<View>(viewId)
                ViewMatchers.assertThat("RecyclerView child item's is NOT visible: ",
                    childView?.visibility,
                    CoreMatchers.not(View.VISIBLE))
            }
        }
    }

}

class ItemChildTextViewCorrectText{
    companion object{
        /**
         * Comprueba que el texto de un TextView perteneciente a un item de RecyclerView
         * es correcto
         * @param viewId el id del TextView
         * @param text el texto de referencia
         */
        fun isTextItemChildCorrect(viewId: Int, text: String): ViewAssertion{
            return RecyclerViewItemChildCorrectTextAssertion(viewId, text)
        }
    }

    private class RecyclerViewItemChildCorrectTextAssertion(val viewId: Int, val texto: String): ViewAssertion{
        override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
            if (noViewFoundException != null) {
                throw noViewFoundException
            }

            val childView = view?.findViewById<View>(viewId) as TextView
            ViewMatchers.assertThat("RecyclerView child item's has correct text: ",
                childView.text, CoreMatchers.`is`(texto) )
        }
    }

}

fun clickItemChildWithId(id: Int): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View>? {
            return null
        }

        override fun getDescription(): String {
            return "Click on a child view with id: $id."
        }

        override fun perform(uiController: UiController, view: View) {
            val v = view.findViewById<View>(id) as View
            v.performClick()
        }
    }
}
