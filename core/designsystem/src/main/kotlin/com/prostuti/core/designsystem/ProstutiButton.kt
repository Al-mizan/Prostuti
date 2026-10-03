package com.prostuti.core.designsystem

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Shared primary button. Shows a constrained 20dp spinner when `loading` is true
 * and maintains consistent minimum 48dp touch targets without layout jumping.
 */
@Composable
fun ProstutiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        shape = RoundedCornerShape(12.dp),
        colors = colors,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp),
    ) {
        if (loading) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp),
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}
