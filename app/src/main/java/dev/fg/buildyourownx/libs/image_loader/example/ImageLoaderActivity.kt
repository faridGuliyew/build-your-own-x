package dev.fg.buildyourownx.libs.image_loader.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import dev.fg.buildyourownx.R
import dev.fg.buildyourownx.libs.image_loader.ui.ImageLoaderImage

class ImageLoaderActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val placeholder = ContextCompat.getDrawable(this, R.drawable.loading_placeholder)!!.toBitmap().asImageBitmap()


        setContent {

            LazyVerticalStaggeredGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalItemSpacing = 12.dp,
                horizontalArrangement = Arrangement.spacedBy(
                    12.dp, Alignment.CenterHorizontally
                ),
                columns = StaggeredGridCells.Fixed(2)
            ) {
                items(catImages) {
                    ImageLoaderImage(
                        modifier = Modifier.size(150.dp),
                        url = it,
                        key = it,
                        loadingBitmap = placeholder,
                        errorBitmap = placeholder,
                        transitionSpec = {
                            slideIn { it.center } + fadeIn() togetherWith slideOut { it.center } + fadeOut()
                        }
                    )
                }
            }
        }
    }
}