package com.dialiax.sweeto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dialiax.sweeto.databinding.ActivityPayOutBinding
import com.dialiax.sweeto.databinding.FragmentCongratsBottomSheetBinding
import com.dialiax.sweeto.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PayOutActivity : AppCompatActivity() {

     lateinit var binding: ActivityPayOutBinding
     private lateinit var auth: FirebaseAuth
    private lateinit var  name: String
    private lateinit var  address: String
    private lateinit var  phone: String
    private lateinit var  totalAmount: String
    private lateinit var  foodItemsName : ArrayList<String>
    private lateinit var  foodItemsPrice : ArrayList<String>
    private lateinit var  foodItemsIngredients : ArrayList<String>
    private lateinit var  foodItemsDescription : ArrayList<String>
    private lateinit var  foodItemsImage : ArrayList<String>
    private lateinit var  foodItemsQuantity : ArrayList<Int>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase and user details
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference()

        //set user data
        setUserData()

        // get user details from firebase
        val intent = intent
        foodItemsName = intent.getStringArrayListExtra("FoodItemsName") as ArrayList<String>
        foodItemsPrice = intent.getStringArrayListExtra("FoodItemsPrice") as ArrayList<String>
        foodItemsIngredients = intent.getStringArrayListExtra("FoodItemsIngredients") as ArrayList<String>
        foodItemsDescription = intent.getStringArrayListExtra("FoodItemsDescription") as ArrayList<String>
        foodItemsImage = intent.getStringArrayListExtra("FoodItemsImage") as ArrayList<String>
        foodItemsQuantity = intent.getIntegerArrayListExtra("FoodItemsQuantity") as ArrayList<Int>

         totalAmount = '₹' + calculateTotalAmount().toString()
        binding.amount.isEnabled = false
        binding.amount.setText(totalAmount)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.placeMyOrder.setOnClickListener {
            //get data from textview
            name = binding.name.text.toString().trim()
            address = binding.address.text.toString().trim()
            phone = binding.phone.text.toString().trim()

            if (name.isBlank()&&address.isBlank()&&phone.isBlank()){
                Toast.makeText(this, "Please Enter ALl The Details", Toast.LENGTH_SHORT).show()
            }else{
                placeOrder()
            }

        }
    }

    private fun placeOrder() {
        userId = auth.currentUser?.uid?:""
        val time = System.currentTimeMillis()
        val itemPushKey = databaseReference.child("OrderDetails").push().key
        val orderDetails = OrderDetails(userId,name,foodItemsName,foodItemsImage,foodItemsPrice,foodItemsQuantity,address,totalAmount,phone,time,itemPushKey,false,false)
        val orderReference = databaseReference.child("OrderDetails").child(itemPushKey!!)
        orderReference.setValue(orderDetails).addOnSuccessListener {
            val bottomSheetDialog = CongratsBottomSheet()
            bottomSheetDialog.show(supportFragmentManager,"Test")
            removeItemFromCart()
            addOrderToHistory(orderDetails)
        }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to Order", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addOrderToHistory(orderDetails: OrderDetails) {
        databaseReference.child("user").child(userId).child("BuyHistory")
            .child(orderDetails.itemPushKey!!)
            .setValue(orderDetails).addOnSuccessListener {

            }
    }

    private fun removeItemFromCart() {
        val cartItemsReference = databaseReference.child("user").child(userId).child("CartItems")
        cartItemsReference.removeValue()
    }

    private fun calculateTotalAmount(): Int {
        var totalAmount = 0
        for (i in 0 until foodItemsPrice.size){
            var price = foodItemsPrice[i]
            val lastChar = price.last()
            val priceIntValue = if (lastChar == '$'){
                price.dropLast(1).toInt()
            }else{
                price.toInt()
            }
            var quantity = foodItemsQuantity[i]
            totalAmount += priceIntValue * quantity
        }
        return totalAmount
    }

    private fun setUserData() {
        val user = auth.currentUser
        if (user != null){
            val userId = user.uid
            val userReference = databaseReference.child("user").child(userId)

            userReference.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val names = snapshot.child("name").getValue(String::class.java)?:""
                        val addresses = snapshot.child("address").getValue(String::class.java)?:""
                        val phones = snapshot.child("phone").getValue(String::class.java)?:""
                        binding.apply {
                            name.setText(names)
                            address.setText(addresses)
                            phone.setText(phones)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

    }
}