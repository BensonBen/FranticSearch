package vanderclay.comet.benson.franticsearch.ui.fragments


import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import io.magicthegathering.javasdk.resource.Card
import vanderclay.comet.benson.franticsearch.R
import vanderclay.comet.benson.franticsearch.commons.addManaSymbols
import vanderclay.comet.benson.franticsearch.commons.cardToMap
import vanderclay.comet.benson.franticsearch.model.Deck

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CardFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CardFragment : Fragment(), View.OnClickListener {

    // Reference to the card object the user pressed on
    var card: Card? = null

    // Reference to the firebase authorization object
    private var mAuth: FirebaseAuth? = null

    // Reference to the firebase Auth State Changed listener
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null

    // Reference to the Firebase Database
    private var mDatabase: DatabaseReference? = null

    /*Reference to the add Button in the view itself*/
    private var addButton: ImageButton? = null

    /*Reference to the favorites buttuon in the view itself*/
    private var favButton: ImageButton? = null

    /*Reference to the card set Text*/
    private var setText: TextView? = null

    /*Reference to the card converted mana cost*/
    private var cmcText: TextView? = null

    /*Reference to the card collector text*/
    private var collectorText: TextView? = null

    /*Reference to the power and toughness*/
    private var ptText: TextView? = null

    /*Reference to the card Image View*/
    private var cardImage: ImageView? = null

    /*Reference to the shopping cart add Button*/
    private var cardButton: ImageButton? = null

    /*Reference to the user currently signed in user*/
    private var user: FirebaseUser? = null

    /*Reference to the ability Text View in the Card Fragment*/
    private var abilityText: TextView? = null

    private var manaContainer: LinearLayout? = null


    private var arrayAdapter: ArrayAdapter<Deck>? = null

    private var decks: MutableList<Deck>? = null

    private var favorited = false

    private val mFavoriteDatabase = FirebaseDatabase.getInstance()
            .reference
            .child("Favorites")
            .child(FirebaseAuth.getInstance().currentUser?.uid)

    /*Reference to the Log Tag String for debugging*/
    val TAG: String = "CardFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d(TAG, " Getting reference to buttons and references to ")
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.fragment_card, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = card?.name

        addButton = rootView.findViewById(R.id.addButton) as ImageButton
        favButton = rootView.findViewById(R.id.favoriteButton) as ImageButton
        cardButton = rootView.findViewById(R.id.cartButton) as ImageButton

        //Set up the on click listeners
        addButton?.setOnClickListener(this)
        favButton?.setOnClickListener(this)
        cardButton?.setOnClickListener(this)

        setText = rootView.findViewById(R.id.cardSetText) as TextView
        cmcText = rootView.findViewById(R.id.cmcText) as TextView
        collectorText = rootView.findViewById(R.id.collectorText) as TextView
        ptText = rootView.findViewById(R.id.ptText) as TextView
        cardImage = rootView.findViewById(R.id.specificCardImage) as ImageView
        abilityText = rootView.findViewById(R.id.abilityText) as TextView
        manaContainer = rootView.findViewById(R.id.cardManaContainer) as LinearLayout

        decks = mutableListOf()
        arrayAdapter = ArrayAdapter(activity, android.R.layout.select_dialog_singlechoice, decks!!)

        setText?.text = card?.set

        if (card?.number != null) {
            collectorText?.text = card?.number
        } else {
            collectorText?.text = "n/a"
        }

        addManaSymbols(card, context, manaContainer)
        loadCardImage()

        if (card?.cmc != null) {
            cmcText?.text = round(card!!.cmc, 2)
        } else {
            cmcText?.text = "0"
        }

        if (card?.power != null && card?.toughness != null) {
            ptText?.text = card?.power + "/" + card?.toughness
        } else {
            ptText?.text = "0/0"
        }

        if (card?.originalText != null) {
            abilityText?.text = card?.originalText
        } else {
            abilityText?.text = "No ability"
        }

        //Firebase Setup
        this.mAuth = FirebaseAuth.getInstance()
        this.mDatabase = FirebaseDatabase.getInstance().getReference()

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            //Create a reference to the current user to get access to there UID
            //Used for database insertions and such
            user = firebaseAuth.currentUser
        }

        setFavoriteButton()

        return rootView
    }

    private fun setFavoriteButton(){
        findCardById(card?.id.toString(), { favorited ->
            if(favorited) {
                favButton?.setImageResource(android.R.drawable.star_on)
                this.favorited = true
            }
            else {
                favButton?.setImageResource(android.R.drawable.star_off)
                this.favorited = false
            }
        })
    }

    private fun round(value: Double, places: Int): String {
        var tempValue = value
        val factor: Long = Math.pow(10.0, places.toDouble()).toLong()
        tempValue *= factor
        var temp: Long = Math.round(tempValue)
        temp /= factor
        return temp.toString()
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            val a = activity
            if (a != null) {
            }
        }
    }

    // Bind a card to the ItemCardRow
    fun loadCardImage() {
        Picasso.with(activity)
                .load(this.card?.imageUrl)
                .placeholder(R.drawable.no_card)
                .into(cardImage)
    }

    /*
     * Implementation of the onclick listener in the card Fragment.
     */
    override fun onClick(view: View?) {
        val i = view?.id
        if (i == R.id.addButton) {
            addButtonPressed()
            Log.d(TAG, " Add Button Pressed... ")
        } else if (i == R.id.favoriteButton) {
            if(favorited) {
                removeFavorite(card!!)
            }
            else {
                addFavorite(card!!)
            }

            Log.d(TAG, " favorite Button Pressed ")

        } else if (i == R.id.cartButton) {
            cartButtonPressed()
        }
    }

    private fun cartButtonPressed() {
        val user = mAuth?.currentUser
        if (user != null) {
            val buyCardIntent = Intent(Intent.ACTION_VIEW)
            buyCardIntent.data = tcgPlayerLink()
            startActivity(buyCardIntent)
        } else {
//            showSnackBar("Wait a second for us to sign you in")
        }
    }

    private fun tcgPlayerLink() = Uri.Builder()
            .scheme("http")
            .authority("shop.tcgplayer.com")
            .appendPath("magic")
            .appendPath("product")
            .appendPath("show")
            .appendQueryParameter("ProductName", card?.name)
            .appendQueryParameter("newSearch", "false")
            .appendQueryParameter("ProductType", "ALL")
            .appendQueryParameter("IsProductName", "ALL").build()

    private fun addButtonPressed(){
        var  builderSingle: AlertDialog.Builder = AlertDialog.Builder(activity)
        builderSingle.setTitle("Choose a Deck to add To")

        decks?.clear()
        Deck.getAllDecks(decks!!, arrayAdapter)

        builderSingle.setAdapter(arrayAdapter) { dialog, which ->
            val deck = arrayAdapter?.getItem(which)
            val innerDialog = AlertDialog.Builder(activity)
            val amountInput = EditText(activity)
            innerDialog.setTitle("Enter number of cards to add")
            amountInput.inputType = InputType.TYPE_CLASS_NUMBER
            amountInput.setRawInputType(Configuration.KEYBOARD_12KEY)
            amountInput.setText("")
            amountInput.append("1")
            innerDialog.setView(amountInput)
            innerDialog.setPositiveButton("Add", { _, _ ->
                if(amountInput.text.isEmpty()) {
                    return@setPositiveButton
                }
                deck?.addCard(card!!, amountInput.text.toString().toLong())
            })
            innerDialog.setNegativeButton("Cancel", { _, _ ->
                dialog.dismiss()
                Log.d(TAG, "Add card cancelled")
            })
            innerDialog.show()
        }

        builderSingle.show()
    }


    private fun addFavorite(card: Card) {
        mFavoriteDatabase.child(card.id).setValue(cardToMap(card))
    }

    private fun removeFavorite(card: Card) {
        mFavoriteDatabase.child(card.id).removeValue()
    }

    private fun findCardById(primaryKey: String, callback: (favorited: Boolean) -> Unit) {
        var result = false
        val favoritesDatabaseRef = FirebaseDatabase
                .getInstance()
                .getReference("Favorites")
                .child(FirebaseAuth.getInstance().currentUser?.uid)
                .child(primaryKey)

        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.value != null){
                    Log.d("Favorites", dataSnapshot.value.toString())
                    callback(true)
                }else {
                    callback(false)
                }
                result = true
            }
        }

        favoritesDatabaseRef.addValueEventListener(valueEventListener)
//            return result
    }

    companion object {
        fun newInstance(card: Card): CardFragment {
            val fragment = CardFragment()
            fragment.card = card
            return fragment
        }

    }
}
