package com.pokemon.explorer.state

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pokemon.explorer.data.db.AppDatabase
import com.pokemon.explorer.data.db.User
import com.pokemon.explorer.data.repository.AuthRepository
import com.pokemon.explorer.data.repository.AuthResult
import kotlinx.coroutines.launch

/** The four bottom-navigation destinations. */
enum class TabId(val label: String, val initialScreen: String) {
    Explore("Explore", Screen.HOME),
    Collection("Collection", Screen.COLLECTION),
    Compare("Compare", Screen.COMPARE),
    Guide("Guide", Screen.TYPE_GUIDE),
}

/** Screen keys used by [AppState.navigate]. */
object Screen {
    const val HOME = "home"
    const val SEARCH = "search"
    const val DETAIL = "detail"
    const val COMPARE = "compare"
    const val COLLECTION = "collection"
    const val TYPE_GUIDE = "typeGuide"
    const val SETTINGS = "settings"
    const val LOGIN = "login"
    const val REGISTER = "register"

    /** Screens that hide the bottom navigation bar, matching the web app. */
    val HIDDEN_NAV = setOf(SETTINGS, DETAIL, SEARCH, LOGIN, REGISTER)
}

/** One entry on a tab's back stack. */
data class NavFrame(
    val screen: String,
    val id: Int? = null,
    val idA: Int? = null,
    val filterType: String? = null,
)

enum class ThemeSetting(val label: String) {
    System("System"),
    Light("Light"),
    Dark("Dark"),
}

/**
 * Holds navigation stacks, the collection and display preferences.
 *
 * This is the Kotlin equivalent of the web app's `AppContext`: each bottom-nav tab
 * keeps its own back stack, so switching tabs preserves where you were and pressing
 * back inside a tab returns you through that tab's history.
 *
 * State lives in a [ViewModel], so it survives configuration changes (rotation,
 * dark-mode switches, resizing) — something the original browser state did not.
 */
