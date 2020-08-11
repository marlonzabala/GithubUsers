package com.app.githubusers.extensions

import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

fun ImageView.loadNormalColor(imageUrl: String) {
    val requestOption = RequestOptions()
        .centerCrop()
        .dontTransform()
        .dontAnimate()
        .circleCrop()

    Glide.with(this)
        .load(imageUrl)
        .apply(requestOption)
        .into(this)
}

fun ImageView.loadInvertedColor(imageUrl: String) {
    val requestOption = RequestOptions()
        .centerCrop()
        .dontTransform()
        .dontAnimate()
        .circleCrop()

    val negativeColorFilter = floatArrayOf(
        -1.0f, 0f, 0f, 0f, 255f,
        0f, -1.0f, 0f, 0f, 255f,
        0f, 0f, -1.0f, 0f, 255f,
        0f, 0f, 0f, 1.0f, 0f
    )

    Glide.with(context)
        .load(imageUrl)
        .apply(requestOption)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                resource?.colorFilter = ColorMatrixColorFilter(negativeColorFilter)
                return false
            }
        })
        .into(this)
}