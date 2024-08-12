package com.example.myfirstworkingapplication

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfirstworkingapplication.ui.theme.MyFirstWorkingApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFirstWorkingApplicationTheme {
                TodoList()
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun TodoList() {
    val paddedScreenHeight: Int = LocalConfiguration.current.screenHeightDp - 16

    var checkedStates by remember {
        mutableStateOf(mutableMapOf<String, Boolean>())
    }

    var text by remember {
        mutableStateOf("")
    }

    var texts by remember {
        mutableStateOf(listOf<String>())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn (
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(bottom = 128.dp, top = 32.dp)

        ) {
            items(texts) { currentText ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = currentText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )
                    checkedStates[currentText]?.let {
                        Checkbox (
                            onCheckedChange = {
                                if(checkedStates[currentText] == false){
                                    checkedStates[currentText] = true
                                } else {
                                    checkedStates[currentText] = false
                                }
                            },
                            checked = it
                        )
                    }
                }
                Divider()
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)

        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                },
                modifier = Modifier
                    .weight(1f)
            )
            Button(onClick = {
                if(text.isNotBlank()) {
                    texts = texts + text
                    checkedStates[text] = false
                    text = ""
                }
            }) {
                Text(text = "add")
            }
            Button(onClick = {
                // Check if all checkboxes are unchecked
                val allUnchecked = checkedStates.values.all { !it }

                if (allUnchecked) {
                    // Optional: Show a message or take some action when no checkboxes are checked
                    println("No items are selected for removal.")
                } else {
                    // Proceed with removal
                    val newTexts = texts.filter { currentText ->
                        !checkedStates[currentText]!!
                    }
                    texts = newTexts
                    checkedStates = checkedStates.filterKeys { key ->
                        newTexts.contains(key)
                    }.toMutableMap()
                }
            }) {
                Text(text = "remove")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyFirstWorkingApplicationTheme {
        Surface(
            Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            TodoList()
        }
    }
}