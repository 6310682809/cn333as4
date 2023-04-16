package com.example.randomimage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest

enum class RandomImageGameScreen(val title: String) {
    Start(title = "Random Image"),
    Image(title = "Image"),
}

var selectedText = ""
var message = ""

@Composable
fun ScreenDisplay() {
    var width by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
//    var message by remember { mutableStateOf("") }

    Column {
        val navController = rememberNavController()

        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentScreen = RandomImageGameScreen.valueOf(
            backStackEntry?.destination?.route ?: RandomImageGameScreen.Start.name
        )

        Scaffold(
            topBar = {
                AppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() }
                )
            }
        ){ innerPadding ->
            NavHost(navController,
                startDestination = RandomImageGameScreen.Start.name,
                modifier = Modifier.padding(innerPadding)) {
                composable(route = RandomImageGameScreen.Start.name) {
                    Column {
                        Text(
                            text = stringResource(R.string.width),
                            fontSize = 20.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                        EditWidth(
                            value = width,
                            onValueChange = { width = it }
                        )
                        Text(
                            text = stringResource(R.string.height),
                            fontSize = 20.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                        EditHeight(
                            value = height,
                            onValueChange = { height = it }
                        )
                        Text(
                            text = stringResource(R.string.type),
                            fontSize = 20.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                        DropDownMenu()
//                        val check = checkInput((if(width.isEmpty()) 0 else width.toInt()), (if(height.isEmpty()) 0 else width.toInt()))
//                        Text(
//                            text = message,
//                            style = MaterialTheme.typography.h4,
//                            fontSize = 20.sp,
//                            color = Color.Red,
//                            modifier = Modifier.padding(20.dp)
//                        )
//                        if(check){
                            Button(modifier = Modifier.padding(20.dp), onClick = { navController.navigate(RandomImageGameScreen.Image.name) }) { Text("SUBMIT") }
//                        } else{
//                            Button(modifier = Modifier.padding(20.dp), onClick = {}) { Text("SUBMIT") }
//                        }
                    }
                }
                composable(route = RandomImageGameScreen.Image.name) {
                    DisplayImage(width.toInt(), height.toInt(), selectedText)
                }
            }
        }
    }
}

@Composable
fun checkInput(width: Int, height: Int):Boolean {
    if ((8 > width || width > 2000 ) || (8 > height || height > 2000 )){
        message = "The width and height must in the range of 8-2000."
        return false
    }
    else if(selectedText.isEmpty()) {
        message = "Please select a type"
        return  false
    } else{
        message = ""
        return true
    }
}

@Composable
fun AppBar(
    currentScreen: RandomImageGameScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(currentScreen.title) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun DisplayImage(width: Int, height: Int, type: String) {
    val src = "https://loremflickr.com/$width/$height/$type"
    AsyncImage(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(src)
            .crossfade(true)
            .build(),
        contentDescription = "",
        error = painterResource(R.drawable.ic_broken_image),
        placeholder = painterResource(R.drawable.loading_img),
        modifier = Modifier
            .width(width.dp)
            .height(height.dp)
    )
}

@Composable
fun DropDownMenu() {
    var expanded by remember { mutableStateOf(false) }
    val suggestions = listOf("movie", "game", "album", "book", "face", "fashion", "shoes", "watch", "furniture")
//    var selectedText by remember { mutableStateOf("") }

    var textfieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(Modifier.padding(20.dp)) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = { selectedText = it },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    //This value is used to assign to the DropDown the same width
                    textfieldSize = coordinates.size.toSize()
                },
            label = { Text("Label") },
            trailingIcon = {
                Icon(icon,"contentDescription",
                    Modifier.clickable { expanded = !expanded })
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current){textfieldSize.width.toDp()})
        ) {
            suggestions.forEach { label ->
                DropdownMenuItem(onClick = {
                    selectedText = label
                    expanded = false
                }) {
                    Text(text = label)
                }
            }
        }
    }
}

@Composable
fun EditWidth(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.width)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun EditHeight(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.height)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}