// app/src/main/java/com/project/bansigo_rider/ui/components/OtpCodeInput.kt
package com.project.bansigo_rider.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.ui.theme.*

@Composable
fun OtpCodeInput(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    onCompleted: ((String) -> Unit)? = null
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Hidden single basic text field capturing system IME inputs cleanly
        BasicTextField(
            value = code,
            onValueChange = { newText ->
                val digitsOnly = newText.filter { it.isDigit() }.take(length)
                onCodeChange(digitsOnly)
                if (digitsOnly.length == length) {
                    onCompleted?.invoke(digitsOnly)
                }
            },
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (code.length == length) {
                        onCompleted?.invoke(code)
                    }
                }
            ),
            cursorBrush = SolidColor(Color.Transparent),
            decorationBox = {
                // Custom boxes presentation matching inspo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until length) {
                        val char = code.getOrNull(i)?.toString().orEmpty()
                        val isCurrentFocus = i == code.length || (i == length - 1 && code.length == length)
                        val isFilled = char.isNotEmpty()

                        Box(
                            modifier = Modifier
                                .width(46.dp)
                                .height(56.dp)
                                .background(
                                    color = if (isFilled) SurfaceLight else InputBackground,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = if (isCurrentFocus) 2.dp else 1.dp,
                                    color = when {
                                        isCurrentFocus -> BansiGreen
                                        isFilled -> BansiGreen.copy(alpha = 0.5f)
                                        else -> BorderLight
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        )
    }
}
