package it.polito.mad.lab5.rate

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.polito.mad.lab5.R
import it.polito.mad.lab5.db.PlayGrounds
import it.polito.mad.lab5.db.Rating
import java.time.LocalDate


@Composable
fun Rate() {
    val vmRatings: RateViewModel = viewModel()

    val ratings by vmRatings.fetchAllReviews().observeAsState()
    val fieldsNotRated by vmRatings.fetchAllFields().observeAsState()
    val allFields by vmRatings.fetchAllFields().observeAsState()

    val (readMode, setReadMode) = remember { mutableStateOf(true) }
    val (showForm, setShowForm) = remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedField by remember { mutableStateOf("Select field") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Reviews", textAlign = TextAlign.Center)},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.onPrimary)
            )
        },
        content = {
            Column(modifier = Modifier.padding(it)) {
                if(readMode) {
                    LazyColumn {
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(onClick = { setReadMode(false) }) {
                                    Text("Add new review")
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        allFields?.forEach { f ->
                            item {
                                ratings?.filter { item -> item.field == f.playgroundName }
                                    ?.let { r ->
                                        FieldCard(
                                            field =  f,
                                            reviews = r,
                                            vm = vmRatings
                                        )
                                    }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
                else {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable(onClick = { expanded = true })
                                .background(Color.LightGray),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                selectedField, modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.Center)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown Icon",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            fieldsNotRated?.forEach { field -> DropdownMenuItem(
                                text = { Text(text = field.playgroundName!!, modifier = Modifier
                                    .fillMaxWidth()
                                    .align(CenterHorizontally))},
                                onClick = {
                                    selectedField = field.playgroundName!!
                                    expanded = false
                                    setShowForm(true)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )}
                        }

                        if (showForm)
                            InsertReviewForm(modifier = Modifier.padding(24.dp), selectedField, vmRatings) {
                                setReadMode(true)
                                selectedField = "Select field"
                                setShowForm(false)
                            }
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = {
                                setReadMode(true)
                                setShowForm(false)
                                selectedField = "Select field"
                            },
                                colors = ButtonDefaults
                                    .buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                                Text("Back")
                            }
                        }
                    }
                }
            }
        })
}

@Composable
fun FieldCard(field: PlayGrounds, reviews: List<Rating>, vm: RateViewModel) {
    val (showContent, setShowContent) = remember { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),

        ){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically)
                .background(MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            setShowContent(!showContent)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                modifier = Modifier
                                    .weight(0.4f)
                                    .clip(shape = CircleShape)
                                    .size(62.dp),
                                painter = painterResource(
                                    when(field.sportName){
                                        "Football" -> R.drawable.football
                                        "Basketball" -> R.drawable.basketball
                                        "Golf" -> R.drawable.golf
                                        else -> R.drawable.judo
                                    }
                                ),
                                contentDescription = null,
                            )

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.6f),
                                text = field.playgroundName!!,
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center
                            )

                        }
                        if(showContent) {
                            if(reviews.isEmpty()) {
                                Spacer(modifier = Modifier.height(24.dp))
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "No reviews yet...",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                            reviews.forEach { r ->
                                ReviewComponent(
                                    r,
                                    vm,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun ReviewComponent(review: Rating, vm: RateViewModel, modifier: Modifier) {
    val (expanded, setExpanded) = remember { mutableStateOf(false) }
    val starsColor = Color(0xFFFFC107)
    val loggedUser = vm.fetchLoggedUser().value

    Column(
        modifier = modifier.then(
            Modifier
                .padding(start = 16.dp, end = 16.dp)
        )
    ) {
        Box(
            modifier = Modifier
                .align(CenterHorizontally)
                .clickable { setExpanded(!expanded) }
        ) {
            Column(
                modifier = Modifier.wrapContentSize(align = Alignment.Center)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(align = CenterHorizontally)
                ) {
                    for (i in 1..5) {
                        if (i <= review.score!!) {
                            Icon(
                                modifier = Modifier.height(48.dp),
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                                tint = starsColor,
                            )

                        } else {
                            Icon(
                                modifier = Modifier.height(48.dp),
                                imageVector = Icons.Outlined.StarOutline,
                                contentDescription = null,
                                tint = starsColor,
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(align = CenterHorizontally)
                ) {
                    Text(
                        text = review.user!!.nickname,
                        style = TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(align = CenterHorizontally)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(align = CenterHorizontally)
                        .padding(8.dp)
                ) {
                    Text(
                        text = review.date!!,
                        style = TextStyle(fontSize = 18.sp, textAlign = TextAlign.Center),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(align = CenterHorizontally)
                    )
                }
            }
        }


        if (expanded) {
            if(review.reviewText!! != "") {
                Text(
                    text = review.reviewText!!,
                    style = TextStyle(fontSize = 22.sp, textAlign = TextAlign.Center),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(align = CenterHorizontally)
                        .padding(16.dp)
                )
            }
            
            if(loggedUser?.nickname == review.user!!.nickname) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(50))
                ) {
                    IconButton(
                        onClick = { vm.removeReview(review.id!!) },
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFFF808C))
                            .size(48.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InsertReviewForm(modifier: Modifier, selectedField: String, vm: RateViewModel, onButtonClick: (Boolean) -> Unit) {
    val context = LocalContext.current
    var content by remember { mutableStateOf("") }
    val (rating, setRating) = remember { mutableStateOf(0) }
    val starsColor = Color(0xFFFFC107)

    Column(
        modifier = modifier.then(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()),
        horizontalAlignment = CenterHorizontally)
    {
        val focusManager = LocalFocusManager.current

        Row(modifier = modifier) {
            for(i in 1..5 ){
                if(i <= rating)
                    IconButton(
                        modifier = Modifier.width(32.dp),
                        onClick = {
                            setRating(i)
                        }) {
                        Icon(
                            modifier = Modifier.height(48.dp),
                            imageVector = Icons.Outlined.Star,
                            contentDescription = null,
                            tint = starsColor,
                        )
                    }
                else
                    IconButton(
                        modifier = Modifier.width(32.dp),
                        onClick = {
                            setRating(i)
                        }) {
                        Icon(
                            modifier = Modifier.height(48.dp),
                            imageVector = Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = starsColor,
                        )
                    }
            }
        }

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Review...") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()})
        )

       Spacer(modifier = Modifier.height(16.dp))
       Row(
           horizontalArrangement = Arrangement.Center,
           modifier = Modifier.fillMaxWidth()
       ) {
           Button(onClick = {
               vm.addReview(selectedField, content, rating, Firebase.auth.uid!!, LocalDate.now())
               Toast.makeText(context, "Review saved", Toast.LENGTH_LONG).show()
               onButtonClick(true)
           }) {
               Text("Save")
           }
       }
   }
}
