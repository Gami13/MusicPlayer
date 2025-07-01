package com.gami13.musicplayer.routes


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.gami13.musicplayer.R
import com.gami13.musicplayer.routes.home.HomeRoute
import com.gami13.musicplayer.routes.search.SearchRoute
import com.gami13.musicplayer.routes.settings.SettingsRoute
import kotlinx.serialization.Serializable


sealed class RouteKey {
  @Serializable
  data object Home : RouteKey()

  @Serializable
  data object Player : RouteKey()

  @Serializable
  data object Search : RouteKey()

  @Serializable
  data object Settings : RouteKey()

  @Serializable
  data object Experiments : RouteKey()
}

data class Route(
  val view: @Composable () -> Unit,
  val icon: ImageVector?,
  val iconSelected: ImageVector?,
  val translationKey: Int,
  val isVisible: Boolean
)

val Routes: Map<RouteKey, Route> = mapOf(
  RouteKey.Home to Route(
    view = { HomeRoute() },
    icon = Icons.Outlined.Home,
    iconSelected = Icons.Rounded.Home,
    translationKey = R.string.route_home,
    isVisible = true
  ),

  RouteKey.Search to Route(
    view = { SearchRoute() },
    icon = Icons.Outlined.Download,
    iconSelected = Icons.Rounded.Download,
    translationKey = R.string.route_download,
    isVisible = true
  ),
  RouteKey.Settings to Route(
    view = { SettingsRoute() },
    icon = Icons.Outlined.Settings,
    iconSelected = Icons.Rounded.Settings,
    translationKey = R.string.route_settings,
    isVisible = true
  ),
//  RouteKey.Experiments to Route(
//    view = { ExperimentsRoute() },
//    icon = Icons.Outlined.Settings,
//    iconSelected = Icons.Rounded.Settings,
//    translationKey = R.string.experiments,
//    isVisible = true
//  )

)