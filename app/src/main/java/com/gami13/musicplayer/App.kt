@file:Suppress("t")

package com.gami13.musicplayer

import com.gami13.musicplayer.composables.MusicPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gami13.musicplayer.routes.RouteKey
import com.gami13.musicplayer.routes.Routes
import com.gami13.musicplayer.ui.theme.MusicPlayerTheme

@Preview(showBackground = true, uiMode = 0x21)
@Composable
fun App(
//  modifier: Modifier = Modifier,
  fab: @Composable () -> Unit = {},
  topAppBar: @Composable () -> Unit = {},
  snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

) {

  val navController = rememberNavController()
  var selectedRoute: RouteKey by remember { mutableStateOf(RouteKey.Home) }
  var navBarHeightPx by remember { mutableIntStateOf(0) }
  val density = LocalDensity.current

  MusicPlayerTheme {
    Box {
      Scaffold(bottomBar = {
        NavigationBar(
          modifier = Modifier.onGloballyPositioned { coordinates ->
            navBarHeightPx = coordinates.size.height
          }) {
          Routes.forEach { (routeKey, route) ->
            if (route.isVisible) NavigationBarItem(
              icon = {
              Icon(
                (if (selectedRoute == routeKey) route.iconSelected else route.icon)!!,
                contentDescription = stringResource(route.translationKey)
              )
            },
              label = { Text(stringResource(route.translationKey)) },
              selected = selectedRoute == routeKey,
              onClick = {
                selectedRoute = routeKey
                MainActivity.FAB = {}
                MainActivity.TopAppBar = {}
                navController.navigate(routeKey.toString())
              },
              alwaysShowLabel = true
            )
          }
        }
      }, floatingActionButton = { fab() }, topBar = { topAppBar() }, snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
      }) {
        NavHost(
          navController = navController,
          startDestination = RouteKey.Home.toString(),
          Modifier.padding(it)
        ) {
          Routes.forEach { (routeKey, route) ->
            composable(routeKey.toString()) {
              route.view()
            }
          }
        }
      }
      AnimatedVisibility(
        visible = MainActivity.musicPlayerState.isShown,
        enter = slideInHorizontally(initialOffsetX = { -it }),  // Slide in from left
        exit = slideOutHorizontally(targetOffsetX = { -it })    // Slide out to left
      ) {
        MusicPlayer(naturalOffset = with(density) { -navBarHeightPx.toDp() })
      }

    }
  }
}


