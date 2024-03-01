package com.dialiax.sweeto.adapter

import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dialiax.sweeto.DetailsActivity
import com.dialiax.sweeto.databinding.PopularItemBinding

class PopularAdapter(private val items:List<String>,
                     private val price:List<String>,
                     private val image:List<Int>,
                     private val requiredContext: Context
) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(PopularItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val item = items[position]
        val images= image[position]
        val price = price[position]
        holder.bind(item,price,images)

        holder.itemView.setOnClickListener {
            val intent = Intent(requiredContext, DetailsActivity::class.java)
            intent.putExtra("MenuItemName", item)
            intent.putExtra("MenuItemImage", images)
            requiredContext.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class PopularViewHolder(private val binding: PopularItemBinding): RecyclerView.ViewHolder(binding.root) {
        private val imageView = binding.imageView5
        fun bind(item: String,price: String, images: Int) {
            binding.foodNamePopular.text = item
            binding.pricePopular.text = price
            imageView.setImageResource(images)
        }

    }
}