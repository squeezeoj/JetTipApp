package com.learning.jettipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.learning.jettipapp.components.InputField
import com.learning.jettipapp.ui.theme.JetTipAppTheme
import com.learning.jettipapp.util.calculateTotalPerPerson
import com.learning.jettipapp.util.calculateTotalTip
import com.learning.jettipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetTipAppTheme {
                MyApp {
                    Column() {
                        MainContent()
                    }
                }
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colors.background
    ) {
        content()
    }
}

@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .clip(shape = RoundedCornerShape(CornerSize(12.dp))),
        color = Color.LightGray
    ) {
        Column(modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Total Per Person",
                style = MaterialTheme.typography.h5)
            val total = "%.2f".format(totalPerPerson)
            Text(text ="$${total}",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)  // For LocalSoftwareKeyboardController Line
@Preview
@Composable
fun MainContent() {
    val splitByState = remember {
        mutableStateOf(1)
    }

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }

    BillForm(
        splitByState = splitByState,
        tipAmountState = tipAmountState,
        totalPerPersonState = totalPerPersonState
    ) {
        billAmt -> Log.d("******", "MainContent: $billAmt")
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(modifier: Modifier = Modifier,
             splitByState: MutableState<Int>,
             tipAmountState: MutableState<Double>,
             totalPerPersonState: MutableState<Double>,

            onValChange: (String) -> Unit = {}
             ) {
    val totalBillState = remember {
        mutableStateOf("")
    }

    val validSate = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val sliderPositionState = remember {
        mutableStateOf(0.15f)
    }

    val tipPercentage = (sliderPositionState.value * 100).toInt()

    val keyboardController = LocalSoftwareKeyboardController.current

    TopHeader(totalPerPerson = totalPerPersonState.value)

    Surface(modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(
                valueState = totalBillState,
                labelID = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validSate) return@KeyboardActions
                    onValChange(totalBillState.value.trim())
                    keyboardController?.hide()      // Has to do with Experimental API
                }
            )
            if (validSate) {

                //--- Split Buttons
                Row(
                    modifier = Modifier
                        .padding(3.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Split", modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                        ))
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                Log.d("******", "Minus")
                                if (splitByState.value > 1) {
                                    splitByState.value = splitByState.value - 1
                                }
                                totalPerPersonState.value = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = splitByState.value, tipPercentage = tipPercentage)
                                }
                        )
                        Text(text = "${splitByState.value}",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                Log.d("******", "Plus")
                                splitByState.value = splitByState.value + 1
                                totalPerPersonState.value = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = splitByState.value, tipPercentage = tipPercentage)
                            }
                        )
                    }
                }

                //--- Tip Amount
                Row(modifier = Modifier
                    .padding(3.dp)
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Tip", modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    ))
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(text = "${tipAmountState.value}")
                }

                //--- Percentage Slider
                Column(modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Slider(
                        value = sliderPositionState.value,
                        onValueChange = { newVal ->
                            sliderPositionState.value = newVal
                            Log.d("******", "Tip Slider: ${newVal}")
                            tipAmountState.value = calculateTotalTip(totalBill = totalBillState.value.toDouble(), tipPercentage = tipPercentage)
                            totalPerPersonState.value = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = splitByState.value, tipPercentage = tipPercentage)
                        },
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        steps = 6
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = "${tipPercentage}%")
                }

            } else {
                Box() {}
            }

        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetTipAppTheme {
        MyApp {
            Text("Hello World")
        }
    }
}