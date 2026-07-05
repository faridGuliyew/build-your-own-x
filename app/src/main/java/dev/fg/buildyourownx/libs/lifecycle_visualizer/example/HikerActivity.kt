package dev.fg.buildyourownx.libs.lifecycle_visualizer.example

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import dev.fg.buildyourownx.libs.lifecycle_visualizer.ui.HikerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

// --- Models ---
@Serializable
@Parcelize
data class Waypoint(val name: String, val elevation: Int) : Parcelable

@Serializable
@Parcelize
data class Trail(
    val id: String,
    val name: String,
    val difficulty: String,
    val lengthKm: Float,
    val waypoints: List<Waypoint>
) : Parcelable

val TrailNavType = object : NavType<Trail>(isNullableAllowed = false) {
    override val name: String get() = "trail"
    override fun put(bundle: Bundle, key: String, value: Trail) = bundle.putParcelable(key, value)
    override fun get(bundle: Bundle, key: String): Trail? = bundle.getParcelable(key) as? Trail
    override fun parseValue(value: String): Trail = Json.decodeFromString(Uri.decode(value))
    override fun serializeAsValue(value: Trail): String = Uri.encode(Json.encodeToString(value))
}

// --- Routes ---
@Serializable
object HomeRoute

@Serializable
data class ProfileRoute(val userId: String, val role: String)

@Serializable
data class TrailDetailsRoute(val trail: Trail)

@Serializable
data class AlertRoute(val title: String, val message: String)

@Serializable
data class DeepStackRoute(val level: Int)

@Serializable
data class DeepLinkRoute(val source: String)

// --- Theme Colors ---
private val BgColor = Color(0xFF121212)
private val SurfaceColor = Color(0xFF1E1E1E)
private val PrimaryGreen = Color(0xFF10B981) // Emerald Green
private val TextWhite = Color(0xFFF9FAFB)
private val TextGray = Color(0xFF9CA3AF)
private val WarningRed = Color(0xFFEF4444)

