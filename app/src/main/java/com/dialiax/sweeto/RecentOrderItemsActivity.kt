package com.dialiax.sweeto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dialiax.sweeto.adapter.RecentBuyAdapter
import com.dialiax.sweeto.databinding.ActivityRecentOrderItemsBinding
import com.dialiax.sweeto.model.OrderDetails

class RecentOrderItemsActivity : AppCompatActivity() {

    private val binding: ActivityRecentOrderItemsBinding by lazy {
        ActivityRecentOrderItemsBinding.inflate(layoutInflater)
    }

    private lateinit var allFoodNames: ArrayList<String>
    private lateinit var allFoodImages: ArrayList<String>
    private lateinit var allFoodPrices: ArrayList<String>
    private lateinit var allFoodQuantities: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        val recentOrderItems = intent.getSerializableExtra("RecentBuyOrderItem") as ArrayList<OrderDetails>

        // Handle potential empty list
        recentOrderItems?.let { orderDetails ->
            if (orderDetails.isNotEmpty()) {
                // Process the list (choose one of the options below):

                // Option 1: Iterate through all items
                for (orderItem in orderDetails) {
                    allFoodNames = orderItem.foodNames as ArrayList<String>
                    allFoodImages = orderItem.foodImages as ArrayList<String>
                    allFoodPrices = orderItem.foodPrices as ArrayList<String>
                    allFoodQuantities = orderItem.foodQuantities as ArrayList<Int>
                    setAdapter() // Call setAdapter after processing each item (if applicable)
                }
                
            } else {
                Toast.makeText(this, "List is Empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setAdapter() {
        val rv = binding.recentBuyRecyclerView
        rv.layoutManager = LinearLayoutManager(this)
        val adapter = RecentBuyAdapter(this, allFoodNames, allFoodImages, allFoodPrices, allFoodQuantities)
        rv.adapter = adapter
    }
}
