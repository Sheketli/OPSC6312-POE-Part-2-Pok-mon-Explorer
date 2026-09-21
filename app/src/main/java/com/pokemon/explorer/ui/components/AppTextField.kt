package com.pokemon.explorer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokemon.explorer.ui.theme.Border12
import com.pokemon.explorer.ui.theme.BodyFont
import com.pokemon.explorer.ui.theme.PokeRed
import com.pokemon.explorer.ui.theme.Radii
import com.pokemon.explorer.ui.theme.Surface08
import com.pokemon.explorer.ui.theme.TextFaint
import com.pokemon.explorer.ui.theme.TextHigh

/**
 * The app's text field.
 *
 * Built on [BasicTextField] rather than a Material component so the rounded,
 * dark-translucent styling from the design carries through without fighting
 * Material's own decoration.
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    focusRequester: FocusRequester? = null,
    autoFocus: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
) {
    if (autoFocus && focusRequester != null) {
        LaunchedEffect(Unit) {
            // A FocusRequester throws if it is used before its modifier node has been
            // attached, which happens during the first layout pass. Wait for one frame
            // so the field exists, and ignore the request if the screen is already gone.
            withFrameNanos { }
            runCatching { focusRequester.requestFocus() }
        }
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(color = TextHigh, fontSize = 14.sp, fontFamily = BodyFont),
        cursorBrush = SolidColor(PokeRed),
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onImeAction() }),
        modifier = modifier
            .fillMaxWidth()
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(Radii.input)
                    .background(Surface08)
                    .border(1.dp, Border12, Radii.input)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                leadingIcon?.invoke()
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = TextFaint,
                            fontSize = 14.sp,
                            fontFamily = BodyFont,
                        )
                    }
                    innerTextField()
                }
                trailingIcon?.invoke()
            }
        },
    )
}

/** Transparent variant used for the inline pickers on the Compare screen. */
@Composable
fun SmallTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onImeAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(color = TextHigh, fontSize = 14.sp),
        cursorBrush = SolidColor(PokeRed),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = { onImeAction() }),
        modifier = modifier
            .fillMaxWidth()
            .clip(Radii.input)
            .background(Surface08)
            .border(1.dp, Border12, Radii.input)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(text = placeholder, color = TextFaint, fontSize = 14.sp)
                }
                innerTextField()
            }
        },
    )
}
