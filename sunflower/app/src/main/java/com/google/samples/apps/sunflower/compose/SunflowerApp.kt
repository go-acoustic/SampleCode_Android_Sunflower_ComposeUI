/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower.compose

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ShareCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.acoustic.connect.android.connectmod.composeui.ConnectComposeUI.Companion.ConnectWrapper
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.compose.gallery.GalleryScreen
import com.google.samples.apps.sunflower.compose.home.HomeScreen
import com.google.samples.apps.sunflower.compose.plantdetail.PlantDetailsScreen

@Composable
fun SunflowerApp() {
    val navController = rememberNavController()

    // Connect SDK Wrapper init with app key and post message URL
    ConnectWrapper(
        navController,
        appKey = "fa47722a7fef4bcd8677fd8d6d113a0d",
        postMessageURL = "https://lib-us-2.brilliantcollector.com/collector/collectorPost"
    ) {
        SunFlowerNavHost(
            navController = navController
        )
    }
}

@Composable
fun SunFlowerNavHost(
    navController: NavHostController
) {
    val activity = (LocalContext.current as Activity)
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                onPlantClick = {
                    navController.navigate(
                        Screen.PlantDetail.createRoute(
                            plantId = it.plantId
                        )
                    )
                }
            )
        }
        composable(
            route = Screen.PlantDetail.route,
            arguments = Screen.PlantDetail.navArguments
        ) {
            PlantDetailsScreen(
                onBackClick = { navController.navigateUp() },
                onShareClick = {
                    createShareIntent(activity, it)
                },
                onGalleryClick = {
                    navController.navigate(
                        Screen.Gallery.createRoute(
                            plantName = it.name
                        )
                    )
                }
            )
        }
        composable(
            route = Screen.Gallery.route,
            arguments = Screen.Gallery.navArguments
        ) {
            GalleryScreen(
                onPhotoClick = {
                    val uri = Uri.parse(it.user.attributionUrl)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    activity.startActivity(intent)
                },
                onUpClick = {
                    navController.navigateUp()
                })
        }
    }
}

// Helper function for calling a share functionality.
// Should be used when user presses a share button/menu item.
private fun createShareIntent(activity: Activity, plantName: String) {
    val shareText = activity.getString(R.string.share_text_plant, plantName)
    val shareIntent = ShareCompat.IntentBuilder(activity)
        .setText(shareText)
        .setType("text/plain")
        .createChooserIntent()
        .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    activity.startActivity(shareIntent)
}