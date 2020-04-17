package vanderclay.comet.benson.franticsearch.ui.activities

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import vanderclay.comet.benson.franticsearch.R
import vanderclay.comet.benson.franticsearch.ui.fragments.CardSearchFragment
import vanderclay.comet.benson.franticsearch.ui.fragments.SettingsFragment
import com.google.android.material.navigation.NavigationView
import vanderclay.comet.benson.franticsearch.ui.fragments.DeckListFragment
import vanderclay.comet.benson.franticsearch.ui.fragments.FavoriteFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val mainActivityTag = "MainActivity"

//    private val scanIntent = "SCAN_INTENT"
//    private val deckIntent = "DECK_INTENT"
//    private val favoritesIntent = "FAVORITES_INTENT"

    private var drawerLayout: DrawerLayout? = null
    private var toolbar: Toolbar? = null
    private var navigationView: NavigationView? = null
    private var drawerToggle: ActionBarDrawerToggle? = null
//    private val rcOcrCapture = 9003

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
//            if (FirebaseAuth.getInstance().currentUser == null) {
//                 goToLoginActivity()
//            }
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            drawerLayout = findViewById(R.id.drawer_layout)
            toolbar = findViewById(R.id.main_toolbar)
            navigationView = findViewById(R.id.nav_view)
            setSupportActionBar(toolbar)

            drawerToggle = ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_close,
                R.string.drawer_open
            )
            drawerLayout?.addDrawerListener(drawerToggle!!)
            drawerToggle?.syncState()
            navigationView?.setNavigationItemSelectedListener(this)
//            drawerToggle = setUpDrawerToggle()
//            mDrawer?.addDrawerListener(drawerToggle as DrawerLayout.DrawerListener)
//
//            setupDrawerContent(nvDrawer)
        } catch (e: Exception) {
            Log.d(mainActivityTag, e.message)
        }
    }

//    override fun onResume() {
//        super.onResume()
//        if(intent.action == "SCAN_SUCCESS") {
//            nvDrawer?.setCheckedItem(R.id.card_search)
//            return
//        }
//        when (intent.action) {
//            scanIntent -> {
//                val intent = Intent(this, CardScanActivity::class.java)
//                this.startActivityForResult(intent, rcOcrCapture)
//                title = getString(R.string.card_scan_shortcut)
//                nvDrawer?.setCheckedItem(R.id.card_scan)
//            }
//            deckIntent -> {
//                supportFragmentManager.beginTransaction().replace(R.id.flContent,
//                    DeckListFragment.newInstance()).commit()
//                title = getString(R.string.decks)
//                nvDrawer?.setCheckedItem(R.id.decks)
//            }
//            favoritesIntent -> {
//                supportFragmentManager.beginTransaction().replace(R.id.flContent,
//                    FavoriteFragment.newInstance()).commit()
//                title = getString(R.string.favorites)
//                nvDrawer?.setCheckedItem(R.id.decks)
//            }
//            else -> {
//                supportFragmentManager.beginTransaction().replace(R.id.flContent,
//                    CardSearchFragment.newInstance()).commit()
//                title = getString(R.string.card_search)
//                nvDrawer?.setCheckedItem(R.id.card_search)
//            }
//        }
//        intent.action = ""
//    }

//    private fun setUpDrawerToggle(): ActionBarDrawerToggle {
//        return ActionBarDrawerToggle(this, drawerLayout, toolbar as Toolbar?, R.string.drawer_open, R.string.drawer_close)
//    }
//
//    private fun setupDrawerContent(navigationView: NavigationView?) {
//        navigationView?.setNavigationItemSelectedListener {
//            selectDrawerItem(it)
//            true
//        }
//    }

//    private fun selectDrawerItem(menuItem: MenuItem) {
//        val fragment = when(menuItem.itemId) {
//            R.id.card_search -> {
//                CardSearchFragment.newInstance()
//            }
//            R.id.settings_item -> {
//                SettingsFragment.newInstance()
//            }
//            R.id.decks -> {
//                DeckListFragment.newInstance()
//            }
//            R.id.favorites -> {
//                FavoriteFragment.newInstance()
//            }
//            R.id.card_scan -> {
//                val intent = Intent(this, CardScanActivity::class.java)
//                this.startActivityForResult(intent, rcOcrCapture)
//                null
//            }
//            else -> CardSearchFragment.newInstance()
//        }
//        if(fragment != null) {
////            supportFragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit()
//
//            menuItem.isChecked = true
//
//            supportActionBar?.title = menuItem.title
//            Log.d(mainActivityTag, "Transitioning to ${(menuItem.title as String)}")
//        }
//        drawerLayout?.closeDrawers()
//    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE 1: Make sure to override the method with only a single `Bundle` argument
    // Note 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.
    // There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle?.syncState()
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggles
        drawerToggle?.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle?.onOptionsItemSelected(item)!!) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (menuItem.itemId) {
            R.id.card_search -> {
                CardSearchFragment.newInstance()
            }
            R.id.settings_item -> {
                SettingsFragment.newInstance()
            }
            R.id.decks -> {
                DeckListFragment.newInstance()
            }
            R.id.favorites -> {
                FavoriteFragment.newInstance()
            }
//            R.id.card_scan -> {
//                val intent = Intent(this, CardScanActivity::class.java)
//                this.startActivityForResult(intent, rcOcrCapture)
//                null
//            }
//            else -> CardSearchFragment.newInstance()
            else -> null
        }

//        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == rcOcrCapture) {
//            if (resultCode == CommonStatusCodes.SUCCESS) {
//                if (data != null) {
//                    val menuItem = nvDrawer?.menu?.findItem(R.id.card_scan)
//                    val text = data.getStringExtra(CardScanActivity.TextBlockObject) ?: ""
//                    Log.d(mainActivityTag, "Text read: $text")
//                    val cardSearchFragment = CardSearchFragment.newInstance(text)
////                    supportFragmentManager.beginTransaction()
////                        .replace(R.id.flContent, cardSearchFragment).commitAllowingStateLoss()
//                    menuItem?.isChecked = true
//                    supportActionBar?.title = menuItem?.title
//                    intent.action = "SCAN_SUCCESS"
//                } else {
//                    Log.d(mainActivityTag, "No Text captured, intent data is null")
//                }
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data)
//        }
//    }

//    private fun goToLoginActivity() {
//        val loginIntent = Intent(baseContext, LoginActivity::class.java)
//        startActivity(loginIntent)
//        finish()
//    }

//    fun logout() {
//        FirebaseAuth.getInstance().signOut()
//        goToLoginActivity()
//    }
}
