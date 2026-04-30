package com.scrollblocker.ui.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
// IMPORTING YOUR CENTRALIZED COLORS
import com.scrollblocker.ui.theme.PrimaryCyan
import com.scrollblocker.ui.theme.SecondaryPink
import com.scrollblocker.ui.theme.WarningOrange
import com.scrollblocker.ui.theme.BackgroundDark
import com.scrollblocker.ui.theme.SurfaceDark
import com.scrollblocker.ui.theme.CardDark
import com.scrollblocker.ui.theme.TextWhite
import com.scrollblocker.ui.theme.TextGray

@Composable
fun WelcomeScreen(
    onDailyLimitClick: () -> Unit,
    onBlockAllClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // 1. THE IMAGE (Archery Target)
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(75.dp))
                .background(SurfaceDark)
                .border(2.dp, PrimaryCyan, RoundedCornerShape(75.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Image(painter = painterResource(id = R.drawable.archery_target), contentDescription = "Target")
            Text("🎯", fontSize = 64.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 2. THE TEXT
        Text(
            text = "Hit Your Target.",
            color = PrimaryCyan,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "How would you like to build your focus today? Pick your preferred mode to get started.",
            color = TextGray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 3. THE OPTIONS
        SelectionCard(
            title = "Daily Limit",
            description = "Set a specific time allowance. Block apps only after time runs out.",
            accentColor = SecondaryPink,
            onClick = { onDailyLimitClick() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SelectionCard(
            title = "Block All",
            description = "Zero distractions. Instantly block selected apps from opening.",
            accentColor = WarningOrange,
            onClick = { onBlockAllClick() }
        )
    }
}

@Composable
fun SelectionCard(
    title: String,
    description: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .background(accentColor, RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextWhite,
                    fontSize = 20.sp,
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
@Preview(showBackground = true, name = "Welcome Screen Preview")
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(
        onDailyLimitClick = { /* Do nothing in preview */ },
        onBlockAllClick = { /* Do nothing in preview */ }
    )
}

