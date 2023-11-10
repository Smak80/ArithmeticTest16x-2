package ru.smak.arithmetictest16x_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.smak.arithmetictest16x_2.ui.theme.ArithmeticTest16x2Theme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArithmeticTest16x2Theme {
                val exerciseCount = 5
                var attempt by remember { mutableStateOf(1) }
                var currentExercise by remember { mutableStateOf(1) }
                var correctAnswers by remember { mutableStateOf(0) }
                val availableOperators = listOf('+', '-', '*', '/')
                val operators = remember(attempt) { List(exerciseCount){
                    availableOperators[Random.nextInt(4)]
                }}
                val operands = remember(attempt){List(exerciseCount){
                    when(operators[it]){
                        '*' -> Random.nextInt(-20, 21) to Random.nextInt(21)
                        '/' -> {
                            val res = Random.nextInt(-20, 21)
                            val den = Random.nextInt(1,21)
                            res * den to den
                        }
                        else -> Random.nextInt(-99, 100) to Random.nextInt(100)
                    }
                }}
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (i in 0 until exerciseCount) {
                        ExerciseCard(
                            operands[i].first,
                            operands[i].second,
                            operators[i],
                            attempt,
                            isVisible = i < currentExercise,
                        ){
                            currentExercise++
                            if (it) correctAnswers++
                        }
                    }
                    if (currentExercise > exerciseCount){
                        Text(
                            text = stringResource(R.string.result, correctAnswers, exerciseCount),
                            modifier = Modifier
                                .fillMaxWidth(),
                            style = TextStyle(textAlign = TextAlign.Center),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = when(correctAnswers){
                                in 0..2 -> Color.Red
                                3 -> Color.Blue
                                in 4..5 -> Color(0f, 0.6f, 0f)
                                else -> Color.Black
                            }
                        )
                        Button(onClick = {
                            correctAnswers = 0
                            currentExercise = 1
                            attempt++
                        }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }
        }
    }
}

fun checkResult(op1: Int, op2: Int, operator: Char, userResult: Int?) = when (operator) {
    '+' -> op1 + op2 == userResult
    '-' -> op1 - op2 == userResult
    '*' -> op1 * op2 == userResult
    '/' -> op1 / op2 == userResult

    else -> false
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseCard(
    op1: Int,
    op2: Int,
    operator: Char,
    attempt: Int,
    modifier: Modifier = Modifier,
    isVisible: Boolean = false,
    onGotAnswer: (Boolean)->Unit = {},
){
    val exercise = "$op1 $operator $op2 = "
    var color by remember(attempt) { mutableStateOf(Color.Black) }
    var userText by remember(attempt) { mutableStateOf("") }
    var enable by remember(attempt) { mutableStateOf(true) }
    ElevatedCard(modifier = modifier.alpha(if (isVisible) 1f else 0f)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            ) {
            Text(
                text = exercise,
                modifier = Modifier.weight(3f),
                fontSize = 32.sp
                )
            OutlinedTextField(
                value = userText,
                onValueChange = {
                    if ((it.toIntOrNull() != null || it.isBlank() || it == "-") && it.length <= 5)
                        userText = it
                },
                modifier = Modifier
                    .weight(2f)
                    .padding(horizontal = 8.dp),
                textStyle = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold, color = color),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = enable && isVisible
            )
            IconButton(
                onClick = {
                    if (enable && isVisible) {
                        val userResult = userText.toIntOrNull()
                        val result = checkResult(
                            op1,
                            op2,
                            operator,
                            userResult
                        )
                        color = if (result) Color.Green else Color.Red
                        enable = false
                        onGotAnswer(result)
                    }
                },
                modifier = Modifier.weight(1f)) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_circle_right_64),
                        contentDescription = stringResource(id = R.string.btn_next),
                        tint = color
                    )
                }
        }
    }
}

@Preview
@Composable
fun ExerciseCardPreview(){
    ArithmeticTest16x2Theme {
        ExerciseCard(18, 74, '+', 0)
    }
}