using Android.App;
using Android.Content.PM;
using Android.OS;
using Android.Views;
using Android.Graphics;
using Android.Runtime;
using AndroidX.Annotations;

namespace MusicPlayer;

[Activity(Theme = "@style/Maui.SplashTheme", MainLauncher = true, LaunchMode = LaunchMode.SingleTop, ConfigurationChanges = ConfigChanges.ScreenSize | ConfigChanges.Orientation | ConfigChanges.UiMode | ConfigChanges.ScreenLayout | ConfigChanges.SmallestScreenSize | ConfigChanges.Density)]
public class MainActivity : MauiAppCompatActivity
{
	protected override void OnCreate(Bundle? savedInstanceState)
	{
		base.OnCreate(savedInstanceState);

		if (Build.VERSION.SdkInt >= BuildVersionCodes.R) // API level 30
		{
			SetStatusBarColorForNew();
		}
		else if (Build.VERSION.SdkInt >= BuildVersionCodes.Lollipop)
		{
			SetStatusBarColorForLollipopAndAbove();
		}


	}
	[RequiresApi(Api = (int)BuildVersionCodes.R)]
	private void SetStatusBarColorForNew()
	{
		if (Window != null)
		{
			Window.SetDecorFitsSystemWindows(false);
		}
		Window.SetStatusBarColor(Android.Graphics.Color.Transparent);
		var windowInsetsController = Window.InsetsController;
		if (windowInsetsController != null)
		{
			windowInsetsController.Hide(WindowInsets.Type.StatusBars());
			windowInsetsController.SystemBarsBehavior = (int)WindowInsetsControllerBehavior.ShowTransientBarsBySwipe;
		}
	}

	[RequiresApi(Api = (int)BuildVersionCodes.Lollipop)]
	private void SetStatusBarColorForLollipopAndAbove()
	{
		Window.SetStatusBarColor(Android.Graphics.Color.Transparent);
		Window.DecorView.SystemUiVisibility = (StatusBarVisibility)(
			SystemUiFlags.LayoutStable | SystemUiFlags.LayoutFullscreen);
	}
}
