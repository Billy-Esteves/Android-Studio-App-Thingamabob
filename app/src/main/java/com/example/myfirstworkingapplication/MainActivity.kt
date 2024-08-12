package com.example.myfirstworkingapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    var allUnchecked = true
    var text by remember {
        mutableStateOf("")
    }

    var notes by remember {
        mutableStateOf(listOf<Note>())
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
            items(notes) { currentNote ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = currentNote.text,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )
                    Checkbox(
                        // When the checkbox is toggled, a new Note instance is created using the
                        // copy function with the updated checked value.
                        // Then, the list is updated with a new list where the modified Note
                        // replaces the old one.
                        onCheckedChange = {
                            val updatedNote = currentNote.copy(checked = !currentNote.checked)
                            notes = notes.map { note ->
                                if (note == currentNote) updatedNote else note
                            }
                        },
                        checked = currentNote.checked
                    )
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
                    var newNote = Note(text, false)
                    notes = notes + newNote
                    text = ""
                }
            }) {
                Text(text = "add")
            }
            Button(onClick = {
                // Check if all checkboxes are unchecked
                allUnchecked = true
                for (note in notes) {
                    if (note.checked) {
                        allUnchecked = false
                        break
                    }
                }

                if (allUnchecked) {
                    // Optional: Show a message or take some action when no checkboxes are checked
                    println("No items are selected for removal.")
                } else {
                    // Proceed with removal
                    val newNotes = notes.filter { currentNote ->
                        !currentNote.checked
                    }
                    notes = newNotes
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