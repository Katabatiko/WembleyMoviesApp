package com.gonpas.wembleymoviesapp.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gonpas.wembleymoviesapp.databinding.ActorItemBinding
import com.gonpas.wembleymoviesapp.domain.DomainActor

class CastAdapter(
    private val personListener: PersonListener
): RecyclerView.Adapter<CastAdapter.CastViewHolder>() {
    var data = listOf<DomainActor>()
        set(value) { field = value }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        return CastViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        val item = data[position]
        holder.binding.actor = item
        holder.binding.profileView.setOnClickListener{
            personListener.onClick(item.personId)
        }
        holder.binding.biografia.setOnClickListener{
            personListener.onClick(item.personId)
        }
        holder.binding.interprete.setOnClickListener{
            personListener.onClick(item.personId)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class CastViewHolder(val binding: ActorItemBinding): RecyclerView.ViewHolder(binding.root){
        companion object{
            fun from(parent: ViewGroup): CastViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding: ActorItemBinding = ActorItemBinding.inflate(layoutInflater, parent,false)
                return CastViewHolder(binding)
            }
        }
    }
}