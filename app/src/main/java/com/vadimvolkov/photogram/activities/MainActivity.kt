package com.vadimvolkov.photogram.activities

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.vadimvolkov.photogram.R
import kotlinx.android.synthetic.main.bottom_navigation_view.*


abstract class MainActivity(val navNumber: Int) : AppCompatActivity() {

    private val TAG = "MainActivity"

    fun setupBottomNavigation() {
        bottom_navigation_view.setOnNavigationItemSelectedListener {
            val nextActivity =
                    when (it.itemId) {
                        R.id.item_home -> HomeActivity::class.java
                        R.id.item_search -> SearchActivity::class.java
                        R.id.item_share -> ShareActivity::class.java
                        R.id.item_like -> LikeActivity::class.java
                        R.id.item_profile -> ProfileActivity::class.java
                        else -> {
                            Log.e(TAG, "Unknown item clicked $it" )
                            null
                        }
                    }
            if (nextActivity != null) {
                val intent = Intent(this, nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                overridePendingTransition(0,0)  // for deactivate anim
                true
            } else {
                false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (bottom_navigation_view != null) {
            bottom_navigation_view.menu.getItem(navNumber).isChecked = true
        }
    }
}