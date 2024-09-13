package com.example.myfirstworkingapplication

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.myfirstworkingapplication.ui.theme.MyFirstWorkingApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // Create DataStore with preferencesDataStore delegate for storing the to-do list
    val Context.dataStore by preferencesDataStore(name = "todo_list")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Enables edge-to-edge drawing of the UI for a better visual experience
        setContent {
            MyFirstWorkingApplicationTheme {
                // Use LocalContext to get the app's context
                val dataStoreContext = LocalContext.current

                // Create an instance of DataStoreManager to handle data persistence
                val dataStoreManager = DataStoreManager(dataStoreContext)

                // Start the NavigationBar composable, passing the DataStoreManager
                NavigationBar(Modifier, dataStoreManager)
            }
        }
    }
}

@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    dataStoreManager: DataStoreManager
) {
    // Remember the drawer state (open/closed) for the navigation drawer
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    // Remember a coroutine scope to manage side-effects such as opening/closing drawer asynchronously
    val scope = rememberCoroutineScope()

    // State variable to track which screen is currently selected
    var selectedScreen by remember { mutableStateOf("To Do List") }

    // Modal drawer layout with the drawer's content and the main screen content
    ModalNavigationDrawer(
        drawerState = drawerState,  // The state of the drawer (open/closed)
        drawerContent = {
            // The content inside the navigation drawer (items like "To Do List" and "Inspirational Quote")
            ModalDrawerSheet {
                DrawerContent(onSelectScreen = { screen ->
                    selectedScreen = screen  // Update the screen based on user selection
                    scope.launch {
                        drawerState.close()  // Close the drawer after selecting an item
                    }
                })
            }
        }
    ) {
        // Main scaffold layout with a top app bar and content area
        Scaffold(
            topBar = {
                TopBar(
                    onOpenDrawer = {  // Handler to open the drawer when the menu icon is clicked
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()  // Toggle the drawer state
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            // The Box layout will display different screens based on the selectedScreen state
            Box(modifier = Modifier.padding(paddingValues)) {
                when (selectedScreen) {
                    "To Do List" -> {
                        TodoList(dataStoreManager = dataStoreManager)  // Show the To-Do list screen
                    }
                    "Inspirational Quote" -> {
                        InspirationalQuote()  // Show the Inspirational Quote screen
                    }
                    else -> {
                        Text("Select a screen from the drawer")  // Default text when no screen is selected
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerContent(
    onSelectScreen: (String) -> Unit,  // Lambda function to notify when a drawer item is selected
    modifier: Modifier = Modifier
) {
    // Static header for the drawer
    Text(
        text = "Tools",
        fontSize = 22.sp,
        modifier = Modifier
            .padding(start = 16.dp, top = 4.dp, bottom = 8.dp)
    )

    // Divider line in the drawer
    HorizontalDivider(
        thickness = 4.dp,
        color = Color.Blue
    )

    // Navigation item for "To Do List"
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Default.Create,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 32.dp)
                    .size(26.dp)
            )
        },
        label = {
            Text(
                text = "To Do List",
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(start = 4.dp)
            )
        },
        selected = false,
        onClick = {
            onSelectScreen("To Do List")  // Notify the parent when this item is selected
        }
    )

    // Navigation item for "Inspirational Quote"
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 32.dp)
                    .size(26.dp)
            )
        },
        label = {
            Text(
                text = "Inspirational Quote",
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(start = 4.dp)
            )
        },
        selected = false,
        onClick = {
            onSelectScreen("Inspirational Quote")  // Notify the parent when this item is selected
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onOpenDrawer: () -> Unit  // Lambda to open the drawer when the menu icon is clicked
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Blue.copy(0.4f)  // Set the background color for the app bar
        ),
        navigationIcon = {
            // Icon to open the drawer
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp)
                    .size(30.dp)
                    .clickable {
                        onOpenDrawer()  // Open the drawer when the icon is clicked
                    }
            )
        },
        title = {
            Text(text = "Menu")  // Title in the app bar
        },
        actions = {
            // Icon for settings (currently without functionality)
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .size(30.dp)
                    .clickable {
                        // Placeholder for settings action
                    }
            )
        }
    )
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun TodoList(dataStoreManager: DataStoreManager) {
    // Coroutine scope for handling asynchronous tasks like loading/saving notes
    val scope = rememberCoroutineScope()

    // State variables for input text and notes list
    var text by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf(listOf<Note>()) }

    // LaunchedEffect to load the saved notes when the composable is first displayed
    LaunchedEffect(Unit) {
        scope.launch {
            dataStoreManager.getFromDataStore().collect { loadedNotes ->
                notes = loadedNotes  // Update the notes list with saved notes from DataStore
            }
        }
    }

    // Main UI for displaying the to-do list and input area
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(bottom = 128.dp, top = 32.dp)
        ) {
            // Display each note in the list with a checkbox
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
                        // Toggle the checked state of the note and update the notes list
                        onCheckedChange = {
                            val updatedNote = currentNote.copy(checked = !currentNote.checked)
                            notes = notes.map { note ->
                                if (note == currentNote) updatedNote else note
                            }
                            // Save the updated list back to DataStore
                            scope.launch {
                                dataStoreManager.saveTodDataStore(notes)
                            }
                        },
                        checked = currentNote.checked
                    )
                }
                Divider()
            }
        }

        // Input area to add new notes
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Text input for new note
            OutlinedTextField(
                value = text,
                onValueChange = { newText ->
                    text = newText  // Update the text field state
                },
                modifier = Modifier
                    .weight(1f)
            )
            // Button to add new note to the list
            Button(onClick = {
                if (text.isNotBlank()) {
                    val newNote = Note(text, false)
                    notes = notes + newNote  // Add the new note to the list
                    text = ""  // Clear the text field
                    scope.launch {
                        dataStoreManager.saveTodDataStore(notes)  // Save the updated list
                    }
                }
            }) {
                Text(text = "add")
            }
            // Button to remove checked notes
            Button(onClick = {
                val newNotes = notes.filter { !it.checked }  // Filter out checked notes
                notes = newNotes
                scope.launch {
                    dataStoreManager.saveTodDataStore(notes)  // Save the updated list
                }
            }) {
                Text(text = "remove")
            }
        }
    }
}

// TODO: Placeholder for a future feature that will display an inspirational quote
@Composable
fun InspirationalQuote() {

}
