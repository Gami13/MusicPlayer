package com.gami13.musicplayer

import android.app.ComponentCaller
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.FloatingActionButton
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
import com.gami13.musicplayer.routes.DownloadRoute
import com.gami13.musicplayer.routes.HomeRoute
import com.gami13.musicplayer.routes.PlayerRoute
import com.gami13.musicplayer.routes.RouteKey
import com.gami13.musicplayer.routes.Routes
import com.gami13.musicplayer.routes.SearchRoute
import com.gami13.musicplayer.routes.SettingsRoute
import com.gami13.musicplayer.ui.theme.MusicPlayerTheme


class MainActivity : AppCompatActivity() {
  val openDocumentTree = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) {
    uri: Uri? ->
    // 'ActivityResultCallback': Handle the returned Uri
    Log.d("test", "onActivityResult: $uri")
  }

  companion object {
    lateinit var appContext: Context
    var FAB: @Composable () -> Unit by mutableStateOf({})

  }


  override fun onActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?,
    caller: ComponentCaller
  ) {
    super.onActivityResult(requestCode, resultCode, data, caller)
    if (requestCode == GET_STORAGE_CODE){
      Log.d("test", "onActivityResult: $data")
    }
  }
  override fun onCreate(savedInstanceState: Bundle?) {

    appContext = applicationContext
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      App(fab = FAB
      )
    }



  }
}


@Preview(showBackground = true, uiMode = 0x21)
@Composable
fun App(modifier: Modifier = Modifier, fab: @Composable () -> Unit = {}) {


  val navController = rememberNavController()
  var selectedRoute: RouteKey by remember { mutableStateOf(RouteKey.Home) }

  MusicPlayerTheme {
    Scaffold(
      bottomBar = {
        NavigationBar {

          Routes.forEach { (routeKey, route) ->
            if (route.isVisible)
              NavigationBarItem(
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
                  navController.navigate(routeKey)
                }, alwaysShowLabel = true
              )
          }
        }

      }, floatingActionButton = { fab() }) {
      NavHost(
        navController = navController,
        startDestination = RouteKey.Home,
        Modifier.padding(it)
      ) {
        composable<RouteKey.Home> { HomeRoute() }
        composable<RouteKey.Player> { PlayerRoute() }
        composable<RouteKey.Search> { SearchRoute() }
        composable<RouteKey.Settings> { SettingsRoute() }
        composable<RouteKey.Download> { DownloadRoute() }


      }
    }
  }
}


