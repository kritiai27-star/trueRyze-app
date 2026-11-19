package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.model.AccessRequest
import com.example.data.model.AiChatMessage
import com.example.data.model.Enquiry
import com.example.data.model.JobApplication
import com.example.data.model.PortalMessage
import com.example.data.model.SupportTicket
import com.example.data.model.UserProfile
import com.example.data.repository.AiAssistantRepository
import com.example.data.repository.TrueRyzeRepository
import com.example.firebase.AuthRepository
import com.example.firebase.AuthResult
import com.example.firebase.configureAppCheck
import com.example.ui.components.TrueRyzeBackdrop
import com.example.ui.screens.ConnectScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HowItWorksScreen
import com.example.ui.screens.LegalPrivacyScreen
import com.example.ui.screens.PortalAuthenticatedScreen
import com.example.ui.screens.PortalSignedOutScreen
import com.example.ui.screens.PricingScreen
import com.example.ui.screens.ServiceDetailScreen
import com.example.ui.screens.ServicesScreen
import com.example.ui.theme.CobaltContainer
import com.example.ui.theme.CobaltIce
import com.example.ui.theme.GraphiteSurface
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.TrueRyzeTheme
import com.example.ui.viewmodel.PortalViewModel
import com.example.ui.viewmodel.UiState
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

object TrueRyzeRoutes {
    const val HOME = "home"
    const val SERVICES = "services"
    const val SERVICE_DETAIL = "service_detail/{serviceId}"
    const val HOW_IT_WORKS = "how_it_works"
    const val PRICING = "pricing"
    const val CONNECT = "connect"
    const val PORTAL = "portal"
    const val LEGAL = "legal"

    fun serviceDetail(serviceId: String) = "service_detail/$serviceId"
}

private data class TopNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mandatory STEP 1: Initialize Firebase App Check with launch intent before Compose or ViewModels
        configureAppCheck(this, intent)

        enableEdgeToEdge()
        setContent {
            TrueRyzeTheme {
                TrueRyzeApp()
            }
        }
    }
}

