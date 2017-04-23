package vanderclay.comet.benson.franticsearch.ui.adapters

import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.magicthegathering.javasdk.resource.Card
import vanderclay.comet.benson.franticsearch.databinding.ItemCardRowBinding
import vanderclay.comet.benson.franticsearch.ui.adapters.viewholder.CardViewHolder


/**
 * Created by gclay on 4/5/17.
 */

class FavoriteListAdapter(val cards: MutableList<Card>): RecyclerView.Adapter<CardViewHolder>() {

    private var mCards = cards

    private val TAG = "FavoriteListAdapter"


    override fun getItemCount(): Int {
        return mCards.size
    }

    override fun onBindViewHolder(holder: CardViewHolder?, position: Int) {
        val card = mCards[position]
        holder?.bind(card)

        holder?.itemView?.setOnLongClickListener {
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView?.context!!)
            alertDialogBuilder.setMessage("Delete Favorite? ${card.name}?")
            alertDialogBuilder.setPositiveButton("Yes", { _, _ ->
                val favoriteCardReference = FirebaseDatabase
                        .getInstance()
                        .getReference("Favorites")
                        .child(FirebaseAuth.getInstance().currentUser?.uid)
                        .orderByChild("name")
                        .equalTo(card.name)

                val valueEventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (postSnapshot in dataSnapshot.children) {
                            FirebaseDatabase
                                    .getInstance()
                                    .getReference("Favorites")
                                    .child(FirebaseAuth.getInstance().currentUser?.uid)
                                    .child(postSnapshot.key)
                                    .removeValue()
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, cards.size)
                            notifyDataSetChanged()
                        }

                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d(TAG, databaseError.toString())
                    }
                }

                favoriteCardReference.addValueEventListener(valueEventListener)
            })
            alertDialogBuilder.setNegativeButton("No", { _, _ ->
                Log.d(TAG, "Remove deck cancelled")
            })
            alertDialogBuilder.create().show()
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CardViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val itemBinding = ItemCardRowBinding.inflate(layoutInflater, parent, false)
        return CardViewHolder(itemBinding)
    }
}

