package com.kimnlee.cardmanagement.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CardManagementDirectRegistrationScreen(
    onNavigateBack: () -> Unit
) {
    FlowColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ){
        Column (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight(0.7f),
        ){
            Column (){
                // var cardNumber by remember { mutableStateOf("") }
                val (cardNumber, setCardNumber) = remember {
                    mutableStateOf("")
                }
                Text(text="카드 번호", modifier = Modifier.padding(bottom = 10.dp))
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { setCardNumber(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.padding(16.dp))
            Row {
                Column (
                ){
                    val (expirationDate, setExpirationDate) = remember {
                        mutableStateOf("")
                    }
                    Text(text="유효 기간", modifier = Modifier.padding(bottom = 10.dp))
                    OutlinedTextField(
                        value = expirationDate,
                        onValueChange = { setExpirationDate(it) },
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column (

                ){
                    val (cvc, setCvc) = remember {
                        mutableStateOf("")
                    }
                    Text(text="CVC", modifier = Modifier.padding(bottom = 10.dp))
                    OutlinedTextField(
                        value = cvc,
                        onValueChange = { setCvc(it) }
                    )
                }
            }
            Spacer(modifier = Modifier.padding(16.dp))
            Column {
                val (userName, setUserName) = remember { mutableStateOf("") }
                Text(text="이름", modifier = Modifier.padding(bottom = 10.dp))
                OutlinedTextField(
                    value = userName,
                    onValueChange = {setUserName(it)},
                    modifier = Modifier.fillMaxWidth())
            }
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            val context = LocalContext.current
            Button(onClick = {
                    Toast.makeText(context, "확인 버튼", Toast.LENGTH_SHORT).show()
                  },
            ) {
                Text("확인")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onNavigateBack,

            ) {
                Text("취소")
            }
        }
    }
}
