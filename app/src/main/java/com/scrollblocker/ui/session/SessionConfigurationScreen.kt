package com.scrollblocker.ui.session

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

// IMPORTANT: Make sure these match your actual package name!
import com.scrollblocker.ui.theme.BackgroundDark
import com.scrollblocker.ui.theme.PrimaryCyan
import com.scrollblocker.ui.theme.SecondaryPink
import com.scrollblocker.ui.theme.SurfaceDark
import com.scrollblocker.ui.theme.TextGray
import com.scrollblocker.ui.theme.TextWhite

@Composable
fun SessionConfigurationScreen(
    // We pass the chosen minutes back to the navigation graph when they click continue
    onContinue: (Int) -> Unit
) {
    // This remembers the slider's position. We start it at 30 minutes.
    var sliderValue by remember { mutableFloatStateOf(30f) }

    // Convert the float to a solid integer (e.g., 30.4 becomes 30)
    val selectedMinutes = sliderValue.roundToInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // 1. THE IMAGE (Time / Hourglass concept)
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(SurfaceDark)
                .border(2.dp, SecondaryPink, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // TODO: Replace with an actual image later (like an hourglass or clock)
            // Image(painter = painterResource(id = R.drawable.hourglass), contentDescription = "Time")
            Text("⏳", fontSize = 64.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 2. THE TEXT
        Text(
            text = "Set Daily Limit",
            color = SecondaryPink,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "How much time do you want to spend on these apps before we block them?",
            color = TextGray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 3. THE DYNAMIC TIME DISPLAY
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "$selectedMinutes",
                color = TextWhite,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = " min",
                color = PrimaryCyan,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. THE DRAGGABLE SLIDER
        Slider(
            value = sliderValue,
            onValueChange = { newValue ->
                sliderValue = newValue
            },
            valueRange = 1f..15f, // Minimum 5 minutes, Maximum 120 minutes
            steps = 13, // Breaks the slider into 5-minute chunks for clean snapping
            colors = SliderDefaults.colors(
                thumbColor = PrimaryCyan,
                activeTrackColor = SecondaryPink,
                inactiveTrackColor = SurfaceDark,
                activeTickColor = SecondaryPink,
                inactiveTickColor = SurfaceDark
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1m", color = TextGray, fontSize = 12.sp)
            Text("15m", color = TextGray, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(48.dp))

        // 5. CONTINUE BUTTON
        Button(
            onClick = { onContinue(selectedMinutes) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SecondaryPink,
                contentColor = BackgroundDark // Dark text on bright pink looks incredible
            )
        ) {
            Text(
                text = "Continue",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// I added the preview right away so you can see it in Android Studio!
@Preview(showBackground = true, name = "Session Config Preview")
@Composable
fun SessionConfigurationScreenPreview() {
    SessionConfigurationScreen(
        onContinue = { /* Do nothing in preview */ }
    )
}
