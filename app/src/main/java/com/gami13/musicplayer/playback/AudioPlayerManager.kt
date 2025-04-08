package com.gami13.musicplayer.playback

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.gami13.musicplayer.MainActivity
import com.gami13.musicplayer.R
import com.gami13.musicplayer.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Manages audio playback and media controls for the app
 */
class AudioPlayerManager(private val context: Context) {

  companion object {
    private const val TAG = "AudioPlayerManager"
    private const val NOTIFICATION_ID = 1
    private const val CHANNEL_ID = "MusicPlayerChannel"
    private const val REQUEST_CODE = 101
  }

  private val mediaPlayer = MediaPlayer()
  private var currentSong: Song? = null
  private var mediaSession: MediaSessionCompat? = null
  private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
  private val handler = Handler(Looper.getMainLooper())
  private val progressUpdateInterval = 1000L // 1 second
  private var isAudioFocusGranted = false
  private var playbackDelayed = false
  private var resumeOnFocusGain = false
  private var playbackNowAuthorized = false

  // Audio focus handling
  private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
  private lateinit var audioFocusRequest: AudioFocusRequest

  // Progress tracking callback
  private var onProgressUpdate: ((Int, Int) -> Unit)? = null

  private val progressRunnable = object : Runnable {
    override fun run() {
      if (mediaPlayer.isPlaying) {
        val currentPosition = mediaPlayer.currentPosition / 1000
        val duration = mediaPlayer.duration / 1000
        onProgressUpdate?.invoke(currentPosition, duration)
        handler.postDelayed(this, progressUpdateInterval)
      }
    }
  }

  private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
    when (focusChange) {
      AudioManager.AUDIOFOCUS_GAIN -> {
        Log.d(TAG, "AUDIOFOCUS_GAIN")
        if (playbackDelayed || resumeOnFocusGain) {
          playbackDelayed = false
          resumeOnFocusGain = false
          mediaPlayer.start()
          updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
        }
        mediaPlayer.setVolume(1.0f, 1.0f)
      }

      AudioManager.AUDIOFOCUS_LOSS -> {
        Log.d(TAG, "AUDIOFOCUS_LOSS")
        // We've lost focus for an extended period, stop playback and release media player
        pausePlayback()
        resumeOnFocusGain = false
        playbackDelayed = false
      }

      AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
        Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT")
        // We've lost focus for a short time, pause playback but keep player ready
        pausePlayback(release = false)
        resumeOnFocusGain = true
        playbackDelayed = false
      }

      AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
        Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK")
        // We've lost focus for a short time but can duck (play at a reduced volume)
        mediaPlayer.setVolume(0.2f, 0.2f)
      }
    }
  }

  init {
    setupMediaPlayer()
    setupMediaSession()
    createNotificationChannel()
    initAudioFocusRequest()
  }

  private fun initAudioFocusRequest() {
    audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
      .setAudioAttributes(
        AudioAttributes.Builder()
          .setUsage(AudioAttributes.USAGE_MEDIA)
          .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
          .build()
      )
      .setAcceptsDelayedFocusGain(true)
      .setOnAudioFocusChangeListener(audioFocusChangeListener)
      .build()
  }
  private fun setupMediaPlayer() {
    mediaPlayer.setAudioAttributes(
      AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .build()
    )

    mediaPlayer.setOnCompletionListener {
      // Automatically move to the next song when current completes
      playNext()
    }

    mediaPlayer.setOnErrorListener { _, what, extra ->
      Log.e(TAG, "MediaPlayer Error: what=$what, extra=$extra")
      // Return false to trigger onCompletion
      false
    }

    mediaPlayer.setOnSeekCompleteListener {
      // Update UI immediately after seeking completes
      if (mediaPlayer.duration > 0) {
        onProgressUpdate?.invoke(
          mediaPlayer.currentPosition / 1000,
          mediaPlayer.duration / 1000
        )
      }
    }

    mediaPlayer.setOnPreparedListener {
      // Start playback if we have audio focus or request audio focus
      requestAudioFocus()
      // Report initial duration
      if (mediaPlayer.duration > 0) {
        onProgressUpdate?.invoke(
          mediaPlayer.currentPosition / 1000,
          mediaPlayer.duration / 1000
        )
      }
    }
  }

  private fun setupMediaSession() {
    // Create a MediaSessionCompat
    mediaSession = MediaSessionCompat(context, TAG).apply {
      setCallback(object : MediaSessionCompat.Callback() {
        override fun onPlay() {
          super.onPlay()
          resumePlayback()
        }

        override fun onPause() {
          super.onPause()
          pausePlayback()
        }

        override fun onSkipToNext() {
          super.onSkipToNext()
          playNext()
        }

        override fun onSkipToPrevious() {
          super.onSkipToPrevious()
          playPrevious()
        }

        override fun onStop() {
          super.onStop()
          stopPlayback()
        }

        override fun onSeekTo(pos: Long) {
          super.onSeekTo(pos)
          mediaPlayer.seekTo(pos.toInt())
        }
      })

      // Enable callbacks from media buttons and transport controls
      setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

      // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
      val stateBuilder = PlaybackStateCompat.Builder()
        .setActions(
          PlaybackStateCompat.ACTION_PLAY or
                  PlaybackStateCompat.ACTION_PLAY_PAUSE or
                  PlaybackStateCompat.ACTION_PAUSE or
                  PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                  PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                  PlaybackStateCompat.ACTION_STOP or
                  PlaybackStateCompat.ACTION_SEEK_TO
        )
      setPlaybackState(stateBuilder.build())

      // Set session active
      isActive = true
    }
  }

  private fun createNotificationChannel() {
    val name = context.getString(R.string.channel_name)
    val descriptionText = context.getString(R.string.channel_description)
    val importance = NotificationManager.IMPORTANCE_LOW
    val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
      description = descriptionText
    }
    // Register the channel with the system
    val notificationManager: NotificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
  }

  private fun requestAudioFocus(): Boolean {

    val result = audioManager.requestAudioFocus(audioFocusRequest)
    return when (result) {
      AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
        isAudioFocusGranted = true
        playbackNowAuthorized = true
        true
      }

      AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {
        playbackDelayed = true
        playbackNowAuthorized = false
        false
      }

      AudioManager.AUDIOFOCUS_REQUEST_FAILED -> {
        isAudioFocusGranted = false
        playbackNowAuthorized = false
        false
      }

      else -> false
    }
  }


  private fun abandonAudioFocus() {
    audioManager.abandonAudioFocusRequest(audioFocusRequest)
    isAudioFocusGranted = false
    playbackNowAuthorized = false
  }

  fun setOnProgressUpdateListener(listener: (Int, Int) -> Unit) {
    onProgressUpdate = listener
  }

  fun removeOnProgressUpdateListener() {
    onProgressUpdate = null
  }

  fun loadSong(song: Song) {
    try {
      // Reset MediaPlayer to prepare for new source
      mediaPlayer.reset()

      currentSong = song
      val uri = song.storagePath.toUri()

      Log.d(TAG, "Loading song: ${song.title} from URI: $uri")

      mediaPlayer.setDataSource(context, uri)
      mediaPlayer.prepareAsync()

      // Update metadata
      updateMetadata(song)

      // Show notification
      showNotification()

    } catch (e: Exception) {
      Log.e(TAG, "Error loading song", e)
    }
  }
  fun resumePlayback() {
    if (!mediaPlayer.isPlaying) {
      if (playbackNowAuthorized) {
        mediaPlayer.start()
        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
        showNotification()
        // Make sure we remove any existing callbacks to avoid duplicates
        handler.removeCallbacks(progressRunnable)
        handler.post(progressRunnable)
        // Report initial position and duration immediately
        if (mediaPlayer.duration > 0) {
          onProgressUpdate?.invoke(
            mediaPlayer.currentPosition / 1000,
            mediaPlayer.duration / 1000
          )
        }
      } else {
        if (requestAudioFocus()) {
          mediaPlayer.start()
          updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
          showNotification()
          // Make sure we remove any existing callbacks to avoid duplicates
          handler.removeCallbacks(progressRunnable)
          handler.post(progressRunnable)
          // Report initial position and duration immediately
          if (mediaPlayer.duration > 0) {
            onProgressUpdate?.invoke(
              mediaPlayer.currentPosition / 1000,
              mediaPlayer.duration / 1000
            )
          }
        }
      }
    } else {
      // If already playing, make sure progress updates are running
      handler.removeCallbacks(progressRunnable)
      handler.post(progressRunnable)
    }
  }

  fun pausePlayback(release: Boolean = false) {
    if (mediaPlayer.isPlaying) {
      mediaPlayer.pause()
      handler.removeCallbacks(progressRunnable)
      updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
      showNotification()
    }

    if (release) {
      abandonAudioFocus()
    }
  }

  fun playNext() {
    val queue = MainActivity.musicPlayerState.queue
    var nextIndex = MainActivity.musicPlayerState.currentSongIdx + 1

    if (nextIndex >= queue.size) {
      nextIndex = 0 // Loop to beginning
    }

    if (queue.isNotEmpty() && nextIndex >= 0 && nextIndex < queue.size) {
      MainActivity.musicPlayerState.currentSongIdx = nextIndex
      loadSong(queue[nextIndex])
      resumePlayback()
    }
  }

  fun playPrevious() {
    val queue = MainActivity.musicPlayerState.queue
    var prevIndex = MainActivity.musicPlayerState.currentSongIdx - 1

    if (prevIndex < 0) {
      prevIndex = queue.size - 1 // Loop to end
    }

    if (queue.isNotEmpty() && prevIndex >= 0 && prevIndex < queue.size) {
      MainActivity.musicPlayerState.currentSongIdx = prevIndex
      loadSong(queue[prevIndex])
      resumePlayback()
    }
  }

  fun stopPlayback() {
    if (mediaPlayer.isPlaying) {
      mediaPlayer.stop()
    }
    mediaPlayer.reset()
    updatePlaybackState(PlaybackStateCompat.STATE_STOPPED)
    handler.removeCallbacks(progressRunnable)
    abandonAudioFocus()

    // Cancel notification
    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.cancel(NOTIFICATION_ID)
  }
  fun seekTo(seconds: Int) {
    // Convert seconds to milliseconds for MediaPlayer
    val milliseconds = seconds * 1000

    // Perform the seek operation
    mediaPlayer.seekTo(milliseconds)

    // Immediately update UI without waiting for onSeekCompleteListener
    onProgressUpdate?.invoke(seconds, mediaPlayer.duration / 1000)

    // Make sure progress updates are running
    if (mediaPlayer.isPlaying) {
      handler.removeCallbacks(progressRunnable)
      handler.post(progressRunnable)
    }

    Log.d(TAG, "Seeking to $seconds seconds")
  }

  fun isPlaying(): Boolean {
    return mediaPlayer.isPlaying
  }

  fun getCurrentPosition(): Int {
    return mediaPlayer.currentPosition / 1000 // Convert to seconds
  }

  fun getDuration(): Int {
    return mediaPlayer.duration / 1000 // Convert to seconds
  }

  private fun updateMetadata(song: Song) {
    val metadata = MediaMetadataCompat.Builder()
      .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
      .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artist)
      .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.album)
      .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, (song.duration * 1000).toLong())

    // Add album art if available
    if (song.cover.isNotEmpty()) {
      val bitmap = BitmapFactory.decodeByteArray(song.cover, 0, song.cover.size)
      metadata.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
    }

    mediaSession?.setMetadata(metadata.build())
  }

  private fun updatePlaybackState(state: Int) {
    val stateBuilder = PlaybackStateCompat.Builder()
      .setState(
        state,
        mediaPlayer.currentPosition.toLong(),
        1.0f
      )
      .setActions(
        PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_STOP or
                PlaybackStateCompat.ACTION_SEEK_TO
      )

    mediaSession?.setPlaybackState(stateBuilder.build())

    // Update app's state
    MainActivity.musicPlayerState.isPlaying = state == PlaybackStateCompat.STATE_PLAYING
  }

  private fun showNotification() {
    if (currentSong == null) return

    // Create a notification with playback controls
    val song = currentSong!!

    // Create an explicit intent for the activity
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
      context, REQUEST_CODE, intent,
      PendingIntent.FLAG_IMMUTABLE
    )

    // Get session token for notification
    val sessionToken = mediaSession?.sessionToken ?: return

    // Create notification actions
    val playPauseIcon = if (mediaPlayer.isPlaying) {
      R.drawable.testing // You need to add these drawable resources
    } else {
      R.drawable.testing
    }

    // Extract album art if available
    var albumArt: Bitmap? = null
    if (song.cover.isNotEmpty()) {
      albumArt = BitmapFactory.decodeByteArray(song.cover, 0, song.cover.size)
    }

    // Build the notification
    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setContentTitle(song.title)
      .setContentText(song.artist)
      .setSmallIcon(R.drawable.testing) // You need to add this drawable resource
      .setLargeIcon(albumArt)
      .setContentIntent(pendingIntent)
      .setStyle(
        androidx.media.app.NotificationCompat.MediaStyle()
          .setMediaSession(sessionToken)
          .setShowActionsInCompactView(0, 1, 2) // Show previous, play/pause, next in compact view
      )
      .addAction(R.drawable.testing, "Previous", createActionIntent(PlaybackActions.PREVIOUS))
      .addAction(playPauseIcon, "Play/Pause", createActionIntent(PlaybackActions.PLAY_PAUSE))
      .addAction(R.drawable.testing, "Next", createActionIntent(PlaybackActions.NEXT))
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setOnlyAlertOnce(true)
      .setOngoing(mediaPlayer.isPlaying)
      .build()

    try {
      val notificationManager = NotificationManagerCompat.from(context)
      notificationManager.notify(NOTIFICATION_ID, notification)
    } catch (e: SecurityException) {
      Log.e(TAG, "Permission denied for showing notification", e)
    }
  }

  private fun createActionIntent(action: String): PendingIntent {
    val intent = Intent(context, MediaActionReceiver::class.java).apply {
      this.action = action
    }
    return PendingIntent.getBroadcast(
      context,
      action.hashCode(),
      intent,
      PendingIntent.FLAG_IMMUTABLE
    )
  }

  fun release() {
    mediaPlayer.release()
    mediaSession?.release()
    handler.removeCallbacks(progressRunnable)
    abandonAudioFocus()
  }

  // Used to handle audio routing such as BT, earphones, etc.
  fun handleAudioRouteChange() {
    // Only react if we're playing
    if (mediaPlayer.isPlaying) {
      // Pause briefly and restart to switch audio route
      val wasPlaying = mediaPlayer.isPlaying
      val position = mediaPlayer.currentPosition

      mediaPlayer.pause()
      try {
        // Small delay to let the system switch audio routing
        Thread.sleep(100)
        if (wasPlaying) {
          mediaPlayer.start()
          mediaPlayer.seekTo(position)
        }
      } catch (e: Exception) {
        Log.e(TAG, "Error handling audio route change", e)
      }
    }
  }

  object PlaybackActions {
    const val PREVIOUS = "com.gami13.musicplayer.action.PREVIOUS"
    const val PLAY_PAUSE = "com.gami13.musicplayer.action.PLAY_PAUSE"
    const val NEXT = "com.gami13.musicplayer.action.NEXT"
  }
}