class AppState(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val authRepository: AuthRepository by lazy {
        val database = AppDatabase.getDatabase(application)
        AuthRepository(database.userDao())
    }

    var currentUser by mutableStateOf<User?>(null)
        private set

    var authError by mutableStateOf<String?>(null)
        private set

    var isAuthChecked by mutableStateOf(false)
        private set

    var activeTab by mutableStateOf(TabId.Explore)
        private set

    var favourites by mutableStateOf(setOf<Int>())
        private set

    var caught by mutableStateOf(setOf<Int>())
        private set

    var theme by mutableStateOf(ThemeSetting.Dark)
        private set

    var searchHistoryEnabled by mutableStateOf(true)
        private set

    var recentSearches by mutableStateOf(listOf<String>())
        private set

    var resumeLastViewed by mutableStateOf(true)
        private set

    var notificationsEnabled by mutableStateOf(false)
        private set

    init {
        val savedUsername = prefs.getString("logged_in_user", null)
        if (savedUsername != null) {
            viewModelScope.launch {
                val database = AppDatabase.getDatabase(application)
                val user = database.userDao().getUserByUsername(savedUsername)
                currentUser = user
            }
        }
        isAuthChecked = true

        // Load settings
        theme = ThemeSetting.entries.find { it.name == prefs.getString("theme", ThemeSetting.Dark.name) } ?: ThemeSetting.Dark
        searchHistoryEnabled = prefs.getBoolean("search_history_enabled", true)
        resumeLastViewed = prefs.getBoolean("resume_last_viewed", true)
        notificationsEnabled = prefs.getBoolean("notifications_enabled", false)
        recentSearches = prefs.getString("recent_searches_csv", null)?.split(",")?.filter { it.isNotBlank() } ?: emptyList()

        // Load collection
        val savedFavourites = prefs.getStringSet("favourites", null)
        favourites = if (savedFavourites == null) {
            setOf(25, 1, 6)
        } else {
            savedFavourites.mapNotNull { it.toIntOrNull() }.toSet()
        }

        val savedCaught = prefs.getStringSet("caught", null)
        caught = if (savedCaught == null) {
            setOf(25, 1, 6, 7, 4, 5, 152, 249, 250, 384, 143, 59, 130, 131, 9, 3, 65, 68, 76, 94, 149)
        } else {
            savedCaught.mapNotNull { it.toIntOrNull() }.toSet()
        }
    }

    private val stacks = mutableStateMapOf(
        TabId.Explore to listOf(NavFrame(Screen.HOME)),
        TabId.Collection to listOf(NavFrame(Screen.COLLECTION)),
        TabId.Compare to listOf(NavFrame(Screen.COMPARE)),
        TabId.Guide to listOf(NavFrame(Screen.TYPE_GUIDE)),
    )

    val currentFrame: NavFrame
        get() = stacks.getValue(activeTab).last()

    val canGoBack: Boolean
        get() = stacks.getValue(activeTab).size > 1

    fun selectTab(tab: TabId) {
        activeTab = tab
    }

    /** Pushes a new screen onto the active tab's stack. */
    fun navigate(
        screen: String,
        id: Int? = null,
        idA: Int? = null,
        filterType: String? = null,
    ) {
        val frame = NavFrame(screen = screen, id = id, idA = idA, filterType = filterType)
        stacks[activeTab] = stacks.getValue(activeTab) + frame
    }

    /** Pops the active tab's stack. No-op at the root of a tab. */
    fun goBack() {
        val stack = stacks.getValue(activeTab)
        if (stack.size <= 1) return
        stacks[activeTab] = stack.dropLast(1)
    }

    fun toggleFavourite(id: Int) {
        favourites = favourites.toggle(id)
        prefs.edit().putStringSet("favourites", favourites.map { it.toString() }.toSet()).apply()
    }

    fun toggleCaught(id: Int) {
        caught = caught.toggle(id)
        prefs.edit().putStringSet("caught", caught.map { it.toString() }.toSet()).apply()
    }

    fun resetCollection() {
        favourites = emptySet()
        caught = emptySet()
        prefs.edit().remove("favourites").remove("caught").apply()
    }

    /**
     * Named `selectTheme` rather than `setTheme`: a `var theme` already generates a
     * `setTheme(ThemeSetting)` setter, and declaring another one is a JVM signature
     * clash. Matches [selectTab] for consistency.
     */
    fun selectTheme(value: ThemeSetting) {
        theme = value
        prefs.edit().putString("theme", value.name).apply()
    }

    fun updateSearchHistoryEnabled(enabled: Boolean) {
        searchHistoryEnabled = enabled
        prefs.edit().putBoolean("search_history_enabled", enabled).apply()
    }

    fun updateResumeLastViewed(enabled: Boolean) {
        resumeLastViewed = enabled
        prefs.edit().putBoolean("resume_last_viewed", enabled).apply()
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        notificationsEnabled = enabled
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    fun addSearchToHistory(query: String) {
        if (!searchHistoryEnabled || query.isBlank()) return
        val normalized = query.trim().lowercase()
        val newList = (listOf(normalized) + recentSearches.filter { it != normalized }).take(10)
        recentSearches = newList
        prefs.edit().putString("recent_searches_csv", newList.joinToString(",")).apply()
    }

    fun clearSearchHistory() {
        recentSearches = emptyList()
        prefs.edit().remove("recent_searches_csv").apply()
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            authError = null
            when (val result = authRepository.login(username, password)) {
                is AuthResult.Success -> {
                    currentUser = result.user
                    prefs.edit().putString("logged_in_user", result.user.username).apply()
                }
                is AuthResult.Error -> {
                    authError = result.message
                }
            }
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            authError = null
            when (val result = authRepository.register(username, password)) {
                is AuthResult.Success -> {
                    currentUser = result.user
                    prefs.edit().putString("logged_in_user", result.user.username).apply()
                }
                is AuthResult.Error -> {
                    authError = result.message
                }
            }
        }
    }

    fun logout() {
        currentUser = null
        prefs.edit().remove("logged_in_user").apply()
        activeTab = TabId.Explore
        stacks[TabId.Explore] = listOf(NavFrame(Screen.HOME))
        stacks[TabId.Collection] = listOf(NavFrame(Screen.COLLECTION))
        stacks[TabId.Compare] = listOf(NavFrame(Screen.COMPARE))
        stacks[TabId.Guide] = listOf(NavFrame(Screen.TYPE_GUIDE))
    }

    private fun Set<Int>.toggle(id: Int): Set<Int> =
        if (contains(id)) this - id else this + id
}
