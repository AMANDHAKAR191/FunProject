package com.example.funproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.funproject.ui.theme.FunProjectTheme
import com.example.funproject.ui.theme.darkGray
import com.example.funproject.ui.theme.gray
import com.example.funproject.ui.theme.redOrange
import com.example.funproject.ui.theme.white
import kotlinx.coroutines.delay
import java.lang.Math.PI
import java.util.Calendar
import java.util.Date
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FunProjectTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(
                    topBar = { LargeTopAppBar(title = { Text(text = "Dynamic Clock") })}
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        var currentTimeInMs by remember {
                            mutableStateOf(System.currentTimeMillis())
                        }

                        LaunchedEffect(key1 = true) {
                            while (true) {
                                delay(200)
                                currentTimeInMs = System.currentTimeMillis()
                            }
                        }
                        Box(
                            modifier = Modifier
                                .background(white)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Clock(
                                modifier = Modifier
                                    .size(500.dp),
                                time = {
                                    currentTimeInMs
                                },
                                circleRadius = 250f,
                                outerCircleThickness = 10f
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun Clock(
    modifier: Modifier = Modifier,
    time: () -> Long,
    circleRadius: Float,
    outerCircleThickness: Float,
) {
    var hoursCircleCenter by remember {
        mutableStateOf(Offset.Zero)
    }
    var minutesCircleCenter by remember {
        mutableStateOf(Offset.Zero)
    }
    var secondsCircleCenter by remember {
        mutableStateOf(Offset.Zero)
    }
    var hoursCircleRadius by remember {
        mutableStateOf(0f)
    }
    var minutesCircleRadius by remember {
        mutableStateOf(0f)
    }
    var secondsCircleRadius by remember {
        mutableStateOf(0f)
    }
    val textMeasurer = rememberTextMeasurer()
    Box(
        modifier = modifier
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val width = size.width
            val height = size.height

            hoursCircleRadius = circleRadius
            minutesCircleRadius = hoursCircleRadius / 2
            secondsCircleRadius = minutesCircleRadius/1.5f

            hoursCircleCenter = Offset(x = width / 2f, y = height / 2f)
            minutesCircleCenter =
                Offset(x = hoursCircleCenter.x + hoursCircleRadius, y = hoursCircleCenter.y)
            secondsCircleCenter =
                Offset(x = minutesCircleCenter.x + minutesCircleRadius, y = minutesCircleCenter.y)


            val date = Date(time())
            val cal = Calendar.getInstance()
            cal.time = date
            val hours = cal.get(Calendar.HOUR_OF_DAY)
            val minutes = cal.get(Calendar.MINUTE)
            val seconds = cal.get(Calendar.SECOND)

            println("hours: $hours minutes: $minutes seconds: $seconds")

            //watch dial circle
            drawCircle(
                style = Stroke(
                    width = outerCircleThickness
                ),
                brush = Brush.linearGradient(
                    listOf(
                        white.copy(0.35f),
                        darkGray.copy(0.55f)
                    )
                ),
                radius = circleRadius + outerCircleThickness / 2f,
                center = hoursCircleCenter
            )
            // outer outline
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        white.copy(0.45f),
                        darkGray.copy(0.35f)
                    )
                ),
                radius = hoursCircleRadius,
                center = hoursCircleCenter
            )
            //little small circle at center of watch circle
            drawCircle(
                color = gray,
                radius = 15f,
                center = hoursCircleCenter
            )
            circleComponent(
                longTextSample = (hours % 12).toString(),
                center = Offset(
                    x = hoursCircleCenter.x + hoursCircleRadius / 2,
                    y = hoursCircleCenter.y
                ),
                textMeasurer = textMeasurer,
                circleRadius = 40f
            )

            //for hours ticks(1..12 counts)
            val littleLineLength = circleRadius * 0.1f
            val largeLineLength = circleRadius * 0.2f
            for (i in 0 until 12) {
                val angleInDegrees = i * 360f / 12
                val angleInRad = angleInDegrees * PI / 180f + PI / 2f
                val lineLength = largeLineLength
                val lineThickness = if (i % 3 == 0) 5f else 2f

                val start = Offset(
                    x = (hoursCircleRadius * cos(angleInRad) + hoursCircleCenter.x).toFloat(),
                    y = (hoursCircleRadius * sin(angleInRad) + hoursCircleCenter.y).toFloat()
                )

                val end = Offset(
                    x = (hoursCircleRadius * cos(angleInRad) + hoursCircleCenter.x).toFloat(),
                    y = (hoursCircleRadius * sin(angleInRad) + lineLength + hoursCircleCenter.y).toFloat()
                )
                rotate(
                    angleInDegrees + 180,
                    pivot = start
                ) {
                    drawLine(
                        color = gray,
                        start = start,
                        end = end,
                        strokeWidth = lineThickness.dp.toPx()
                    )
                }
            }


            // for hour hand
            val hoursAngleInDegree = (((hours % 12) / 12f * 60f) + minutes / 12f) * 360f / 60f
            val hoursLineLength = hoursCircleRadius / 2
            val hourLineThickness = 9f

            val hoursStart = Offset(x = hoursCircleCenter.x, y = hoursCircleCenter.y)
            val hoursEnd =
                Offset(x = hoursCircleCenter.x, y = hoursLineLength + hoursCircleCenter.y)

            rotate(
                hoursAngleInDegree + 180,
                pivot = hoursStart
            ) {
                drawLine(
                    color = gray,
                    start = hoursStart,
                    end = hoursEnd,
                    strokeWidth = hourLineThickness.dp.toPx()
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(
                            white.copy(0.45f),
                            darkGray.copy(0.25f)
                        )
                    ),
                    radius = minutesCircleRadius,
                    center = hoursEnd
                )
                drawCircle(
                    color = gray,
                    radius = 15f,
                    center = hoursEnd
                )

                // for minute hand end point
                val minuteAngleInDegree = (minutes + seconds / 60f) * 360f / 60f

                val minuteLineLength = minutesCircleRadius / 2

                val minuteLineThickness = 7f

                val minuteStart = Offset(x = hoursEnd.x, y = hoursEnd.y)

                val minuteEnd =
                    Offset(x = minutesCircleCenter.x, y = minuteLineLength + minutesCircleCenter.y)

                rotate(
                    minuteAngleInDegree + 104,
                    pivot = minuteStart
                ) {
                    drawLine(
                        color = gray,
                        start = minuteStart,
                        end = minuteEnd,
                        strokeWidth = minuteLineThickness.dp.toPx()
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                white.copy(0.45f),
                                darkGray.copy(0.25f)
                            )
                        ),
                        radius = secondsCircleRadius,
                        center = minuteEnd
                    )
                    drawCircle(
                        color = gray,
                        radius = 15f,
                        center = minuteEnd
                    )

                    // for second hand end point
                    val secondAngleInDegree = seconds * 360f / 60f
                    println("minuteAngleInDegree: $secondAngleInDegree")

                    val secondLineLength = minutesCircleRadius * 3

                    val secondLineThickness = 5f

                    val secondStart = Offset(x = minuteEnd.x, y = minuteEnd.y)
                    val secondEnd = Offset(
                        x = secondsCircleCenter.x,
                        y = secondLineLength + secondsCircleCenter.y
                    )

                    rotate(
                        secondAngleInDegree - 82,
                        pivot = secondStart
                    ) {
                        drawLine(
                            color = redOrange,
                            start = secondStart,
                            end = secondEnd,
                            strokeWidth = secondLineThickness.dp.toPx()
                        )
                        drawCircle(
                            color = gray,
                            radius = 15f,
                            center = secondEnd
                        )
                    }
                    circleComponent(
                        longTextSample = seconds.toString(),
                        center = Offset(
                            x = minuteEnd.x + secondsCircleRadius/2,
                            y = minuteEnd.y
                        ),
                        textMeasurer = textMeasurer,
                        circleRadius = 40f
                    )
                }
                circleComponent(
                    longTextSample = minutes.toString(),
                    center = Offset(
                        x = hoursEnd.x + minutesCircleRadius/2,
                        y = hoursEnd.y
                    ),
                    textMeasurer = textMeasurer,
                    circleRadius = 40f
                )
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
fun DrawScope.circleComponent(longTextSample: String, center: Offset, textMeasurer: TextMeasurer, circleRadius: Float) {

    val measuredText = textMeasurer.measure(
        AnnotatedString(longTextSample),
        constraints = Constraints.fixed(
            width = (size.width).toInt(),
            height = (size.height).toInt()
        ),
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(fontSize = 20.sp)
    )

    drawCircle(
        color = gray,
        radius = circleRadius,
        center = center
    )
    // Draw text
    drawText(
        textLayoutResult = measuredText,
        topLeft = Offset(
            x = center.x - size.width*0.035f,
            y = center.y - size.height*0.035f
        ), // position where you want to start drawing the text
        brush = SolidColor(Color.White)
    )
}

enum class ClockHands {
    Seconds,
    Minutes,
    Hours
}