package com.scrollblocker.ui.session


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

import com.scrollblocker.ui.theme.BackgroundDark
import com.scrollblocker.ui.theme.CardDark
import com.scrollblocker.ui.theme.PrimaryCyan
import com.scrollblocker.ui.theme.SecondaryPink
import com.scrollblocker.ui.theme.SurfaceDark
import com.scrollblocker.ui.theme.TextGray
import com.scrollblocker.ui.theme.TextWhite
import com.scrollblocker.ui.theme.WarningOrange

@Composable
fun CooldownPeriodScreen(
    // Pass the chosen minutes and the chosen mode back to the nav graph
    onGetStarted: (Int, String) -> Unit
) {
    // State for the slider (1 to 60 mins)
    var sliderValue by remember { mutableFloatStateOf(15f) }
    val selectedMinutes = sliderValue.roundToInt()

    // State for the selected option. "SCROLL" or "SHORTS"
    var selectedOption by remember { mutableStateOf("SCROLL") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // 1. THE IMAGE (Ice)
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(SurfaceDark)
                .border(2.dp, PrimaryCyan, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("❄️", fontSize = 48.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. THE TEXT
        Text(
            text = "Cooldown Period",
            color = PrimaryCyan,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "How long should we block the apps once your limit is reached?",
            color = TextGray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 3. THE SLIDER (1 to 60 mins)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "$selectedMinutes",
                color = TextWhite,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = " min",
                color = SecondaryPink,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            valueRange = 1f..60f,
            steps = 58, // 58 steps exactly between 1 and 60 for 1-minute increments
            colors = SliderDefaults.colors(
                thumbColor = SecondaryPink,
                activeTrackColor = PrimaryCyan,
                inactiveTrackColor = SurfaceDark,
                activeTickColor = PrimaryCyan,
                inactiveTickColor = SurfaceDark
            ),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1m", color = TextGray, fontSize = 12.sp)
            Text("60m", color = TextGray, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 4. THE 2 OPTIONS (Select any one)
        CooldownOptionCard(
            title = "Disable scroll",
            description = "Disable scroll in 5 sec",
            isSelected = selectedOption == "SCROLL",
            onClick = { selectedOption = "SCROLL" }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CooldownOptionCard(
            title = "Block Short videos",
            description = "Prevents display of short videos",
            isSelected = selectedOption == "SHORTS",
            onClick = { selectedOption = "SHORTS" }
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 5. GET STARTED BUTTON
        Button(
            onClick = { onGetStarted(selectedMinutes, selectedOption) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryCyan,
                contentColor = BackgroundDark
            )
        ) {
            Text(
                text = "Get Started",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// A special card that changes color when selected!
@Composable
fun CooldownOptionCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // If selected, border is Cyan. If not, it's invisible.
    val borderColor = if (isSelected) PrimaryCyan else Color.Transparent
    // If selected, make the card background slightly lighter to pop out.
    val backgroundColor = if (isSelected) SurfaceDark else CardDark

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(2.dp, borderColor, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox/Radio circle indicator
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (isSelected) PrimaryCyan else TextGray, CircleShape)
                    .background(if (isSelected) PrimaryCyan else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Text("✓", color = BackgroundDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = TextGray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Cooldown Period Preview")
@Composable
fun CooldownPeriodScreenPreview() {
    CooldownPeriodScreen(
        onGetStarted = { _, _ -> }
    )
}
