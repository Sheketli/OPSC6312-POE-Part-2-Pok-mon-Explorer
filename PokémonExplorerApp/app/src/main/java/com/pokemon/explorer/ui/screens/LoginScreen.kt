package com.pokemon.explorer.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokemon.explorer.state.AppState
import com.pokemon.explorer.state.Screen
import com.pokemon.explorer.ui.components.AppLogo
import com.pokemon.explorer.ui.components.AppTextField
import com.pokemon.explorer.ui.components.GameButton
import com.pokemon.explorer.ui.components.GameButtonStyle
import com.pokemon.explorer.ui.components.LockIcon
import com.pokemon.explorer.ui.components.UserIcon
import com.pokemon.explorer.ui.components.EyeIcon
import com.pokemon.explorer.ui.theme.DisplayFont
import com.pokemon.explorer.ui.theme.PokeRed
import com.pokemon.explorer.ui.theme.TextHigh
import com.pokemon.explorer.ui.theme.TextLow

@Composable
fun LoginScreen(
    state: AppState,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppLogo(
            size = 0.8f,
            showIcon = true,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "Welcome Back",
            color = TextHigh,
            fontSize = 32.sp,
            fontFamily = DisplayFont,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Login to continue your journey",
            color = TextLow,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        AppTextField(
            value = username,
            onValueChange = { username = it },
            placeholder = "Username",
            leadingIcon = { UserIcon(color = Color.White.copy(alpha = 0.4f)) },
            imeAction = ImeAction.Next
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Password",
            leadingIcon = { LockIcon(color = Color.White.copy(alpha = 0.4f)) },
            trailingIcon = {
                Box(
                    modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                ) {
                    EyeIcon(
                        visible = passwordVisible,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            },
            keyboardType = KeyboardType.Password,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            imeAction = ImeAction.Done,
            onImeAction = { state.login(username, password) }
        )

        state.authError?.let { error ->
            Text(
                text = error,
                color = PokeRed,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        GameButton(
            style = GameButtonStyle.Red,
            onClick = { state.login(username, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "LOGIN",
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = DisplayFont,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Don't have an account? ",
                color = TextLow,
                fontSize = 14.sp
            )
            Text(
                text = "Register",
                color = PokeRed,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { state.navigate(Screen.REGISTER) }
            )
        }
    }
}
