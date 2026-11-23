package com.devforgely.beeproductive.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.devforgely.beeproductive.ui.onboarding.rememberBeeTraitsComposition

private const val CreamWallpaper = "wallpaper/cream.svg"
private const val HoneyFlooring = "flooring/honey.svg"
private const val HiveWindow = "furniture/window/window.svg"
private const val HiveDoor = "furniture/door/door.svg"
private const val FlowerBed = "furniture/bed/flower_bed.svg"
private const val CeilingLight = "furniture/light/ceiling_light.svg"

/**
 * Home room. Asset arguments are paths under assets/.
 * Drop a new file in assets/wallpaper, assets/flooring, or assets/furniture and pass its path to swap the design.
 */
@Composable
fun HiveHomeScene(
    modifier: Modifier = Modifier,
    wallpaperAsset: String = CreamWallpaper,
    flooringAsset: String = HoneyFlooring,
    windowAsset: String = HiveWindow,
    doorAsset: String = HiveDoor,
    bedAsset: String = FlowerBed,
    lightAsset: String = CeilingLight,
) {
    val imageLoader = rememberSvgImageLoader()
    val idle by rememberBeeTraitsComposition("bee_idle")
    val idleProgress by animateLottieCompositionAsState(
        composition = idle,
        iterations = LottieConstants.IterateForever,
        restartOnPlay = true,
    )
    Box(
        modifier
            .fillMaxWidth()
            .height(300.dp),
    ) {
        SceneSvg(
            assetPath = wallpaperAsset,
            imageLoader = imageLoader,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        SceneSvg(
            assetPath = windowAsset,
            imageLoader = imageLoader,
            contentDescription = "Window",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(x = (-52).dp, y = 36.dp)
                .size(width = 90.dp, height = 102.dp),
        )
        SceneSvg(
            assetPath = lightAsset,
            imageLoader = imageLoader,
            contentDescription = "Ceiling light",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = (-2).dp)
                .padding(end = 108.dp)
                .size(width = 76.dp, height = 73.dp),
        )
        SceneSvg(
            assetPath = flooringAsset,
            imageLoader = imageLoader,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.46f),
            contentScale = ContentScale.FillBounds,
        )
        SceneSvg(
            assetPath = doorAsset,
            imageLoader = imageLoader,
            contentDescription = "Door",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 36.dp, bottom = 84.dp)
                .size(width = 88.dp, height = 123.dp),
        )
        SceneSvg(
            assetPath = bedAsset,
            imageLoader = imageLoader,
            contentDescription = "Flower bed",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 8.dp, bottom = 6.dp)
                .size(width = 146.dp, height = 88.dp),
        )
        LottieAnimation(
            composition = idle,
            progress = { idleProgress },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp)
                .size(148.dp),
        )
    }
}

@Composable
private fun SceneSvg(
    assetPath: String,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data("file:///android_asset/$assetPath")
            .build(),
        contentDescription = contentDescription,
        imageLoader = imageLoader,
        modifier = modifier,
        contentScale = contentScale,
    )
}

@Composable
private fun rememberSvgImageLoader(): ImageLoader {
    val context = LocalContext.current
    return remember(context) {
        ImageLoader.Builder(context.applicationContext)
            .components { add(SvgDecoder.Factory()) }
            .build()
    }
}
