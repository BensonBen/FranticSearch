package vanderclay.comet.benson.franticsearch

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.content_search.*

class SearchActivity : AppCompatActivity(), OnQueryTextListener{

    private var _adapter: ArrayAdapter<String>? = null
    var adapter: ArrayAdapter<String>?
        get() = _adapter
        set(value) {
            _adapter = value
        }

    private var mLinearLayoutManager = LinearLayoutManager(this)
    private val cardListView by lazy {
        card_list_view.layoutManager = mLinearLayoutManager
    }

    private val toolbar by lazy {
        tool_bar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)

        val array: Array<String> = arrayOf("This", "Is", "A", "list", "Is", "A", "list", "Is", "A", "list", "Is", "A", "list", "Is", "A", "list", "Is", "A", "list", "Is", "A", "list", "Is", "A", "list", "Is", "A", "list", "Is", "A", "list", "Is", "A", "list", "Is", "A", "list")
        adapter = ArrayAdapter(
                this@SearchActivity,
                android.R.layout.simple_list_item_1,
                array)
        cardListView?.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMenuItem = menu?.findItem(R.id.searchMenuItem) as? MenuItem
        val searchView = MenuItemCompat.getActionView(searchMenuItem) as? SearchView
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if(TextUtils.isEmpty(newText)) {

        }
        adapter?.filter?.filter(newText)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }
}


