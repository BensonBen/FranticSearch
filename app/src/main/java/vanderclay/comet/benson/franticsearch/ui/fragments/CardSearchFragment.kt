package vanderclay.comet.benson.franticsearch.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.SearchView
import io.magicthegathering.javasdk.resource.Card
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import vanderclay.comet.benson.franticsearch.R
import vanderclay.comet.benson.franticsearch.api.MtgAPI
import vanderclay.comet.benson.franticsearch.ui.activities.MainActivity
import vanderclay.comet.benson.franticsearch.ui.adapters.CardListAdapter
import vanderclay.comet.benson.franticsearch.ui.adapters.listeners.EndlessRecyclerViewScrollListener

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CardSearchFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CardSearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CardSearchFragment : Fragment(), SearchView.OnQueryTextListener {
    private val TAG = "CardSearchFragment"

    private var cardModel = mutableListOf<Card>()

    private var cardAdapter = CardListAdapter(cardModel)

    private var scrollListener: EndlessRecyclerViewScrollListener? = null

    private var cardFilter: String? = ""

    private val handler = Handler()

    private var cardList: RecyclerView? = null
    private var searchView: SearchView? = null
    private var searchText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if(searchText.isEmpty()) {
            loadNextDataFromApi(1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.search_menu, menu)
        val item = menu?.findItem(R.id.action_search)
        searchView = SearchView((context as MainActivity).supportActionBar?.themedContext)
        MenuItemCompat.setActionView(item, searchView)
        searchView?.setOnQueryTextListener(this)
        if(searchText.isNotEmpty()) {
            searchView?.setQuery(searchText, true)
            cardModel.clear()
            cardAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_card_search, container, false)
        rootView.tag = TAG
        cardList = rootView.findViewById(R.id.cardList) as RecyclerView

        val layoutManager = LinearLayoutManager(activity.applicationContext)
        cardList?.layoutManager = layoutManager
        scrollListener = object: EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(currentPage: Int): Boolean {
                loadNextDataFromApi(currentPage)
                return true
            }
        }

        cardList?.addOnScrollListener(scrollListener)
        cardList?.setHasFixedSize(true)
        cardList?.adapter = cardAdapter
        return rootView
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.title = activity.getString(R.string.card_search)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        cardFilter =  newText
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            cardModel.clear()
            cardAdapter.notifyDataSetChanged()
            loadNextDataFromApi(1)
            scrollListener?.resetState()

            cardList?.scrollToPosition(0)
        }, 500)

        return true
    }


    private fun loadNextDataFromApi(page: Int) {
        doAsync {
            val cards = MtgAPI.getCards(page, "name=$cardFilter", "orderBy=name")
            uiThread {
                cardModel.addAll(cards)
                Log.d(TAG, "Elements in array after search change ${cardModel.size}")
                cardAdapter.notifyDataSetChanged()
            }
        }
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.

         * @return A new instance of fragment CardSearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(): CardSearchFragment {
            val fragment = CardSearchFragment()
//            val args = Bundle()
            return fragment
        }

        fun newInstance(searchText: String): CardSearchFragment {
            val fragment = CardSearchFragment()

            fragment.searchText = searchText
            return fragment
        }
    }
}
