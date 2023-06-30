package com.gonpas.wembleymoviesapp.utils

import android.view.View
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import org.hamcrest.Matcher


/**
 * Espresso actions for interacting with a [ViewPager2].
 *
 * @Author: adil-hussain-84 at github.com
 *
 * The implementation of this class has been copied over from
 * [ViewPagerActions.java](https://github.com/android/android-test/blob/master/espresso/contrib/java/androidx/test/espresso/contrib/ViewPagerActions.java)
 * with a small number of modifications to make it work for
 * [ViewPager2] instead of
 * [ViewPager](https://developer.android.com/reference/kotlin/androidx/viewpager/widget/ViewPager).
 *
 *
 * I have created an issue in the ViewPager2 IssueTracker space
 * [here](https://issuetracker.google.com/issues/207785217)
 * requesting for this class to be added in a `viewpager2-testing` library within the
 * [androidx.viewpager2](https://maven.google.com/web/index.html#androidx.viewpager2)
 * group.
 */
object ViewPager2Actions {
    private const val DEFAULT_SMOOTH_SCROLL = false
    /**
     * Moves [ViewPager2] to the right by one page.
     */
    @JvmOverloads
    fun scrollRight(smoothScroll: Boolean = DEFAULT_SMOOTH_SCROLL): ViewAction {
        return object : ViewPagerScrollAction() {
            override fun performScroll(viewPager: ViewPager2?) {
                val current = viewPager?.currentItem
                if (current != null) {
                    viewPager.setCurrentItem(current + 1, smoothScroll)
                }
            }

            override fun getDescription(): String {
                return "ViewPager2 move one page to the right"
            }
        }
    }
    /**
     * Moves [ViewPager2] to the left by one page.
     */
    @JvmOverloads
    fun scrollLeft(smoothScroll: Boolean = DEFAULT_SMOOTH_SCROLL): ViewAction {
        return object : ViewPagerScrollAction() {
            override fun performScroll(viewPager: ViewPager2?) {
                val current = viewPager?.currentItem
                if (current != null) {
                    viewPager.setCurrentItem(current - 1, smoothScroll)
                }
            }

            override fun getDescription(): String {
                return "ViewPager2 move one page to the left"
            }
        }
    }
    /**
     * Moves [ViewPager2] to the last page.
     */
    @JvmOverloads
    fun scrollToLast(smoothScroll: Boolean = DEFAULT_SMOOTH_SCROLL): ViewAction {
        return object : ViewPagerScrollAction() {
            override fun performScroll(viewPager: ViewPager2?) {
                val size = viewPager?.adapter!!.itemCount
                if (size > 0) {
                    viewPager.setCurrentItem(size - 1, smoothScroll)
                }
            }

            override fun getDescription(): String {
                return "ViewPager2 move to last page"
            }
        }
    }
    /**
     * Moves [ViewPager2] to the first page.
     */
    @JvmOverloads
    fun scrollToFirst(smoothScroll: Boolean = DEFAULT_SMOOTH_SCROLL): ViewAction {
        return object : ViewPagerScrollAction() {
            override fun getDescription(): String {
                return "ViewPager2 move to first page"
            }

            override fun performScroll(viewPager: ViewPager2?) {
                val size = viewPager?.adapter!!.itemCount
                if (size > 0) {
                    viewPager.setCurrentItem(0, smoothScroll)
                }
            }
        }
    }
    /**
     * Moves [ViewPager2] to a specific page.
     */
    @JvmOverloads
    fun scrollToPage(page: Int, smoothScroll: Boolean = DEFAULT_SMOOTH_SCROLL): ViewAction {
        return object : ViewPagerScrollAction() {
            override fun getDescription(): String {
                return "ViewPager2 move to page"
            }

            override fun performScroll(viewPager: ViewPager2?) {
                viewPager?.setCurrentItem(page, smoothScroll)
            }
        }
    }

    /**
     * [ViewPager2] listener that serves as Espresso's [IdlingResource] and notifies the
     * registered callback when the [ViewPager2] gets to SCROLL_STATE_IDLE state.
     */
    private class CustomViewPager2Listener : OnPageChangeCallback(), IdlingResource {
        private var mCurrState = ViewPager2.SCROLL_STATE_IDLE
        private var mCallback: IdlingResource.ResourceCallback? = null
        var mNeedsIdle = false
        override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
            mCallback = resourceCallback
        }

        override fun getName(): String {
            return "ViewPager2 listener"
        }

        override fun isIdleNow(): Boolean {
            return if (!mNeedsIdle) {
                true
            } else {
                mCurrState == ViewPager2.SCROLL_STATE_IDLE
            }
        }

        override fun onPageSelected(position: Int) {
            if (mCurrState == ViewPager2.SCROLL_STATE_IDLE) {
                if (mCallback != null) {
                    mCallback!!.onTransitionToIdle()
                }
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            mCurrState = state
            if (mCurrState == ViewPager2.SCROLL_STATE_IDLE) {
                if (mCallback != null) {
                    mCallback!!.onTransitionToIdle()
                }
            }
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }
    }

    private abstract class ViewPagerScrollAction : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return ViewMatchers.isDisplayed()
        }

        override fun perform(uiController: UiController, view: View) {
            val viewPager = view as ViewPager2

            // Add a custom tracker listener
            val customListener = CustomViewPager2Listener()
            viewPager.registerOnPageChangeCallback(customListener)

            // Note that we're running the following block in a try-finally construct.
            // This is needed since some of the actions are going to throw (expected) exceptions.
            // If that happens, we still need to clean up after ourselves
            // to leave the system (Espresso) in a good state.
            try {
                // Register our listener as idling resource so that Espresso waits until the
                // wrapped action results in the ViewPager2 getting to the SCROLL_STATE_IDLE state
                IdlingRegistry.getInstance().register(customListener)
                uiController.loopMainThreadUntilIdle()
                performScroll(view)
                uiController.loopMainThreadUntilIdle()
                customListener.mNeedsIdle = true
                uiController.loopMainThreadUntilIdle()
                customListener.mNeedsIdle = false
            } finally {
                // Unregister our idling resource
                IdlingRegistry.getInstance().unregister(customListener)
                // And remove our tracker listener from ViewPager2
                viewPager.unregisterOnPageChangeCallback(customListener)
            }
        }

        protected abstract fun performScroll(viewPager: ViewPager2?)
    }
}