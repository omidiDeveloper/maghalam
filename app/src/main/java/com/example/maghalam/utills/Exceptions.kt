package com.example.maghalam.utills

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.maghalam.ui.features.register.RegisterScreenView
import kotlinx.coroutines.launch

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.math.atan2

fun Modifier.slideUpAnimation(
    durationMillis: Int = 800,
    delayMillis: Int = 0,
    initialOffsetY: Float = 100f
): Modifier = composed {
    val offsetY = remember { Animatable(initialOffsetY) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delayMillis.toLong())
        launch {
            offsetY.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = durationMillis,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    this
        .offset(y = offsetY.value.dp)
        .alpha(alpha.value)
}


@Composable
fun RtlLayout(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        content()
    }
}

val coroutinesExceptionHandler = CoroutineExceptionHandler { _ , throwable ->
    Log.v("exceptions" , "Error -> " + throwable.message!!)
}




@Composable
fun HandwriteWord(
    pathData: Int,
    modifier: Modifier = Modifier,
    duration: Int = 3000,
    strokeWidth: Dp = 6.dp,
    color: Color = Color(0xFF2C3E73)
) {
    val path = remember(pathData) {
        PathParser().parsePathString(pathData.toString()).toPath()
    }

    val anim = remember { Animatable(0f) }

    LaunchedEffect(pathData) {
        anim.snapTo(0f)
        anim.animateTo(1f, animationSpec = tween(duration))
    }

    Canvas(modifier) {
        val measure = PathMeasure()
        measure.setPath(path, false)

        val length = measure.length
        val drawLength = length * anim.value
        val drawn = Path()
        measure.getSegment(0f, drawLength, drawn, startWithMoveTo = true)

        drawPath(
            path = drawn,
            color = color,
            style = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        if (drawLength > 0f) {
            val point = measure.getPosition(drawLength)
            val tangent = measure.getTangent(drawLength)
            val angle = Math.toDegrees(atan2(tangent.y, tangent.x).toDouble()).toFloat()

            drawCircle(
                color = Color.Black,
                radius = 5.dp.toPx(),
                center = point
            )

        }
    }

}
