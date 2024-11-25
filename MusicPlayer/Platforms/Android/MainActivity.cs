using Android.App;
using Android.Content.PM;
using Android.OS;
using Android.Views;


namespace MusicPlayer;

[Activity(Theme = "@style/Maui.SplashTheme", MainLauncher = true, LaunchMode = LaunchMode.SingleTop, ConfigurationChanges = ConfigChanges.ScreenSize | ConfigChanges.Orientation | ConfigChanges.UiMode | ConfigChanges.ScreenLayout | ConfigChanges.SmallestScreenSize | ConfigChanges.Density)]
public class MainActivity : MauiAppCompatActivity
{
	protected override void OnCreate(Bundle? savedInstanceState)
	{
		base.OnCreate(savedInstanceState);

		// if (Build.VERSION.SdkInt >= BuildVersionCodes.R)
		// {
		// 	SetStatusBarColorApi30();
		// }
		// else if (Build.VERSION.SdkInt >= BuildVersionCodes.Lollipop)
		// {
		// 	SetStatusBarColorApi21();
		// }

	}

#pragma warning disable CA1416

	private void SetStatusBarColorApi30()
	{
		Window?.SetDecorFitsSystemWindows(false);
		Window?.SetStatusBarColor(Android.Graphics.Color.Transparent);
		Window?.InsetsController?.Apply(controller =>
		{
			controller.Hide(WindowInsets.Type.StatusBars());
			controller.SystemBarsBehavior = (int)WindowInsetsControllerBehavior.ShowTransientBarsBySwipe;
		});
	}

#pragma warning disable CS0618 // Type or member is obsolete

	private void SetStatusBarColorApi21()
	{
		Window?.SetStatusBarColor(Android.Graphics.Color.Transparent);
		if (Window?.DecorView != null)
		{
			Window.DecorView.SystemUiVisibility = (StatusBarVisibility)(
				SystemUiFlags.LayoutStable | SystemUiFlags.LayoutFullscreen);
		}
	}
#pragma warning restore CS0618 // Type or member is obsolete

#pragma warning restore CA1416
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
