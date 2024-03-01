package com.dialiax.sweeto.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dialiax.sweeto.databinding.CartItemBinding
import com.dialiax.sweeto.model.CartItems
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CartAdapter(
    private val context: Context,
    private val CartItems: MutableList<String>,
    private val CartItemPrice: MutableList<String>,
    private var CartImage: MutableList<String>,
    private var CartDescription: MutableList<String>,
    private val CartQuantity: MutableList<Int>,
    private var CartIngredient: MutableList<String>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val auth = FirebaseAuth.getInstance()

    init {
        val database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid ?: ""
        val cartItemNumber = CartItems.size

        itemQuantities = IntArray(cartItemNumber) { 1 }
        cartItemsReference = database.reference.child("user").child(userId).child("CartItems")
    }

    companion object {
        private var itemQuantities: IntArray = intArrayOf()
        private lateinit var cartItemsReference: DatabaseReference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = CartItems.size

    //get updated quantity
    fun getUpdatedItemsQuantities(): MutableList<Int> {
        val itemQuantity = mutableListOf<Int>()
        itemQuantity.addAll(CartQuantity)
        return itemQuantity
    }

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val quantity = itemQuantities[position]
                cartFoodName.text = CartItems[position]
                cartitemPrice.text = CartItemPrice[position]
                cartItemQuantity.text = quantity.toString()
                //load image using glide
                val uriString = CartImage[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(cartImage)

                minusButton.setOnClickListener {
                    decreaseQuantity(position)
                }
                plusbutton.setOnClickListener {
                    increaseQuantity(position)
                }
                deleteButton.setOnClickListener {
                    val itemPosition = adapterPosition
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        deleteItem(itemPosition)
                    }
                }

            }

        }

        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                CartQuantity[position] = itemQuantities[position]
                binding.cartItemQuantity.text = itemQuantities[position].toString()
            }
        }

        private fun deleteItem(position: Int) {
            if (position < 0 || position >= CartItems.size) {
                Toast.makeText(context, "Invalid position", Toast.LENGTH_SHORT).show()
                return
            }

            getUniqueKeyAtPosition(position) { uniqueKey ->
                if (uniqueKey != null) {
                    cartItemsReference.child(uniqueKey).removeValue()
                        .addOnSuccessListener {
                            if (CartItems.isNotEmpty()) CartItems.removeAt(position)
                            if (CartImage.isNotEmpty()) CartImage.removeAt(position)
                            if (CartDescription.isNotEmpty()) CartDescription.removeAt(position)
                            if (CartQuantity.isNotEmpty()) CartQuantity.removeAt(position)
                            if (CartItemPrice.isNotEmpty()) CartItemPrice.removeAt(position)
                            if (CartIngredient.isNotEmpty()) CartIngredient.removeAt(position)
                            if (itemQuantities.isNotEmpty()) {
                                itemQuantities = itemQuantities.filterIndexed { index, _ -> index != position }.toIntArray()
                            }
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, CartItems.size)
                            Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }


        private fun decreaseQuantity(position: Int) {
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--
                CartQuantity[position] = itemQuantities[position]
                binding.cartItemQuantity.text = itemQuantities[position].toString()
            }
        }

    }


    private fun getUniqueKeyAtPosition(positionRetrieve: Int, onComplete: (String?) -> Unit) {
        cartItemsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var uniqueKey: String? = null
                //loop for snapshot children
                snapshot.children.forEachIndexed { index, dataSnapshot ->
                    if (index == positionRetrieve) {
                        uniqueKey = dataSnapshot.key
                        return@forEachIndexed
                    }
                }
                onComplete(uniqueKey)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Data not Fetch", Toast.LENGTH_SHORT).show()
            }

        })
    }


}