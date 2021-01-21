package com.vadimvolkov.photogram.views

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.activities.*
import kotlinx.android.synthetic.main.bottom_navigation_view.*

class MyBottomNavigation (
    activity: Activity,
    private val bottomNavigationView: BottomNavigationView,
    private val navNumber: Int): LifecycleObserver
{
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
            bottomNavigationView.menu.getItem(navNumber).isChecked = true
    }

    init {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            val nextActivity =
                when (it.itemId) {
                    R.id.item_home -> HomeActivity::class.java
                    R.id.item_search -> SearchActivity::class.java
                    R.id.item_share -> ShareActivity::class.java
                    R.id.item_like -> LikeActivity::class.java
                    R.id.item_profile -> ProfileActivity::class.java
                    else -> {
                        Log.e(MainActivity.TAG, "Unknown item clicked $it")
                        null
                    }
                }
            if (nextActivity != null) {
                val intent = Intent(activity, nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                activity.overridePendingTransition(0,0)  // for deactivate anim
                activity.startActivity(intent)
                true
            } else {
                false
            }
        }
    }
}

fun MainActivity.setupBottomNavigation(navNumber: Int) {
    MyBottomNavigation(this, bottom_navigation_view, navNumber)
}