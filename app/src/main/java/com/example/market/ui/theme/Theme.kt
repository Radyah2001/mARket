package com.example.market.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.market.R

private val DarkColorScheme = darkColorScheme(
    primary = Beige,
    secondary = Orange,
    tertiary = DarkOrange,
    background = Teal
)

private val LightColorScheme = lightColorScheme(
    primary = Beige,
    secondary = Orange,
    tertiary = DarkOrange,
    background = Teal

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun MARketTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun Logo() {
    Image(
        modifier = Modifier
            .wrapContentWidth(align = Alignment.CenterHorizontally),
        painter = painterResource(id = R.drawable.group_1),
        contentDescription = "My Image Description"
    )
}

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    contentDescription: String,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp, 4.dp)
            .border(
                BorderStroke(width = 2.dp, color = Teal),
                shape = RoundedCornerShape(2)
            ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Beige,
            unfocusedContainerColor = Beige,
            focusedTextColor = Color.Black,

            ),
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder) },
        leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = contentDescription) },
        visualTransformation = visualTransformation
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DarkOrange,
            contentColor = Color.White
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            modifier = Modifier.padding(0.dp, 6.dp)
        )
    }
}

@Composable
fun PriceTextField(
    value: String,
    onValueChange: (String) -> Unit,
    currency: String = "kr.", // Default currency
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: ImageVector,
    placeholder: String = "Price"
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            // Filter out non-numeric characters
            val filteredValue = newValue.filter { it.isDigit() }
            onValueChange(filteredValue)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        trailingIcon = { Text(currency) }, // Trailing currency text
        leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = "price")},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 4.dp)
            .border(
                BorderStroke(width = 2.dp, color = Teal),
                shape = RoundedCornerShape(2)
            ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Beige,
            unfocusedContainerColor = Beige,
            focusedTextColor = Color.Black,
            cursorColor = Color.Black,

        ),
        singleLine = true,
        placeholder = { Text(text = placeholder) },
    )
}