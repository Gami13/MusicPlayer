using System.Collections.ObjectModel;
using System.Diagnostics;
using System.Text.Json;
using System.Threading.Tasks;
using Material.Components.Maui;
using MauiContentButton;
using Microsoft.Maui.Controls.Shapes;

namespace MusicPlayer;

public struct PlayerState {
	Database.Song currentSong;
	int currentTime;
	bool isPlaying;

}


public static class AppState {
	public static HttpClient httpClient = new HttpClient();
	public static NavigationManager? NavigationManager { get; set; } = null;
	public static Language.Code PreferredLanguage = Language.Code.EN_US;
	public static string MusicDirectory = "";
	public static PlayerState PlayerState = new();

	public static ObservableCollection<Database.Song> SongsList { get; set; } = new ObservableCollection<Database.Song>();
	public static List<NavigationSubscription> NavigationSubscriptions = new();
	public static void SubscribeToNavigation(RouteKey routeKey, Action onNavigate) {
		Debug.WriteLine($"Subscribing to navigation for {routeKey}");
		NavigationSubscription subscription = new NavigationSubscription {
			RouteKey = routeKey,
			OnNavigate = onNavigate
		};
		bool isNew = true;
		foreach (var sub in NavigationSubscriptions) {
			if (sub.RouteKey == routeKey) {

				isNew = false;
				break;
			}
		}
		if (isNew) {
			NavigationSubscriptions.Add(subscription);
		}
	}
	public static SearchListItem? SelectedForDownload { get; set; } = null;
	public static void Save() {
		try {
			string appDataPath = FileSystem.AppDataDirectory;
			string filePath = System.IO.Path.Combine(appDataPath, "appstate.json");

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
			string filePath = System.IO.Path.Combine(appDataPath, "appstate.json");

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

	public static async void ShowSongMenu(Database.Song song) {
		if (NavigationManager == null) {
			Debug.WriteLine("NavigationManager is null");
			return;
		}

		var mainGrid = new Grid {
			RowDefinitions =
					{
				new RowDefinition { Height = GridLength.Star },
				new RowDefinition { Height = GridLength.Auto }
			}
		};

		var card = new Card {
			VerticalOptions = LayoutOptions.End,
			HorizontalOptions = LayoutOptions.Fill,
			Margin = new Thickness(0),
			Padding = new Thickness(0),
			MinimumHeightRequest = 128,
			BackgroundColor = MDColor.SurfaceContainerLowColor.GetColor(),

		}
		;
		Grid.SetRow(card, 1);

		var stackLayout = new StackLayout {
			Padding = 8,
			Spacing = 8,
			Orientation = StackOrientation.Vertical
		};

		var innerGrid = Components.CreateSongHeader(song);


		stackLayout.Children.Add(innerGrid);

		stackLayout.Children.Add(new BoxView {
			HeightRequest = 1,
			BackgroundColor = MDColor.OutlineVariantColor.GetColor(),
			Margin = new Thickness(8, 0)
		});

		foreach (var item in Constants.songMenuItems) {
			var contentButton = Components.CreateSongMenuListItem(item, song);
			stackLayout.Children.Add(contentButton);
		}
		stackLayout.Margin = new Thickness(0, 0, 0, 16);



		card.Content = stackLayout;
		mainGrid.Children.Add(card);


		await NavigationManager.AddPopUp(mainGrid);
	}
}

public struct AppStateData {
	public Language.Code PreferredLanguage { get; set; }
	public string MusicDirectory { get; set; }
}