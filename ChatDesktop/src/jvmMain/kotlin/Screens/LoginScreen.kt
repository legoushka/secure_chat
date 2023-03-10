package Screens

import ViewModel
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.materialIcon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun LoginScreen(
    viewModel: ViewModel,
    modifier: Modifier = Modifier.padding(24.dp)
) {
    Surface (modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if(viewModel.isRegisterModeActive.value)
                {Arrangement.Start} else {Arrangement.SpaceBetween}
            ) {
                if (viewModel.isRegisterModeActive.value) {
                    Button(
                        onClick = { viewModel.isRegisterModeActive.value = false },
                    ) {
                        Icon(
                            painter = rememberVectorPainter(Icons.Default.ArrowBack), contentDescription = "??????????",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(32.dp))
                }
                Text(
                    text = if (!viewModel.isRegisterModeActive.value) {
                        "????????"
                    } else {
                        "??????????????????????"
                    },
                    style = MaterialTheme.typography.h5
                )
                if (!viewModel.isRegisterModeActive.value){
                    Button(
                        onClick = { viewModel.isDarkThemeActivated.value = !viewModel.isDarkThemeActivated.value},
                    ) {
                        Icon(
                            painter = rememberVectorPainter(Icons.Default.Star), contentDescription = "?????????? ????????",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

            }
            Spacer(modifier = Modifier.height(24.dp))
            if (!viewModel.isRegisterModeActive.value) {
                OutlinedTextField(
                    value = viewModel.serverAddress.value,
                    label = { Text("?????????? ??????????????") },
                    onValueChange = {
                        viewModel.serverAddress.value = it
                        viewModel.isServerAddressValid.value = true
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = !viewModel.isServerAddressValid.value
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.serverPort.value,
                    label = { Text("???????? ??????????????") },
                    onValueChange = {
                        viewModel.serverPort.value = it.filter { it.isDigit() }
                        viewModel.isServerAddressValid.value = true
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = !viewModel.isServerAddressValid.value
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
            OutlinedTextField(
                value = viewModel.loginScreenPhoneNumber.value,
                label = {
                    if (viewModel.isPhoneNumberValid.value) {
                        Text("?????????? ????????????????")
                    } else {
                        Text("???????????????? ???????????? ????????????")
                    }
                },
                onValueChange = {
                    viewModel.loginScreenPhoneNumber.value = it.filter { it.isDigit() }
                    viewModel.isPhoneNumberValid.value = true
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = !viewModel.isPhoneNumberValid.value or !viewModel.isUserDataValid.value
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.loginScreenPassword.value,
                label = { Text("????????????") },
                onValueChange = {
                    viewModel.loginScreenPassword.value = it
                    viewModel.isUserDataValid.value = true
                },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = !viewModel.isUserDataValid.value
            )

            if (viewModel.isRegisterModeActive.value) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.loginScreenName.value,
                    label = { Text("??????") },
                    onValueChange = {
                        viewModel.loginScreenName.value = it
                        viewModel.isUserDataValid.value = true
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = !viewModel.isUserDataValid.value
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.loginScreenSurname.value,
                    label = { Text("??????????????") },
                    onValueChange = {
                        viewModel.loginScreenSurname.value = it
                        viewModel.isUserDataValid.value = true
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = !viewModel.isUserDataValid.value
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "???????????????? ?????? ??????:", modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.Start))
                }
                Row(
                    Modifier.selectableGroup().fillMaxWidth().wrapContentWidth(Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = viewModel.loginScreenGender.value,
                            onClick = { viewModel.loginScreenGender.value = true }
                        )
                        Text(text = "??????.", modifier = Modifier.wrapContentHeight())
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = !viewModel.loginScreenGender.value,
                            onClick = { viewModel.loginScreenGender.value = false }
                        )
                        Text(text = "??????.", modifier = Modifier.wrapContentHeight())
                    }

                }
                OutlinedTextField(
                    value = viewModel.loginScreenIconUrl.value,
                    label = { Text("?????????????????????? ?????????????? URL") },
                    onValueChange = {
                        viewModel.loginScreenIconUrl.value = it
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                if (!viewModel.isRegisterModeActive.value) {
                    viewModel.onLoginPressButton()
                } else {
                    viewModel.onRegisterPressButton()
                }
            }) {
                if (!viewModel.isRegisterModeActive.value) {
                    Text("????????")
                } else {
                    Text("????????????????????????????????????")
                }
            }

            if (!viewModel.isRegisterModeActive.value) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                    viewModel.isRegisterModeActive.value = true
                }) {
                    Text("??????????????????????")
                }
            }
        }
    }

}
