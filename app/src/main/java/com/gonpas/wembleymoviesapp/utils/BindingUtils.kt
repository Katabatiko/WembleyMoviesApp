package com.gonpas.wembleymoviesapp.utils

//import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gonpas.wembleymoviesapp.R
import com.gonpas.wembleymoviesapp.tabs.ApiStatus

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

@BindingAdapter("dateToLocal")
fun TextView.localDate(date: String){
    text = if(date != "") {
        val partes = date.split("-")
        val template = resources.getString(R.string.fecha_estreno)
        template.format(partes[2], partes[1], partes[0])
    } else {
        resources.getString(R.string.por_estrenar)
    }
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