package dev.fg.buildyourownx.libs.lifecycle_visualizer.example

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.HikerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf
import androidx.core.net.toUri
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest


@Serializable
class SafeScreen (val age: Int, val friends: List<String>, val colleagues: List<String>, val name: String, val bestFriend: Friend)

//@Serializable
//class SafeScreen (val friends: List<String>)

@Serializable
class DialogScreen (val message: String)

@Serializable
class SafeScreenGraph

@Serializable
class SafeScreenGraphStart


@Serializable
@Parcelize
class Friend (val name: String, val bestFriend: Friend?) : Parcelable

val FriendType = object : NavType<Friend>(isNullableAllowed = false) {
    override val name: String
        get() = "friend_nav_type"
    override fun put(bundle: Bundle, key: String, value: Friend) {
        bundle.putParcelable(key, value)
    }

    override fun get(bundle: Bundle, key: String): Friend? {
        return bundle.getParcelable(key) as? Friend::class.java
    }

    override fun parseValue(value: String): Friend {
        // Decodes the string back into your object
        return Json.decodeFromString(value)
    }

    override fun serializeAsValue(value: Friend): String {
        // Encodes your object into a safe string representation for the route
        return Json.encodeToString(value)
    }
}
var isFirst = true

class LifecycleVisualizerActivity : ComponentActivity() {

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoroutineScope(Dispatchers.Main.immediate)
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "Screen0") {
                for (i in screenColors.indices) {
                    composable("Screen$i") {
                        GenericScreen(i, onNavigate = {
                            navController.navigate("Screen$it")
                        }, onNavigateSafe = {
//                            navController.navigate("hiker://screen/1".toUri())
//                            navController.navigate(SafeScreenGraphStart())
                            navController.navigate(SafeScreen(
                                friends = listOf("James", "Mark"),
                                age = 20,
                                colleagues = listOf("Best friend", "Mid guy"),
                                name = "John",
                                bestFriend = Friend("Jarob", Friend("Jessica", null))
                            )
                            )
                        })
                    }
                }
                navigation<SafeScreenGraph> (startDestination = SafeScreenGraphStart::class) {
                    composable<SafeScreenGraphStart> (
                        deepLinks = listOf(
                            NavDeepLink("hiker://screen/{number}")
                        )
                    ) {
                        LaunchedEffect(Unit) {
                            delay(5000)
                            navController.navigate(SafeScreen(
                                friends = listOf("James", "Mark"),
                                age = 20,
                                colleagues = listOf("Best friend", "Mid guy"),
                                name = "John",
                                bestFriend = Friend("Jarob", Friend("Jessica", null))
                                )
                            )
                        }
                        LaunchedEffect(Unit) {
                            navController.currentBackStackEntry?.savedStateHandle?.getStateFlow("TEST", "")!!.collectLatest {
                                println("VALUE FOR TEST: $it")
                            }
                        }
                    }
                }
                composable<SafeScreen> (
                    typeMap = mapOf(typeOf<Friend>() to FriendType)
                ) {
                    var isFirstTime by rememberSaveable { mutableStateOf(true) }
                    Text("Safe!")
                    fun goToDialog() {
                        navController.navigate(DialogScreen("Hello, world!"))
                    }
                    LaunchedEffect(Unit) {
                        if (!isFirstTime) return@LaunchedEffect
                        isFirstTime = true
                        goToDialog()
                    }
                    Button(
                        onClick = {
                            navController.previousBackStackEntry?.savedStateHandle?.set("TEST", "TEST${(0..100).random()}")
                        }
                    ) {
                        Text("SET RANDOM!")
                    }
                }
                dialog<DialogScreen> {
                    Text(it.toRoute<DialogScreen>().message)
                }
            }

            HikerView(navController)

//            LaunchedEffect(Unit) {
//                if (!isFirst) return@LaunchedEffect
//                isFirst = false
//                startActivity(Intent(Intent.ACTION_VIEW, "hiker://screen/1".toUri()))
//            }
        }
    }
}

val screenColors =
    listOf(
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Black,
        Color.Yellow,
        Color.Magenta,
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Black,
        Color.Yellow,
        Color.Magenta,
    )

@Composable
fun GenericScreen(index: Int, onNavigate: (Int) -> Unit, onNavigateSafe: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = screenColors[index]),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var count by rememberSaveable { mutableIntStateOf(0) }
        var isGoingUp = remember { false }
        Text("Screen $index!", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Button(
            onClick = {
                if (count == screenColors.lastIndex) {
                    isGoingUp = false
                } else if (count == 0) {
                    isGoingUp = true
                }

                if (isGoingUp) {
                    count++
                } else {
                    count--
                }
            }
        ) {
            Text("Go to Screen$count!", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Button(
            onClick = {
                onNavigate(count)
            }
        ) {
            Text("Go!", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Button(
            onClick = {
                onNavigateSafe()
            }
        ) {
            Text("Go to safe screen!", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}