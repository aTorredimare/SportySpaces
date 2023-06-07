package it.polito.mad.lab5.authentication


import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MyAuthenticationViewModel() : ViewModel() {
    enum class ErrorType {EMAIL, PASSWORD, REPEAT_PASSWORD, CREDENTIAL, NULL}
    data class MyError(var isError: Boolean, val type: ErrorType, val description: String)

    private val auth = Firebase.auth

    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val repeatPassword = mutableStateOf("")

    val isRegistered = mutableStateOf(true)
    val isLogged = mutableStateOf(false)

    val error = mutableStateOf( MyError(false, ErrorType.NULL,"") )

    fun resetCredentials(){
        email.value = ""
        password.value = ""
        repeatPassword.value = ""
    }

    fun firebaseSignUpWithEmailAndPassword(/*email: String, password: String*/) {

        if (email.value != "" && password.value != "" && password.value.length >= 6 && repeatPassword.value != "" && password.value == repeatPassword.value) {
            viewModelScope.launch {
                try {
                    auth.createUserWithEmailAndPassword(email.value, password.value).await()
                    Log.d(ContentValues.TAG, "User registered")
                    isRegistered.value = true
                    error.value.isError = false
                }
                catch (e: FirebaseAuthUserCollisionException){
                    error.value = MyError(true, ErrorType.EMAIL,"Email address is already registered")
                    Log.w(ContentValues.TAG, "Error registering user", e)
                }
                catch (e: FirebaseAuthWeakPasswordException) {
                    error.value = MyError(true, ErrorType.PASSWORD,"Password must be at least 6 characters")
                    Log.w(ContentValues.TAG, "Error registering user", e)
                }
                catch (e: FirebaseAuthInvalidCredentialsException){
                    error.value = MyError(true, ErrorType.EMAIL,"Email address is badly formatted")
                    Log.w(ContentValues.TAG, "Error registering user", e)
                }
                catch (e: Exception) {
                    Log.w(ContentValues.TAG, "Error registering user", e)
                }
            }
        }
        else if(email.value == "") {
            error.value = MyError(true, ErrorType.EMAIL, "Email address is empty")
        }
        else if(password.value == ""){
            error.value = MyError(true, ErrorType.PASSWORD, "Password field is empty")
        }
        else if(password.value.length < 6){ //weak password for firestone
            error.value = MyError(true, ErrorType.PASSWORD,"Password must be at least 6 characters")
        }
        else {
            error.value = MyError(true, ErrorType.REPEAT_PASSWORD, "Passwords are different")
        }
    }

    fun firebaseSignInWithEmailAndPassword(/*email: String, password: String*/) {
        if (email.value != "" && password.value != "") {
            viewModelScope.launch {
                try {
                    val authResult = auth.signInWithEmailAndPassword(email.value, password.value).await()
                    if(authResult.user != null){
                        Log.d(ContentValues.TAG, "User logged in as " + authResult.user!!.uid)
                        //startListenerRegistration()
                        isLogged.value = true
                    }
                } catch (e: FirebaseAuthInvalidUserException) {
                    error.value = MyError(true, ErrorType.CREDENTIAL, "Credential not found")
                    Log.w(ContentValues.TAG, "Error user login", e)
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    error.value = MyError(true, ErrorType.CREDENTIAL, "Credential not found")
                    Log.w(ContentValues.TAG, "Error user login", e)
                } catch (e: Exception) {
                    Log.w(ContentValues.TAG, "Error user login", e)
                }
            }
        }
        else if(email.value == "") {
            error.value = MyError(true, ErrorType.EMAIL, "Email address is empty")
        }
        else {
            error.value = MyError(true, ErrorType.PASSWORD, "Password field is empty")
        }
    }
}