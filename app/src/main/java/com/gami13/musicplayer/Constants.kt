package com.gami13.musicplayer


object Constants {
  const val SETTINGS_NAME = "settings"
  const val YOUTUBE_SUGGESTION_BASE_URL =
    "https://clients1.google.com/complete/search?client=youtube&gs_ri=youtube&ds=yt&q="
  const val YOUTUBE_SEARCH_BASE_URL =
    "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=10&key=${Secrets.YOUTUBE_API_KEY}&type=video&q="

}