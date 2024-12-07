using System.Diagnostics;
using System.Text.Json;

namespace MusicPlayer;
public static class AppState
{
	public static NavigationManager? NavigationManager { get; set; } = null;
	public static Language.Code PreferredLanguage = Language.Code.EN_US;
	public static string MusicDirectory = "";
	public static List<NavigationSubscription> NavigationSubscriptions = new();
	public static void SubscribeToNavigation(RouteKey routeKey, Action onNavigate)
	{
		Debug.WriteLine($"Subscribing to navigation for {routeKey}");
		NavigationSubscription subscription = new NavigationSubscription
		{
			RouteKey = routeKey,
			OnNavigate = onNavigate
		};
		NavigationSubscriptions.Add(subscription);
	}
	public static SearchListItem? SelectedForDownload { get; set; } = null;
	public static void Save()
	{
		try
		{
			string appDataPath = FileSystem.AppDataDirectory;
			string filePath = Path.Combine(appDataPath, "appstate.json");

			Debug.WriteLine($"Saving app state to: {filePath}");
			Debug.WriteLine($"AppState: {PreferredLanguage}, {MusicDirectory}");

			var json = JsonSerializer.Serialize(new AppStateData
			{
				PreferredLanguage = PreferredLanguage,
				MusicDirectory = MusicDirectory
			});

			File.WriteAllText(filePath, json);
		}
		catch (Exception ex)
		{
			Debug.WriteLine($"Failed to save app state: {ex.Message}");

		}

	}
	public static void Load()
	{
		try
		{
			string appDataPath = FileSystem.AppDataDirectory;
			string filePath = Path.Combine(appDataPath, "appstate.json");

			Debug.WriteLine($"Loading app state from: {filePath}");

			if (File.Exists(filePath))
			{
				var json = File.ReadAllText(filePath);
				var data = JsonSerializer.Deserialize<AppStateData>(json);

				PreferredLanguage = data.PreferredLanguage;
				MusicDirectory = data.MusicDirectory;
				Debug.WriteLine($"Loaded app state: {PreferredLanguage}, {MusicDirectory}");
			}
			else
			{
				Debug.WriteLine("No app state file found");
			}
			Language.Change(PreferredLanguage);
		}
		catch (Exception ex)
		{
			Debug.WriteLine($"Failed to load app state: {ex.Message}");

		}
	}
}

public struct AppStateData
{
	public Language.Code PreferredLanguage { get; set; }
	public string MusicDirectory { get; set; }
}