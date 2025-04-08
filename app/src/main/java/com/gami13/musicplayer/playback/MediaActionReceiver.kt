package com.gami13.musicplayer.playback

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gami13.musicplayer.MainActivity

/**
 * BroadcastReceiver to handle media control actions from the notification
 */
class MediaActionReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "MediaActionReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received action: ${intent.action}")
        
        when (intent.action) {
            AudioPlayerManager.PlaybackActions.PREVIOUS -> {
                MainActivity.audioPlayerManager?.playPrevious()
            }
            AudioPlayerManager.PlaybackActions.PLAY_PAUSE -> {
                val isPlaying = MainActivity.audioPlayerManager?.isPlaying() ?: false
                if (isPlaying) {
                    MainActivity.audioPlayerManager?.pausePlayback()
                } else {
                    MainActivity.audioPlayerManager?.resumePlayback()
                }
            }
            AudioPlayerManager.PlaybackActions.NEXT -> {
                MainActivity.audioPlayerManager?.playNext()
            }
        }
    }
}
