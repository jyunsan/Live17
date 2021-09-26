package com.example.live17.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.live17.databinding.ItemLayoutBinding
import com.example.live17.ui.UserListViewModel

class UsersAdapter(
    private val viewModel: UserListViewModel
    ) : RecyclerView.Adapter<ViewHolder>() {

    inner class ItemViewHolder(val binding: ItemLayoutBinding): ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemLayoutBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                )
            )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val item = viewModel.getUserList().get(position)

        val vh = viewHolder as ItemViewHolder
        val binding = vh.binding

        item.avatarUrl.let{
            Glide.with(binding.imageView.context)
                .load(it)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(5)))
                .into(binding.imageView)
        }
        binding.title.text = item.login
    }

    override fun getItemCount() = viewModel.getUserList().size

}


