package vanderclay.comet.benson.franticsearch.data.domain.datasource

import android.content.Context
import vanderclay.comet.benson.franticsearch.data.API.CardAPI
import vanderclay.comet.benson.franticsearch.data.db.CardDB
import vanderclay.comet.benson.franticsearch.data.domain.model.Card

/**
 * Created by gclay on 4/7/17.
 */

class CardProvider(val ctx: Context?) {
    val db = CardDB(ctx)
    val api = CardAPI(db)

    fun requestByName(cardName: String): Card? {
        val res = db.requestCardByName(cardName)
        return if(res != null) res else null
    }

    fun requestAllCardsFromAPI(): List<Card> {
        return api.requestAllCards()
    }

    fun requestAllCardsFromDB(): List<Card> {
        return db.requestAllCards()
    }

    fun requestCardByName(name: String?): Card {
        return db.requestCardByName(name)
    }

}