@Composable
fun TrueRyzeApp() {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }
    val currentUser by authRepository.authStateFlow()
        .collectAsStateWithLifecycle(initialValue = authRepository.currentUser)

    val scope = rememberCoroutineScope()
    var isAuthBusy by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }

    // Attempt silent auto sign-in once on launch if an authorized Google account exists
    LaunchedEffect(Unit) {
        if (authRepository.currentUser == null) {
            authRepository.trySilentSignIn(context)
        }
    }

    val startGoogleSignIn: () -> Unit = {
        if (!isAuthBusy) {
            isAuthBusy = true
            authError = null
            scope.launch {
                when (val result = authRepository.signInWithGoogle(context)) {
                    is AuthResult.Success -> authError = null
                    is AuthResult.Cancelled -> authError = result.message
                    is AuthResult.Failure -> authError = result.message
                }
                isAuthBusy = false
            }
        }
    }

    val user = currentUser
    if (user == null) {
        TrueRyzeShell(
            currentUser = null,
            portalViewModel = null,
            isAuthBusy = isAuthBusy,
            authError = authError,
            onGoogleSignIn = startGoogleSignIn,
            onSignOut = {}
        )
    } else {
        // Key composition & ViewModel strictly by user.uid so account switch or sign-out resets listeners cleanly
        key(user.uid) {
            val portalViewModel: PortalViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                key = user.uid,
                factory = viewModelFactory {
                    initializer {
                        val app = checkNotNull(this[APPLICATION_KEY]) as Application
                        val databaseId = app.getString(R.string.firestore_database_id)
                        val db = FirebaseFirestore.getInstance(databaseId)
                        PortalViewModel(
                            repository = TrueRyzeRepository(db),
                            aiRepository = AiAssistantRepository(),
                            currentUserId = user.uid
                        )
                    }
                }
            )

            LaunchedEffect(user.uid) {
                portalViewModel.ensureProfile(
                    displayName = user.displayName.orEmpty(),
                    email = user.email.orEmpty(),
                    photoUrl = user.photoUrl?.toString()
                )
            }

            TrueRyzeShell(
                currentUser = user,
                portalViewModel = portalViewModel,
                isAuthBusy = isAuthBusy,
                authError = authError,
                onGoogleSignIn = startGoogleSignIn,
                onSignOut = {
                    scope.launch {
                        authRepository.signOut(context)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrueRyzeShell(
    currentUser: FirebaseUser?,
    portalViewModel: PortalViewModel?,
    isAuthBusy: Boolean,
    authError: String?,
    onGoogleSignIn: () -> Unit,
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: TrueRyzeRoutes.HOME

    var connectTab by rememberSaveable { mutableIntStateOf(0) }
    var preselectedPlan by rememberSaveable { mutableStateOf<String?>(null) }

    val topDestinations = listOf(
        TopNavItem(TrueRyzeRoutes.HOME, stringResource(R.string.nav_home), Icons.Rounded.Home, "nav_home"),
        TopNavItem(TrueRyzeRoutes.SERVICES, stringResource(R.string.nav_services), Icons.Rounded.Build, "nav_services"),
        TopNavItem(TrueRyzeRoutes.PRICING, stringResource(R.string.nav_pricing), Icons.Rounded.Sell, "nav_pricing"),
        TopNavItem(TrueRyzeRoutes.CONNECT, stringResource(R.string.nav_connect), Icons.Rounded.MailOutline, "nav_connect"),
        TopNavItem(TrueRyzeRoutes.PORTAL, stringResource(R.string.nav_portal), Icons.Rounded.AccountCircle, "nav_portal")
    )

    val navigateTop: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    TrueRyzeBackdrop {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useRail = maxWidth >= 600.dp

            Row(modifier = Modifier.fillMaxSize()) {
                if (useRail) {
                    NavigationRail(
                        containerColor = GraphiteSurface.copy(alpha = 0.96f),
                        contentColor = InkPrimary
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        topDestinations.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateTop(item.route) },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = CobaltIce,
                                    selectedTextColor = CobaltIce,
                                    indicatorColor = CobaltContainer,
                                    unselectedIconColor = InkMuted,
                                    unselectedTextColor = InkMuted
                                ),
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }

                Scaffold(
                    containerColor = Color.Transparent,
                    contentWindowInsets = WindowInsets.safeDrawing,
                    topBar = {
                        CenterAlignedTopAppBar(
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = GraphiteSurface.copy(alpha = 0.88f),
                                titleContentColor = InkPrimary
                            ),
                            title = {
                                Image(
                                    painter = painterResource(id = R.drawable.logo_small),
                                    contentDescription = stringResource(R.string.brand_title),
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.height(30.dp)
                                )
                            }
                        )
                    },
                    bottomBar = {
                        if (!useRail) {
                            NavigationBar(
                                containerColor = GraphiteSurface.copy(alpha = 0.96f),
                                contentColor = InkPrimary
                            ) {
                                topDestinations.forEach { item ->
                                    val selected = currentRoute == item.route
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = { navigateTop(item.route) },
                                        icon = { Icon(item.icon, contentDescription = item.label) },
                                        label = { Text(item.label, maxLines = 1) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = CobaltIce,
                                            selectedTextColor = CobaltIce,
                                            indicatorColor = CobaltContainer,
                                            unselectedIconColor = InkMuted,
                                            unselectedTextColor = InkMuted
                                        ),
                                        modifier = Modifier.testTag(item.testTag)
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        TrueRyzeNavHost(
                            navController = navController,
                            currentUser = currentUser,
                            portalViewModel = portalViewModel,
                            connectTab = connectTab,
                            preselectedPlan = preselectedPlan,
                            isAuthBusy = isAuthBusy,
                            authError = authError,
                            onGoogleSignIn = onGoogleSignIn,
                            onSignOut = onSignOut,
                            onOpenEnquiry = { plan ->
                                preselectedPlan = plan
                                connectTab = 0
                                navigateTop(TrueRyzeRoutes.CONNECT)
                            },
                            onOpenCareers = {
                                connectTab = 2
                                navigateTop(TrueRyzeRoutes.CONNECT)
                            },
                            onNavigateTop = navigateTop
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrueRyzeNavHost(
    navController: NavHostController,
    currentUser: FirebaseUser?,
    portalViewModel: PortalViewModel?,
    connectTab: Int,
    preselectedPlan: String?,
    isAuthBusy: Boolean,
    authError: String?,
    onGoogleSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onOpenEnquiry: (String?) -> Unit,
    onOpenCareers: () -> Unit,
    onNavigateTop: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = TrueRyzeRoutes.HOME
    ) {
        composable(TrueRyzeRoutes.HOME) {
            HomeScreen(
                onStartProject = { onOpenEnquiry(null) },
                onExploreServices = { onNavigateTop(TrueRyzeRoutes.SERVICES) },
                onOpenServiceDetail = { id ->
                    navController.navigate(TrueRyzeRoutes.serviceDetail(id))
                },
                onOpenHowItWorks = {
                    navController.navigate(TrueRyzeRoutes.HOW_IT_WORKS)
                },
                onOpenPortal = { onNavigateTop(TrueRyzeRoutes.PORTAL) },
                onOpenCareers = onOpenCareers,
                onOpenLegal = { navController.navigate(TrueRyzeRoutes.LEGAL) }
            )
        }

        composable(TrueRyzeRoutes.SERVICES) {
            ServicesScreen(
                onOpenServiceDetail = { id ->
                    navController.navigate(TrueRyzeRoutes.serviceDetail(id))
                },
                onOpenHowItWorks = {
                    navController.navigate(TrueRyzeRoutes.HOW_IT_WORKS)
                },
                onEnquire = { onOpenEnquiry(null) }
            )
        }

        composable(
            route = TrueRyzeRoutes.SERVICE_DETAIL,
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
        ) { backStack ->
            val serviceId = backStack.arguments?.getString("serviceId") ?: "website-development"
            ServiceDetailScreen(
                serviceId = serviceId,
                onBack = { navController.popBackStack() },
                onEnquire = { onOpenEnquiry(null) },
                onViewPlans = { onNavigateTop(TrueRyzeRoutes.PRICING) }
            )
        }

        composable(TrueRyzeRoutes.HOW_IT_WORKS) {
            HowItWorksScreen(
                onBack = { navController.popBackStack() },
                onStartProject = { onOpenEnquiry(null) }
            )
        }

        composable(TrueRyzeRoutes.PRICING) {
            PricingScreen(
                onSelectPlanForEnquiry = { plan -> onOpenEnquiry(plan) }
            )
        }

        composable(TrueRyzeRoutes.CONNECT) {
            val isSubmitting by (portalViewModel?.isSubmitting?.collectAsStateWithLifecycle()
                ?: remember { mutableStateOf(false) })
            val feedback by (portalViewModel?.feedbackMessage?.collectAsStateWithLifecycle()
                ?: remember { mutableStateOf<Pair<Boolean, String>?>(null) })

            ConnectScreen(
                initialTab = connectTab,
                preselectedPlan = preselectedPlan,
                isSignedIn = currentUser != null,
                authEmail = currentUser?.email,
                authName = currentUser?.displayName,
                isAuthBusy = isAuthBusy,
                isSubmitting = isSubmitting,
                feedbackMessage = feedback,
                onClearFeedback = { portalViewModel?.clearFeedback() },
                onSignInClick = onGoogleSignIn,
                onSubmitEnquiry = { enquiry, reset ->
                    portalViewModel?.submitEnquiry(enquiry, reset)
                },
                onSubmitApplication = { app, reset ->
                    portalViewModel?.submitApplication(app, reset)
                },
                onOpenPricing = { onNavigateTop(TrueRyzeRoutes.PRICING) },
                onOpenLegal = { navController.navigate(TrueRyzeRoutes.LEGAL) }
            )
        }

        composable(TrueRyzeRoutes.PORTAL) {
            if (currentUser == null || portalViewModel == null) {
                PortalSignedOutScreen(
                    isAuthBusy = isAuthBusy,
                    authError = authError,
                    onGoogleSignIn = onGoogleSignIn,
                    onOpenEnquiry = { onOpenEnquiry(null) }
                )
            } else {
                val profileState by portalViewModel.profile.collectAsStateWithLifecycle()
                val enquiriesState by portalViewModel.enquiries.collectAsStateWithLifecycle()
                val applicationsState by portalViewModel.applications.collectAsStateWithLifecycle()
                val accessState by portalViewModel.accessRequests.collectAsStateWithLifecycle()
                val ticketsState by portalViewModel.supportTickets.collectAsStateWithLifecycle()
                val messagesState by portalViewModel.portalMessages.collectAsStateWithLifecycle()
                val aiMessages by portalViewModel.aiMessages.collectAsStateWithLifecycle()
                val aiBusy by portalViewModel.aiBusy.collectAsStateWithLifecycle()
                val isSubmitting by portalViewModel.isSubmitting.collectAsStateWithLifecycle()
                val feedback by portalViewModel.feedbackMessage.collectAsStateWithLifecycle()

                PortalAuthenticatedScreen(
                    userEmail = currentUser.email.orEmpty(),
                    userName = currentUser.displayName.orEmpty(),
                    userPhotoUrl = currentUser.photoUrl?.toString(),
                    profileState = profileState,
                    enquiriesState = enquiriesState,
                    applicationsState = applicationsState,
                    accessRequestsState = accessState,
                    supportTicketsState = ticketsState,
                    messagesState = messagesState,
                    aiMessages = aiMessages,
                    aiBusy = aiBusy,
                    isSubmitting = isSubmitting,
                    feedbackMessage = feedback,
                    onClearFeedback = portalViewModel::clearFeedback,
                    onUpdateBusinessName = portalViewModel::updateBusinessName,
                    onSubmitAccessRequest = portalViewModel::submitAccessRequest,
                    onArchiveEnquiry = portalViewModel::archiveEnquiry,
                    onSubmitSupportTicket = portalViewModel::submitSupportTicket,
                    onCloseSupportTicket = portalViewModel::closeSupportTicket,
                    onSendPortalMessage = portalViewModel::sendPortalMessage,
                    onAskAi = portalViewModel::askAiAssistant,
                    onSignOut = onSignOut
                )
            }
        }

        composable(TrueRyzeRoutes.LEGAL) {
            LegalPrivacyScreen(onBack = { navController.popBackStack() })
        }
    }
}
