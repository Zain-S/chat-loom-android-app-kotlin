package com.saeedtechies.chatloom.presentation.auth

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.saeedtechies.chatloom.R
import com.saeedtechies.chatloom.domain.model.User
import com.saeedtechies.chatloom.utils.HandleResponse
import com.saeedtechies.chatloom.utils.USER_CREATED
import kotlinx.serialization.Serializable

@Serializable
object SignUp

@Composable
fun SignUpScreen(navController: NavController, viewModel: AuthViewModel) {
    val response by viewModel.createUserResponse.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var isShowLoader by remember { mutableStateOf(false) }
    response?.HandleResponse(
        onLoad = {
            isShowLoader = it
        },
        onSuccess = { _ ->
            Toast.makeText(context, USER_CREATED, Toast.LENGTH_SHORT).show()
        },
        onError = { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        },
    )
    SignUpContent(
        isShowLoader = isShowLoader,
        onBackClick = { navController.popBackStack() },
        onSignUpClick = { user, uri ->
            viewModel.createUser(user, uri)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpContent(isShowLoader: Boolean = false, onBackClick: (() -> Unit), onSignUpClick: (user: User, uri: Uri?) -> Unit) {
    var phoneNumber by remember { mutableStateOf("") }
    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(uri, flag)
            profilePictureUri = uri
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Sign Up") },
                navigationIcon = {
                    IconButton(onClick = { onBackClick.invoke() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                })
        },
        content = { paddingValues ->
            if (isShowLoader) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                )  {
                    CircularProgressIndicator()
                }
            }
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 10.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        modifier =
                        Modifier
                            .size(64.dp)
                            .background(Color.Gray, CircleShape)
                            .testTag("profilePictureButton")
                    ) {
                        if (profilePictureUri != null) {
                            AsyncImage(
                                model = profilePictureUri,
                                contentDescription = "Picture"
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_add_a_photo_24),
                                contentDescription = "Import Profile Picture",
                                tint = Color.White,
                                modifier = Modifier.testTag("noProfilePic")
                            )
                        }
                    }
                }
                /*OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("firstNameField"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lastNameField"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("emailField"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )*/
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("phoneField"),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
                )
                Spacer(modifier = Modifier.height(16.dp).weight(1f))
                Button(
                    onClick = { onSignUpClick(
                        User(phone = phoneNumber),
                        profilePictureUri
                    )},
                    enabled = phoneNumber.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .testTag("createAccountButton")
                ) {
                    Text("Create Account")
                }
            }
        })
}

@Preview(showBackground = true)
@Composable
fun PreviewSignUpScreen() {
    SignUpContent(true, onBackClick = {}, onSignUpClick = {
        user, uri ->
        // Handle back button click
    })
}