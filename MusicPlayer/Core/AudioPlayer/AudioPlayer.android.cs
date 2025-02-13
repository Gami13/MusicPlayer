using Android.App;
using Android.Content;
using Android.OS;
using Android.Support.V4.Media.Session;
using AndroidX.Core.App;


namespace MusicPlayer;


public static partial class AudioPlayer {


	public static void Play() {
		var context = Android.App.Application.Context;
		var intent = new Intent(context, typeof(AudioPlayerService));
		if (Build.VERSION.SdkInt >= BuildVersionCodes.O) {
			context.StartForegroundService(intent);
		}
		else {
			context.StartService(intent);
		}
	}

	public static void Stop() {

	}

	public static void Pause() {

	}
}

[Service(ForegroundServiceType = Android.Content.PM.ForegroundService.TypeMediaPlayback)]
public class AudioPlayerService : Service {
	MediaSessionCompat mediaSession;

	public override void OnCreate() {
		base.OnCreate();
		mediaSession = new MediaSessionCompat(this, "MusicPlayerMediaSession");
	}

	public override IBinder OnBind(Intent intent) => null;

	public override StartCommandResult OnStartCommand(Intent intent, StartCommandFlags flags, int startId) {
		HandleIntent(intent);
		ShowNotification();
		return StartCommandResult.Sticky;
	}

	void HandleIntent(Intent intent) {
		if (intent?.Action == null)
			return;

		switch (intent.Action) {
			case "action_previous":
				// Handle previous action
				break;
			case "action_next":
				// Handle next action
				break;
			case "action_pause":
				// Handle pause/resume action
				break;
		}
	}

	void ShowNotification() {
		string channelId = "music_player_channel";
		if (Build.VERSION.SdkInt >= BuildVersionCodes.O) {
			var channel = new NotificationChannel(channelId, "Music Player", NotificationImportance.Default);
			var notificationManager = (NotificationManager)GetSystemService(NotificationService);
			notificationManager.CreateNotificationChannel(channel);
		}

		// Build the notification with MediaStyle and action buttons for previous, pause/resume, next.
		var builder = new AndroidX.Core.App.NotificationCompat.Builder(this, channelId)
			.SetContentTitle("Music Player")
			.SetContentText("Now playing: Polish Cow").SetSmallIcon(Resource.Drawable.home)
			.SetStyle(new AndroidX.Media.App.NotificationCompat.MediaStyle()
				.SetMediaSession(mediaSession.SessionToken)
				.SetShowActionsInCompactView(0, 1, 2))

			.AddAction(new NotificationCompat.Action(
				Resource.Drawable.skip_previous, "Previous", GetActionIntent("action_previous")))
			.AddAction(new NotificationCompat.Action(
				Resource.Drawable.play_circle, "Pause", GetActionIntent("action_pause")))
			.AddAction(new NotificationCompat.Action(
				Resource.Drawable.skip_next, "Next", GetActionIntent("action_next")));

		var notification = builder.Build();
		StartForeground(1, notification);
	}

	PendingIntent GetActionIntent(string action) {
		var intent = new Intent(this, typeof(AudioPlayerService));
		intent.SetAction(action);
		return PendingIntent.GetService(this, 0, intent, PendingIntentFlags.UpdateCurrent | PendingIntentFlags.Immutable);
	}
}
