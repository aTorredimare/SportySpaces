package it.polito.mad.lab5.authentication

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.mad.lab5.R
import it.polito.mad.lab5.authentication.MyAuthenticationViewModel.ErrorType.*

var passwordVisible = mutableStateOf(false)
var repeatPasswordVisible = mutableStateOf(false)

@Composable
fun MyAuthentication(vm: MyAuthenticationViewModel) {
    val isRegistered = vm.isRegistered.value
    if(isRegistered){
        LoginScreen(vm = vm)
    }
    else{
        RegisterScreen(vm = vm)
    }
}

@Composable
fun RegisterScreen(vm: MyAuthenticationViewModel) {
    val error = vm.error.value
    val focusRequester = FocusRequester()
    BackHandler(enabled = true) {
        vm.isRegistered.value = true
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Text("Register", modifier = Modifier
            .align(CenterHorizontally)
            .padding(bottom = 64.dp), fontSize = 32.sp)
        //email field
        OutlinedTextField(value = vm.email.value,
            shape = RoundedCornerShape(32.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next,keyboardType = KeyboardType.Email ),
            keyboardActions = KeyboardActions(
                onNext = { focusRequester.requestFocus() } // Passa al campo di testo successivo
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 32.dp,
                    end = 32.dp,
                    bottom = if (error.isError && error.type == EMAIL) 0.dp else 8.dp
                ),
            label = { Text("Email") },
            onValueChange = {
                if (error.isError && error.type == EMAIL)
                    vm.error.value.isError = false
                vm.email.value = it
            })

        //email field error
        if(error.isError && error.type == EMAIL) {
            // credential or password error during login
            Text(text = error.description, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
        }

        // password field
        OutlinedTextField(value = vm.password.value,
            shape = RoundedCornerShape(32.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .padding(
                    start = 32.dp,
                    end = 32.dp,
                    bottom = if (error.isError && error.type == PASSWORD) 0.dp else 8.dp
                ),
            label = { Text("Password") },
            onValueChange = {
                vm.password.value = it
                if (error.isError && error.type == PASSWORD)
                    vm.error.value.isError = false
                else if (error.isError && error.type == REPEAT_PASSWORD)
                    if(vm.password.value == vm.repeatPassword.value)
                        vm.error.value.isError = false
            },
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible.value)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                // Please provide localized description for accessibility services
                val description = if (passwordVisible.value) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    Icon(imageVector = image, description)

                }
            }
        )

        // password error field
        if(error.isError && error.type == PASSWORD) {
            // credential or password error during login
            Text(text = error.description, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
        }

        // repeat password field
        OutlinedTextField(value = vm.repeatPassword.value,
            shape = RoundedCornerShape(32.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 32.dp,
                    end = 32.dp,
                    bottom = if (error.isError && error.type == REPEAT_PASSWORD) 0.dp else 8.dp
                ),
            label = { Text("Repeat Password") },
            onValueChange = {
                vm.repeatPassword.value = it
                if (error.isError && error.type == REPEAT_PASSWORD)
                    if(vm.password.value == vm.repeatPassword.value)
                        vm.error.value.isError = false
            },
            visualTransformation = if (repeatPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (repeatPasswordVisible.value)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                // Please provide localized description for accessibility services
                val description = if (repeatPasswordVisible.value) "Hide password" else "Show password"

                IconButton(onClick = { repeatPasswordVisible.value = !repeatPasswordVisible.value }) {
                    Icon(imageVector = image, description)

                }
            }
        )

        // repeat password error field
        if(error.isError && error.type == REPEAT_PASSWORD) {
            // credential or password error during login
            Text(text = error.description, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 32.dp, start = 32.dp, end = 32.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { vm.isRegistered.value = true}, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )) {
                Text(text = "Back")
            }
            Button(
                onClick = { vm.firebaseSignUpWithEmailAndPassword() },

            ) {
                Text(text = "Submit")
            }
        }
    }
}

@Composable
fun LoginScreen(vm: MyAuthenticationViewModel) {
    val error = vm.error.value
    val focusRequester = FocusRequester()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp, bottom = 64.dp)
                .align(CenterHorizontally), text = "SportySpaces", fontSize = 32.sp,
            textAlign = TextAlign.Center
        )

        Image(
                    painter = painterResource(R.mipmap.ic_launcher_foreground),
                    alignment = Alignment.Center, // Sostituisci con il nome del file dell'immagine scaricata
                    contentDescription = "Field Icon",
                    modifier = Modifier.scale(3f)
        )

        Text( modifier = Modifier
            .padding(start = 32.dp, bottom = 16.dp, end = 32.dp, top = 64.dp)
            .align(CenterHorizontally), text = "Please enter your credentials.")


        OutlinedTextField(value = vm.email.value,
            shape = RoundedCornerShape(32.dp),
            singleLine = true,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 32.dp,
                    end = 32.dp,
                    bottom = if (error.isError && (error.type == EMAIL)) 0.dp else 8.dp
                ),
            label = { Text(text = "Email") },
            onValueChange = {
                if (error.isError && (error.type == CREDENTIAL || error.type == EMAIL))
                    vm.error.value.isError = false
                vm.email.value = it
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next,keyboardType = KeyboardType.Email),
            keyboardActions = KeyboardActions(
                onNext = { focusRequester.requestFocus() } // Passa al campo di testo successivo
            ))

        if (error.isError && error.type == EMAIL) {
            //  error during login
            Text(
                text = error.description,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        OutlinedTextField(value = vm.password.value,
            shape = RoundedCornerShape(32.dp),
            singleLine = true,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .padding(
                    start = 32.dp,
                    end = 32.dp,
                    bottom = if (error.isError && (error.type == CREDENTIAL || error.type == PASSWORD)) 0.dp else 8.dp
                ),
            label = { Text(text = "Password") },
            onValueChange = {
                if (error.isError && (error.type == CREDENTIAL || error.type == PASSWORD))
                    vm.error.value.isError = false
                vm.password.value = it
            },
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible.value)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                // Please provide localized description for accessibility services
                val description =
                    if (passwordVisible.value) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    Icon(imageVector = image, description)

                }
            }
        )
        if (error.isError && (error.type == CREDENTIAL || error.type == PASSWORD)) {
            // credential or password error during login
            Text(
                text = error.description,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }


        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp, bottom = 16.dp),
            onClick = { vm.firebaseSignInWithEmailAndPassword() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = "Sign In")
        }
        TextButton(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, end = 32.dp, bottom = 16.dp),
            onClick = { vm.isRegistered.value = false }
        ) {
            Text(text = "Don't have an account? Sign Up!")
        }

    }
}

