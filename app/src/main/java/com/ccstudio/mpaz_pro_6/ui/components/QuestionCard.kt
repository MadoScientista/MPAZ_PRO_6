package com.ccstudio.mpaz_pro_6.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ccstudio.mpaz_pro_6.data.local.dao.PreguntaConAlternativas

@Composable
fun QuestionCard(
    index: Int,
    preguntaConAlternativas: PreguntaConAlternativas,
    modifier: Modifier = Modifier,
    isPreviewMode: Boolean = true,
    onEditClick: (() -> Unit)? = null
) {
    val pregunta = preguntaConAlternativas.pregunta
    val alternativas = preguntaConAlternativas.alternativas

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Pregunta Enunciado
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pregunta $index",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )
                if (onEditClick != null) {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar pregunta",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = pregunta.enunciado,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Alternativas
            alternativas.forEachIndexed { altIndex, alternativa ->
                val optionLetter = ('A' + altIndex).toString()
                val isCorrect = alternativa.correcta && isPreviewMode

                val defaultSurfaceVariant = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                val defaultOnSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

                val containerColor = if (isCorrect) {
                    Color(0xFFE8F5E9) // Soft Green background
                } else {
                    defaultSurfaceVariant
                }

                val borderColor = if (isCorrect) {
                    Color(0xFF2E7D32) // Dark Green border
                } else {
                    Color.Transparent
                }

                val textColor = if (isCorrect) {
                    Color(0xFF1B5E20)
                } else {
                    defaultOnSurfaceVariant
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(
                            width = if (isCorrect) 1.5.dp else 0.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    color = containerColor,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isCorrect) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outlineVariant
                        ) {
                            Text(
                                text = optionLetter,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCorrect) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = alternativa.enunciado,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = textColor,
                                fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        if (isCorrect) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Alternativa correcta",
                                tint = Color(0xFF2E7D32)
                            )
                        } else {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Outlined.Circle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
