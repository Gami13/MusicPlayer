package com.gami13.musicplayer

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gami13.musicplayer.routes.HomeRoute
import com.gami13.musicplayer.routes.RouteKey
import com.gami13.musicplayer.routes.Routes
import com.gami13.musicplayer.routes.SearchRoute
import com.gami13.musicplayer.routes.SettingsRoute
import com.gami13.musicplayer.ui.theme.MusicPlayerTheme

@Preview(showBackground = true, uiMode = 0x21)
@Composable
fun App(modifier: Modifier = Modifier, fab: @Composable () -> Unit = {}, topAppBar: @Composable () -> Unit = {}) {


  val navController = rememberNavController()
  var selectedRoute: RouteKey by remember { mutableStateOf(RouteKey.Home) }

  MusicPlayerTheme {
    Scaffold(bottomBar = {
      NavigationBar {

        Routes.forEach { (routeKey, route) ->
          if (route.isVisible) NavigationBarItem(icon = {
            Icon(
              (if (selectedRoute == routeKey) route.iconSelected else route.icon)!!,
              contentDescription = stringResource(route.translationKey)
            )

          }, label = { Text(stringResource(route.translationKey)) },
            selected = selectedRoute == routeKey, onClick = {
              selectedRoute = routeKey
              MainActivity.FAB = {}
              MainActivity.TopAppBar = {}
              navController.navigate(routeKey)
            }, alwaysShowLabel = true
          )
        }
      }

    }, floatingActionButton = { fab() }, topBar = { topAppBar() }) {
      NavHost(
        navController = navController, startDestination = RouteKey.Home, Modifier.padding(it)
      ) {
        composable<RouteKey.Home> { HomeRoute() }
        composable<RouteKey.Search> { SearchRoute() }
        composable<RouteKey.Settings> { SettingsRoute() }


      }
    }
  }
}