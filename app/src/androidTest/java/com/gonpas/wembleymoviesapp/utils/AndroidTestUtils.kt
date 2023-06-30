package com.gonpas.wembleymoviesapp.utils

import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.PerformException
import androidx.test.espresso.Root
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.util.TreeIterables
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import java.util.concurrent.TimeoutException


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
         * ViewAssertion que comprueba que el texto de un TextView perteneciente a
         * un item de RecyclerView es correcto
         *
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
            ViewMatchers.assertThat("RecyclerView child item's hasn't correct text: ",
                childView.text, CoreMatchers.`is`(texto) )
        }
    }

}

/**
 * ViewAction que ejecuta un click sobre un elemento de RecyclerView
 *
 * @param id el id del elemento a clickar
 */
fun clickItemChildWithId(id: Int): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View>? {
            return null
        }

        override fun getDescription(): String {
            return "Click on a child view with id: $id."
        }

        override fun perform(uiController: UiController, view: View) {
            val v = view.findViewById(id) as View
            v.performClick()
        }
    }
}

/**
 * Matcher que apunta a una view Toast
 */
class ToastMatcher: TypeSafeMatcher<Root>() {
    override fun matchesSafely(item: Root?): Boolean {
        if (item != null) {
            val type = item.windowLayoutParams.get().type
            if ((type == WindowManager.LayoutParams.TYPE_TOAST)){
                val windowToken = item.decorView.windowToken
                val appToken = item.decorView.applicationWindowToken
                if (windowToken == appToken){
                    return true
                }
            }
        }
        return false
    }

    override fun describeTo(description: Description?) {
        description?.appendText("es toast") ?: "Desconocido"
    }
}

/**
 * ViewAction que realiza escritura en un campo SearchView
 *
 * @param text texto a escribir
 */
fun typeTextAtSearchView(text: String): ViewAction {
    return object : ViewAction {
        override fun getDescription(): String {
            return "Type text at search view"
        }

        override fun getConstraints(): Matcher<View> {
            return allOf(isDisplayed(), isAssignableFrom(SearchView::class.java))
        }

        override fun perform(uiController: UiController?, view: View?) {
            (view as SearchView).setQuery(text, true)
        }
    }
}

/**
 * This ViewAction tells espresso to wait till a certain view is found in the view hierarchy.
 * @param viewId The id of the view to wait for.
 * @param timeout The maximum time which espresso will wait for the view to show up (in milliseconds)
 */
fun waitForView(viewId: Int, timeout: Long): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isRoot()
        }

        override fun getDescription(): String {
            return "wait for a specific view with id $viewId; during $timeout millis."
        }

        override fun perform(uiController: UiController, rootView: View) {
            uiController.loopMainThreadUntilIdle()
            val startTime = System.currentTimeMillis()
            val endTime = startTime + timeout
            val viewMatcher = withId(viewId)

            do {
                // Iterate through all views on the screen and see if the view we are looking for is there already
                for (child in TreeIterables.breadthFirstViewTraversal(rootView)) {
                    // found view with required ID
                    if (viewMatcher.matches(child)) {
                        return
                    }
                }
                // Loops the main thread for a specified period of time.
                // Control may not return immediately, instead it'll return after the provided delay has passed and the queue is in an idle state again.
                uiController.loopMainThreadForAtLeast(100)
            } while (System.currentTimeMillis() < endTime) // in case of a timeout we throw an exception -> test fails
            throw PerformException.Builder()
                .withCause(TimeoutException())
                .withActionDescription(this.description)
                .withViewDescription(HumanReadables.describe(rootView))
                .build()
        }
    }
}
