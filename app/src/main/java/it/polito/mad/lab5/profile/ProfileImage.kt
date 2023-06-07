package it.polito.mad.lab5.profile

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import it.polito.mad.lab5.R

@Composable
fun ProfileImage(
    imageUri: String,
    setImageUri: (String) -> Unit,
    editmode: Boolean,
    changephotoexpanded: Boolean,
    setChangePhotoExpanded: (Boolean) -> Unit,
    context: Context
){
    val photo=rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()){
        if (it != null) {
            val x= getImageUriFromBitmap(context,it)
            setImageUri(x.toString())

        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { setImageUri( it.toString() )}
    }

    val painter = rememberAsyncImagePainter(
        if (imageUri.isEmpty())
            R.drawable.baseline_person_24
        else
            Uri.parse(imageUri)
    )


    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = CircleShape,
            modifier = Modifier
                .padding(8.dp)
                .size(200.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = "ProfilePic",
                modifier = Modifier.fillMaxWidth(1f),
                contentScale = ContentScale.Crop
            )
        }
        if(editmode)
            Button(onClick = {
                setChangePhotoExpanded(true)
            }){
                Text(text = "Change profile picture")
                DropdownMenu(
                    expanded = changephotoexpanded,
                    onDismissRequest = { setChangePhotoExpanded(false) }
                ) {
                    DropdownMenuItem(
                        text = {  Text("Select from gallery") },
                        onClick = {
                            launcher.launch("image/*")
                            setChangePhotoExpanded(false)
                        }
                    )
                    Divider()
                    DropdownMenuItem(
                        text = { Text("Take from camera") },
                        onClick = {
                            photo.launch()
                            setChangePhotoExpanded(false)
                        }
                    )
                }
        }
    }
}