class LifecycleVisualizerActivity : ComponentActivity() {

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            
            Box(modifier = Modifier.fillMaxSize()) {
                NavHost(
                    navController = navController,
                    startDestination = HomeRoute,
                    modifier = Modifier.fillMaxSize().background(BgColor)
                ) {
                    // 1. Home Screen (Start Destination)
                    composable<HomeRoute> {
                        HomeScreen(
                            onProfileClick = {
                                navController.navigate(ProfileRoute(userId = "user_49281", role = "Premium Explorer"))
                            },
                            onTrailClick = {
                                val sampleTrail = Trail(
                                    id = "trail_yosemite_half_dome",
                                    name = "Half Dome Cables Route",
                                    difficulty = "Extreme",
                                    lengthKm = 27.4f,
                                    waypoints = listOf(
                                        Waypoint("Happy Isles", 1220),
                                        Waypoint("Vernal Fall", 1538),
                                        Waypoint("Nevada Fall", 1821),
                                        Waypoint("Sub Dome", 2400),
                                        Waypoint("Summit", 2694)
                                    )
                                )
                                navController.navigate(TrailDetailsRoute(trail = sampleTrail))
                            },
                            onDialogClick = {
                                navController.navigate(AlertRoute(title = "Danger", message = "Severe weather reported on the trail."))
                            },
                            onDeepLinkClick = {
                                // Simulate an external deep link intent that the NavController catches
                                val intent = Intent(Intent.ACTION_VIEW, "hiker://screen/campaign_2026".toUri())
                                startActivity(intent)
                            },
                            onSpamClick = {
                                // Spams navigation to trigger Burst / Double Click warning in Hiker logs
                                CoroutineScope(Dispatchers.Main).launch {
                                    navController.navigate(ProfileRoute(userId = "spam_test", role = "Tester"))
                                }
                            },
                            onDeepStackClick = {
                                navController.navigate(DeepStackRoute(level = 1))
                            }
                        )
                    }

                    // 2. Profile Screen (Simple primitive args)
                    composable<ProfileRoute> { backStackEntry ->
                        val route = backStackEntry.toRoute<ProfileRoute>()
                        GenericScreen(
                            title = "Explorer Profile",
                            subtitle = "ID: ${route.userId} | Role: ${route.role}",
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 3. Trail Details Screen (Type-Safe Complex Object)
                    composable<TrailDetailsRoute>(
                        typeMap = mapOf(typeOf<Trail>() to TrailNavType)
                    ) { backStackEntry ->
                        val trail = backStackEntry.toRoute<TrailDetailsRoute>().trail
                        GenericScreen(
                            title = trail.name,
                            subtitle = "${trail.lengthKm} km • ${trail.difficulty}\n${trail.waypoints.size} waypoints recorded.",
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 4. Alert Dialog Screen
                    dialog<AlertRoute> { backStackEntry ->
                        val alert = backStackEntry.toRoute<AlertRoute>()
                        AlertDialogContent(
                            title = alert.title,
                            message = alert.message,
                            onDismiss = { navController.popBackStack() }
                        )
                    }

                    // 5. Deep Stack Route (To test deep stack > 10 warning)
                    composable<DeepStackRoute> { backStackEntry ->
                        val level = backStackEntry.toRoute<DeepStackRoute>().level
                        GenericScreen(
                            title = "Deep Stack: Level $level",
                            subtitle = "Keep digging to trigger the >10 stack warning.",
                            onBack = { navController.popBackStack() }
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            ActionButton(
                                text = "Go Deeper (Level ${level + 1})",
                                color = PrimaryGreen,
                                onClick = { navController.navigate(DeepStackRoute(level = level + 1)) }
                            )
                        }
                    }

                    // 6. Deep Link Receiver
                    composable(
                        route = "hiker://screen/{source}",
                        deepLinks = listOf(navDeepLink { uriPattern = "hiker://screen/{source}" })
                    ) { backStackEntry ->
                        val source = backStackEntry.arguments?.getString("source") ?: "Unknown"
                        GenericScreen(
                            title = "Deep Link Success",
                            subtitle = "Arrived via external link.\nSource: $source",
                            onBack = { navController.popBackStack() }
                        )
                    }
                }

                // Attach Hiker Floating Window
                HikerView(navController)
            }
        }
    }
}

// --- UI Components ---

@Composable
fun HomeScreen(
    onProfileClick: () -> Unit,
    onTrailClick: () -> Unit,
    onDialogClick: () -> Unit,
    onDeepLinkClick: () -> Unit,
    onSpamClick: () -> Unit,
    onDeepStackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Hiker",
            color = PrimaryGreen,
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        Text(
            text = "Demo Application",
            color = TextGray,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Feature Action Cards
        ActionButton("String Route (Profile)", PrimaryGreen, onProfileClick)
        ActionButton("Type-Safe Object Route (Trail)", PrimaryGreen, onTrailClick)
        ActionButton("Dialog Navigation", PrimaryGreen, onDialogClick)
        ActionButton("Deep Link Simulation", PrimaryGreen, onDeepLinkClick)
        
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = SurfaceColor, thickness = 2.dp)
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Trigger Warnings",
            color = WarningRed.copy(alpha = 0.8f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        ActionButton("Spam Clicks (Burst Warning)", WarningRed, onSpamClick)
        ActionButton("Deep Stack (Depth Warning)", WarningRed, onDeepStackClick)
        
        Spacer(modifier = Modifier.height(80.dp)) // Padding for floating view
    }
}

@Composable
fun GenericScreen(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    extraContent: @Composable () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            color = TextWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = subtitle,
            color = TextGray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        
        extraContent()

        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "GO BACK",
            color = PrimaryGreen,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { onBack() }
                .padding(16.dp)
        )
    }
}

@Composable
fun ActionButton(text: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceColor)
            .clickable { onClick() }
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun AlertDialogContent(title: String, message: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceColor)
            .padding(24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = WarningRed, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(message, color = TextWhite, fontSize = 14.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryGreen.copy(alpha = 0.2f))
                    .clickable { onDismiss() }
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text("DISMISS", color = PrimaryGreen, fontWeight = FontWeight.Bold)
            }
        }
    }
}