package vanderclay.comet.benson.franticsearch.ui.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.toolbar.*
import vanderclay.comet.benson.franticsearch.R
import vanderclay.comet.benson.franticsearch.ui.fragments.CardSearchFragment
import vanderclay.comet.benson.franticsearch.ui.fragments.SettingsFragment
import com.google.android.gms.common.api.CommonStatusCodes
import vanderclay.comet.benson.franticsearch.ui.fragments.DeckListFragment
import vanderclay.comet.benson.franticsearch.ui.fragments.FavoriteFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "MainActivity"

    private val SCAN_INTENT = "SCAN_INTENT"
    private val DECK_INTENT = "DECK_INTENT"
    private val FAVORITES_INTENT = "FAVORITES_INTENT"

    var mDrawer: DrawerLayout? = null
    var nvDrawer: NavigationView? = null
    var drawerToggle: ActionBarDrawerToggle? = null
    private val RC_OCR_CAPTURE = 9003

    override fun onCreate(savedInstanceState: Bundle?) {
        if(FirebaseAuth.getInstance().currentUser == null) {
            goToLoginActivity()
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        mDrawer = findViewById(R.id.drawer_layout) as DrawerLayout?
        nvDrawer = findViewById(R.id.nvView) as NavigationView?
        drawerToggle = setUpDrawerToggle()
        mDrawer?.addDrawerListener(drawerToggle as DrawerLayout.DrawerListener)

        setupDrawerContent(nvDrawer)

    }

    override fun onResume() {
        super.onResume()
        if(intent.action == "SCAN_SUCCESS") {
            nvDrawer?.setCheckedItem(R.id.card_search)
            return
        }
        if(intent.action == SCAN_INTENT) {
            val intent = Intent(this, CardScanActivity::class.java)
            this.startActivityForResult(intent, RC_OCR_CAPTURE)
            title = getString(R.string.card_scan_shortcut)
            nvDrawer?.setCheckedItem(R.id.card_scan)
        }
        else if(intent.action == DECK_INTENT) {
            supportFragmentManager.beginTransaction().replace(R.id.flContent,
                    DeckListFragment.newInstance()).commit()
            title = getString(R.string.decks)
            nvDrawer?.setCheckedItem(R.id.decks)
        }
        else if(intent.action == FAVORITES_INTENT) {
            supportFragmentManager.beginTransaction().replace(R.id.flContent,
                    FavoriteFragment.newInstance()).commit()
            title = getString(R.string.favorites)
            nvDrawer?.setCheckedItem(R.id.decks)
        }
        else {
            supportFragmentManager.beginTransaction().replace(R.id.flContent,
                    CardSearchFragment.newInstance()).commit()
            title = getString(R.string.card_search)
            nvDrawer?.setCheckedItem(R.id.card_search)
        }
        intent.action = ""
    }

    private fun setUpDrawerToggle(): ActionBarDrawerToggle {
        return ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close)
    }

    private fun setupDrawerContent(navigationView: NavigationView?) {
        navigationView?.setNavigationItemSelectedListener {
            selectDrawerItem(it)
            true
        }
    }

    fun selectDrawerItem(menuItem: MenuItem) {
        val fragment = when(menuItem.itemId) {
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
            R.id.card_scan -> {
                val intent = Intent(this, CardScanActivity::class.java)
                this.startActivityForResult(intent, RC_OCR_CAPTURE)
                null
            }
            else -> CardSearchFragment.newInstance()
        }
        if(fragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit()

            menuItem.isChecked = true

            supportActionBar?.title = menuItem.title
            Log.d(TAG, "Transitioning to ${(menuItem.title as String)}")
        }
        mDrawer?.closeDrawers()
    }

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
        if(drawerToggle?.onOptionsItemSelected(item)!!) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.card_search) {
            // Handle the camera action
        } else if (id == R.id.decks) {

        } else if (id == R.id.card_scan) {

        } else if (id == R.id.settings_item) {

        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    val menuItem = nvDrawer?.menu?.findItem(R.id.card_scan)
                    val text = data.getStringExtra(CardScanActivity.TextBlockObject)
                    Log.d(TAG, "Text read: " + text)
                    val cardSearchFragment = CardSearchFragment.newInstance(text)
                    supportFragmentManager.beginTransaction().replace(R.id.flContent, cardSearchFragment).commitAllowingStateLoss()
                    menuItem?.isChecked = true
                    supportActionBar?.title = menuItem?.title
                    intent.action = "SCAN_SUCCESS"
                } else {
                    Log.d(TAG, "No Text captured, intent data is null")
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun goToLoginActivity() {
        val loginIntent = Intent(baseContext, LoginActivity::class.java)
        startActivity(loginIntent)
        finish()
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        goToLoginActivity()
    }
}
