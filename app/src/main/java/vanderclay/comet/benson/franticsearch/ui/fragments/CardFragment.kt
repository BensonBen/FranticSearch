package vanderclay.comet.benson.franticsearch.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import io.magicthegathering.javasdk.resource.Card
import vanderclay.comet.benson.franticsearch.R
import vanderclay.comet.benson.franticsearch.commons.addManaSymbols
import vanderclay.comet.benson.franticsearch.model.Deck
import vanderclay.comet.benson.franticsearch.model.Favorite
import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CardFragment] interface
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

    /**/
    private var favorites: Favorite? = null

    //Tcg player link
    private val tcgPlayer = "http://shop.tcgplayer.com/magic/product/show?ProductName="

    /*End of the string for tcg player links*/
    private val productType = "newSearch=false&ProductType=All&IsProductNameExact=true"

    private var arrayAdapter: ArrayAdapter<Deck>? = null

    private var decks: MutableList<Deck>? = null

    private var favorited = false

    /*Reference to the Log Tag String for debugging*/
    private val cardFragmentTag: String = "CardFragment"

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(cardFragmentTag, " Getting reference to buttons and references to ")
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_card, container, false)
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
        arrayAdapter = ArrayAdapter(activity as AppCompatActivity, android.R.layout.select_dialog_singlechoice, decks!!)

        favorites = Favorite()

        setText?.text = card?.set

        if (card?.number != null) {
            collectorText?.text = card?.number
        } else {
            collectorText?.text = "n/a"
        }

        addManaSymbols(card, context, manaContainer)
        loadCardImage()

        if (card?.cmc != null) {
            cmcText?.text = round(card!!.cmc)
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
            abilityText?.text = " no ability "
        }

        //Firebase Setup
        this.mAuth = FirebaseAuth.getInstance()
        this.mDatabase = FirebaseDatabase.getInstance().reference

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            user = firebaseAuth.currentUser
        }

        setFavoriteButton()

        return rootView
    }

    private fun setFavoriteButton(){
        Favorite.findCardById(card?.id.toString()) { favored ->
            if(favored) {
                favButton?.setImageResource(android.R.drawable.star_on)
                this.favorited = true
            } else {
                favButton?.setImageResource(android.R.drawable.star_off)
                this.favorited = false
            }
        }
    }

    private fun round(value: Double): String {
        var tempValue = value
        val factor: Long = 10.0.pow(2.toDouble()).toLong()
        tempValue *= factor
        var temp: Long = tempValue.roundToLong()
        temp /= factor
        return temp.toString()
    }

    // Bind a card to the ItemCardRow
    private fun loadCardImage() {
        Picasso.with(activity)
                .load(this.card?.imageUrl)
                .placeholder(R.drawable.no_card)
                .into(cardImage)
    }

    /*
     * Implementation of the onclick listener in the card Fragment.
     */
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.addButton -> {
                addButtonPressed()
                Log.d(cardFragmentTag, " Add Button Pressed... ")
            }
            R.id.favoriteButton -> {
                if(favorited) {
                    favorites?.removeFavorite(card!!)
                }
                else {
                    favorites?.addFavorite(card!!)
                }

                Log.d(cardFragmentTag, " favorite Button Pressed ")

            }
            R.id.cartButton -> {
                cartButtonPressed()
            }
        }
    }

    private fun cartButtonPressed() {
        val user = mAuth?.currentUser
        if (user != null) {
            val buyCardIntent = Intent(Intent.ACTION_VIEW)
            buyCardIntent.data = Uri.parse(tcgPlayer + generateCardUri() + productType)
            startActivity(buyCardIntent)
        }
    }

    private fun addButtonPressed(){
        val builderSingle: AlertDialog.Builder = AlertDialog.Builder(activity)
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
            innerDialog.setPositiveButton("Add") { _, _ ->
                if(amountInput.text.isEmpty()) {
                    return@setPositiveButton
                }
                deck?.addCard(card!!, amountInput.text.toString().toLong())
            }
            innerDialog.setNegativeButton("Cancel") { _, _ ->
                dialog.dismiss()
                Log.d(cardFragmentTag, "Add card cancelled")
            }
            innerDialog.show()
        }

        builderSingle.show()
    }

    private fun generateCardUri(): String {
        //split the string on every space in the anem
        val tokenizedName = card?.name?.split("\\s+")
        var resultString = tokenizedName?.joinToString("+")
        resultString += "&"
        return resultString!!
    }


    companion object {
        fun newInstance(card: Card): CardFragment {
            val fragment = CardFragment()
            fragment.card = card
            return fragment
        }

    }
}
