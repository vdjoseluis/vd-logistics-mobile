package com.vdjoseluis.shared

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vdjoseluis.R

@Composable
fun CustomButton (description: String, onClick: ()-> Unit){
    val bgColor: Color
    val icon: ImageVector

    if (description=="Call") {
        bgColor= Color.Green
        icon = Icons.Default.Call
    } else {
        bgColor= MaterialTheme.colorScheme.primaryContainer
        icon= Icons.Default.LocationOn
    }
    Surface (
        modifier = Modifier.size(50.dp),
        shadowElevation = 8.dp,
        shape = CircleShape,
        color = bgColor
    ){
        IconButton(onClick = onClick) {
            Icon(
                icon,
                contentDescription = "$description button",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewCustomButton (){
    Surface (
        modifier = Modifier.size(50.dp),
        shadowElevation = 8.dp,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary
    ){
        IconButton(onClick = {/* TODO: */}) {
            Icon(
                Icons.Default.Call,
                contentDescription = "Custom button",
                tint = Color.Green,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}