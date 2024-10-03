package com.kimnlee.memberinvitation.presentation.components

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.kimnlee.common.ui.theme.MobiBgGray
import com.kimnlee.common.ui.theme.MobiBlue
import com.kimnlee.common.ui.theme.MobiPayTheme
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
import com.kimnlee.memberinvitation.R
import com.kimnlee.memberinvitation.data.repository.MemberInvitationRepository
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel
import kotlinx.coroutines.delay

private const val TAG = "MemberInvitationViaBLE"
@Composable
fun MemberInvitationViaBLE(
    memberInvitationRepository: MemberInvitationRepository?,
    vehicleId: Int,
    viewModel: MemberInvitationViewModel,
    onNavigateToConfirmation: () -> Unit
) {
    var showCheckGif by remember { mutableStateOf(false) }
    var showFailGif by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(true) }
    var shouldCloseBottomSheet by remember { mutableStateOf(false) }

    val discoveredPhoneNumbers = viewModel.discoveredPhoneNumbers

    LaunchedEffect(shouldCloseBottomSheet) {
        if (shouldCloseBottomSheet) {
            delay(2000)
            Log.d(TAG, "MemberInvitationViaBLE: 끄자 2초 지났다")
            viewModel.closeInvitationBLE()
            shouldCloseBottomSheet = false
        }
    }

    MobiPayTheme {
        Column(
            modifier = Modifier
                .background(MobiBgGray)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "근처 멤버 초대",
                fontFamily = FontFamily(Font(com.kimnlee.common.R.font.pmedium)),
                style = MaterialTheme.typography.headlineMedium,
                color = MobiTextAlmostBlack
            )

            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    showCheckGif -> {
                        CheckGifAnimation(Modifier.size(300.dp))
                    }
                    showFailGif -> {
                        FailGifAnimation(Modifier.size(300.dp))
                    }
                    else -> {
                        RadarGifAnimation(Modifier.size(300.dp))
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
            ) {
                if (discoveredPhoneNumbers.isNotEmpty()) {
                    discoveredPhoneNumbers.forEach { phoneNumber ->
                        Card(
//                            shape = RoundedCornerShape(15.dp),
                            shape = CircleShape,
                            modifier = Modifier
//                                .fillMaxWidth()
                                .width(210.dp)
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors( // Set the background color of the Card
                                containerColor = Color.White
                            )
                        ) {
                            Text(
                                textAlign = TextAlign.Center,
                                text = phoneNumberFormat(phoneNumber),
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally),
                                fontSize = 18.sp,
                                fontFamily = FontFamily(Font(com.kimnlee.common.R.font.pmedium)),
                            )
                        }
                    }
                }
                else {
                    Text(
                        text = "주변에 초대받을 회원이 없어요.\n초대받을 회원은 초대 대기화면을 켜야해요.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            Button(
                onClick = {
                    isScanning = false
                    if (discoveredPhoneNumbers.isNotEmpty()) {
                        discoveredPhoneNumbers.forEach { phoneNumber ->
                            if (memberInvitationRepository != null) {
                                memberInvitationRepository.sendInvitation(phoneNumber, vehicleId)
                            }
                            showCheckGif = true
                        }
                    } else {
                        showFailGif = true
                    }
                    shouldCloseBottomSheet = true
//                    viewModel.closeInvitationBLE()
                },
                shape = RoundedCornerShape(9.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp)
                    .height(44.dp)
                    .width(270.dp)
                    .alpha(if (shouldCloseBottomSheet) 0f else 1f),
                enabled = !shouldCloseBottomSheet,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MobiBlue
                )
            ) {
                Text(
                    text = if (discoveredPhoneNumbers.isNotEmpty()) "초대하기" else "취소",
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(com.kimnlee.common.R.font.psemibold)),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun RadarGifAnimation(modifier: Modifier) {
    val animatedScale by animateFloatAsState(
        targetValue = 0.9f,
        animationSpec = tween(durationMillis = 200, easing = LinearEasing)
    )

    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context)
                    .data(R.raw.radar) // Use radar.gif
                    .apply {
                        size(Size.ORIGINAL)
                    }
                    .build(),
                imageLoader = imageLoader
            ),
            contentDescription = null,
            modifier = Modifier
                .size(300.dp)
                .scale(animatedScale)
        )
    }
}

private fun phoneNumberFormat(phoneNumber: String): String {
    return phoneNumber.replace(Regex("(\\d{3})(\\d{4})(\\d{4})"), "$1-$2-$3")
}


@Composable
fun CheckGifAnimation(modifier: Modifier) {
    val animatedScale by animateFloatAsState(
        targetValue = 0.9f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
    )

    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context)
                    .data(R.raw.check) // Use check.gif
                    .apply {
                        size(Size.ORIGINAL)
                    }
                    .build(),
                imageLoader = imageLoader
            ),
            contentDescription = null,
            modifier = Modifier
                .size(300.dp)
                .scale(animatedScale)
        )
    }
}

@Composable
fun FailGifAnimation(modifier: Modifier) {
    val animatedScale by animateFloatAsState(
        targetValue = 0.9f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
    )

    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context)
                    .data(R.raw.fail)
                    .apply {
                        size(Size.ORIGINAL)
                    }
                    .build(),
                imageLoader = imageLoader
            ),
            contentDescription = null,
            modifier = Modifier
                .size(300.dp)
                .scale(animatedScale)
        )
    }
}
