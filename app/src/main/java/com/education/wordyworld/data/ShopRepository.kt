package com.education.wordyworld.data

import com.education.wordyworld.model.ShopItem
import com.education.wordyworld.model.ShopItemType

class ShopRepository {
    private val items = listOf(
        ShopItem(
            id = 1,
            title = "Hint Helper Pack",
            description = "Get 3 extra hints to solve tricky words.",
            price = "\$1.99",
            type = ShopItemType.HINT_PACK
        ),
        ShopItem(
            id = 2,
            title = "Sticker Party Bundle",
            description = "Unlock a surprise set of mascot stickers.",
            price = "\$3.49",
            type = ShopItemType.STICKER_PACK
        ),
        ShopItem(
            id = 3,
            title = "Ad-Free Explorer",
            description = "Play without ads for a distraction-free adventure.",
            price = "\$4.99",
            type = ShopItemType.AD_FREE
        )
    )

    fun getShopItems(): List<ShopItem> = items
}
