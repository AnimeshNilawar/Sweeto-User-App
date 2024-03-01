package com.dialiax.sweeto.model

data class CartItems(
    var foodName: String? = null,
    var foodPrice: String? = null,
    var foodDescription: String? = null,
    var foodImage: String? = null,
    var foodQuantity: Int = 0,
    var foodIngredients: String? = null
) {
    constructor() : this(null, null, null, null, 0, null)
}
