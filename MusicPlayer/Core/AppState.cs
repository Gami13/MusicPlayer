using System.Diagnostics;
using System.Text.Json;

namespace MusicPlayer;

public struct PlayerState {
	Database.Song currentSong;
	int currentTime;
	bool isPlaying;

}

public struct PopUpItem {
	public string Title;
	public string Icon;
	public Action OnClick;
}
public static class AppState {
	public static HttpClient httpClient = new HttpClient();
	public static bool hasUpdatedMusic = true;
	public static NavigationManager? NavigationManager { get; set; } = null;
	public static Language.Code PreferredLanguage = Language.Code.EN_US;
	public static string MusicDirectory = "";
	public static PlayerState PlayerState = new();
	public static List<NavigationSubscription> NavigationSubscriptions = new();
	public static void SubscribeToNavigation(RouteKey routeKey, Action onNavigate) {
		Debug.WriteLine($"Subscribing to navigation for {routeKey}");
		NavigationSubscription subscription = new NavigationSubscription {
			RouteKey = routeKey,
			OnNavigate = onNavigate
		};
		NavigationSubscriptions.Add(subscription);
	}
	public static SearchListItem? SelectedForDownload { get; set; } = null;
	public static void Save() {
		try {
			string appDataPath = FileSystem.AppDataDirectory;
			string filePath = Path.Combine(appDataPath, "appstate.json");

			Debug.WriteLine($"Saving app state to: {filePath}");
			Debug.WriteLine($"AppState: {PreferredLanguage}, {MusicDirectory}");

			var json = JsonSerializer.Serialize(new AppStateData {
				PreferredLanguage = PreferredLanguage,
				MusicDirectory = MusicDirectory
			});

			File.WriteAllText(filePath, json);
		}
		catch (Exception ex) {
			Debug.WriteLine($"Failed to save app state: {ex.Message}");

		}

	}
	public static void Load() {
		try {
			string appDataPath = FileSystem.AppDataDirectory;
			string filePath = Path.Combine(appDataPath, "appstate.json");

			Debug.WriteLine($"Loading app state from: {filePath}");

			if (File.Exists(filePath)) {
				var json = File.ReadAllText(filePath);
				var data = JsonSerializer.Deserialize<AppStateData>(json);

				PreferredLanguage = data.PreferredLanguage;
				MusicDirectory = data.MusicDirectory;
				Debug.WriteLine($"Loaded app state: {PreferredLanguage}, {MusicDirectory}");
			}
			else {
				Debug.WriteLine("No app state file found");
			}
			Language.Change(PreferredLanguage);
		}
		catch (Exception ex) {
			Debug.WriteLine($"Failed to load app state: {ex.Message}");

		}
	}
	public static void ShowPopUp(Database.Song song, List<PopUpItem> items) {
		if (NavigationManager == null) {
			Debug.WriteLine("NavigationManager is null");
			return;
		}
		NavigationManager.AddPopUp(new Label { Text = "Hello" });
	}
}

public struct AppStateData {
	public Language.Code PreferredLanguage { get; set; }
	public string MusicDirectory { get; set; }
}