package com.gonpas.wembleymoviesapp.utils

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gonpas.wembleymoviesapp.R
import com.gonpas.wembleymoviesapp.ui.tabs.ApiStatus

private const val TAG = "xxBu"

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String){
    imgUrl.let {
        // para convertir la URL en URI
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        // invocar glide para cargar la imagen
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image))
            .into(imgView)
    }
}

@BindingAdapter("calificacion")
fun TextView.setNota(nota: Float){
    val template = resources.getString(R.string.calificacion)
    text = template.format(nota.toString().replace('.',','))
}

@BindingAdapter("releaseDateToLocal")
fun TextView.localReleaseDate(date: String){
    text = if(date != "") {
        val partes = date.split("-")
        val template =  resources.getString(R.string.fecha_estreno)
        template.format(partes[2], partes[1], partes[0])
    } else {
         resources.getString(R.string.por_estrenar)
        }
}
@BindingAdapter("birthdayDateToLocal")
fun TextView.localBirthdayDate(date: String?){
    val template =  resources.getString(R.string.birthday)
    text = if(!date.isNullOrBlank()) {
        val partes = date.split("-")
        template.format(partes[2], partes[1], partes[0])
    } else {
        template.substring(0,template.length -6).format("?")
    }
}
@BindingAdapter("deathdayDateToLocal")
fun TextView.localDeathdayDate(date: String?){
	if(date.isNullOrBlank()){
		visibility = View.GONE
	} else {
        val partes = date.split("-")
        val template =  resources.getString(R.string.deathday)
        text = template.format(partes[2], partes[1], partes[0])
    } 
}

@BindingAdapter("template", "inserto")
fun TextView.formatText(template: String, string: String){
    text = template.format(string)
}


@BindingAdapter("apiStatus")
fun bindStatusImage(statusImgView: ImageView, status: ApiStatus){

    when(status){
        ApiStatus.LOADING -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.loading_animation)
        }
        ApiStatus.ERROR -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.ic_broken_image)
        }
        ApiStatus.DONE -> {
            statusImgView.visibility = View.GONE
        }
    }
}