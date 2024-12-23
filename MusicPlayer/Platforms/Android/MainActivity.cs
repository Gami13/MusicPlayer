using System.Diagnostics;
using Android.App;
using Android.Content;
using Android.Content.PM;
using Android.OS;
using Android.Views;
using CommunityToolkit.Mvvm.Messaging;
using CommunityToolkit.Mvvm.Messaging.Messages;


namespace MusicPlayer;


[Activity(Theme = "@style/Maui.SplashTheme", MainLauncher = true, LaunchMode = LaunchMode.SingleTop, ConfigurationChanges = ConfigChanges.ScreenSize | ConfigChanges.Orientation | ConfigChanges.UiMode | ConfigChanges.ScreenLayout | ConfigChanges.SmallestScreenSize | ConfigChanges.Density)]
public class MainActivity : MauiAppCompatActivity
{
	protected override void OnCreate(Bundle? savedInstanceState)
	{
		base.OnCreate(savedInstanceState);



	}
	protected override void OnActivityResult(int requestCode, Result resultCode, Intent? data)
	{
		base.OnActivityResult(requestCode, resultCode, data);

		if (requestCode == Constants.MUSIC_DIRECTORY_REQUEST_CODE && resultCode == Result.Ok)
		{
			if (data?.Data == null)
			{
				return;
			}
			var uri = data.Data;
			if (ContentResolver != null)
			{
				ContentResolver.TakePersistableUriPermission(uri, data.Flags & (ActivityFlags.GrantReadUriPermission | ActivityFlags.GrantWriteUriPermission));
			}

			System.Diagnostics.Debug.WriteLine("Selected directory: " + uri);
			if (uri != null)
			{
				var uriString = uri?.ToString();
				if (uriString != null)
				{
					WeakReferenceMessenger.Default.Send(new SelectedDirectoryChanged(uriString));
				}
			}
		}



	}
}


public static class AndroidExtensions
{
	public static void Apply(this IWindowInsetsController? controller, Action<IWindowInsetsController> action)
	{
		if (controller != null)
		{
			action(controller);
		}
	}
}
