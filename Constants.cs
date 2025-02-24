using System.Diagnostics;
using MusicPlayer;

public enum RouteKey {
	Home = 0,
	Player = 1,
	Search = 2,
	Settings = 3,
	Download = 4
}
public struct SongMenuItem {
	public string Title;
	public string Icon;
	public Action<Database.Song> OnClick;
	public MDColor Color;
}


public struct Route {
	public required ContentView View { get; set; }
	public string? Icon { get; set; }
	public string? IconFocused { get; set; }
	public TranslationKey TranslationKey { get; set; }
	public required bool IsVisible { get; set; }
};

public struct Constants {
	public const int MUSIC_DIRECTORY_REQUEST_CODE = 1;
	public const string DatabaseFile = "MusicPlayer.db3";
	public const bool IsDebug = true;
	public static string YoutubeSearchUrl(string q) => $"https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=10&key={Secrets.YoutubeApiKey}&q={q}&type=video";
	public static readonly Dictionary<RouteKey, Route> Routes = new() {
		[RouteKey.Home] = new Route {
			View = new MainPage(),
			Icon = IconPacks.IconKind.MaterialCommunity.HomeOutline,
			IconFocused = IconPacks.IconKind.MaterialCommunity.Home,
			TranslationKey = TranslationKey.home,
			IsVisible = true
		},
		[RouteKey.Player] = new Route {
			View = new PlayerPage(),
			Icon = IconPacks.IconKind.MaterialCommunity.PlayCircleOutline,
			IconFocused = IconPacks.IconKind.MaterialCommunity.PlayCircle,
			TranslationKey = TranslationKey.player,
			IsVisible = true
		},
		[RouteKey.Search] = new Route {
			View = new SearchPage(),
			Icon = IconPacks.IconKind.MaterialCommunity.DownloadOutline,
			IconFocused = IconPacks.IconKind.MaterialCommunity.Download,
			TranslationKey = TranslationKey.download,
			IsVisible = true
		},
		[RouteKey.Settings] = new Route {
			View = new SettingsPage(),
			Icon = IconPacks.IconKind.MaterialCommunity.CogOutline,
			IconFocused = IconPacks.IconKind.MaterialCommunity.Cog,
			TranslationKey = TranslationKey.settings,
			IsVisible = true
		},
		[RouteKey.Download] = new Route {
			View = new DownloadPage(),
			Icon = null,
			IconFocused = null,
			TranslationKey = TranslationKey.home,
			IsVisible = false
		}
	};
	// 	           	Play Next 
	//              Add to queue 
	//              Add to playlist 
	//              Other songs from {Artist} 
	//              Open in Youtube 
	//              Share(if i manage to get it to work) 
	//			  	Delete
	public static readonly List<SongMenuItem> songMenuItems = new() {
		new SongMenuItem {
			Title = "Play Next",
			Icon = IconPacks.IconKind.MaterialCommunity.PlaylistPlay,
			OnClick = (e) => Debug.WriteLine("Play Next" + e.Title),
			Color = MDColor.OnSurfaceColor
		},
		new SongMenuItem {
			Title = "Add to Queue",
			Icon = IconPacks.IconKind.MaterialCommunity.PlaylistMusic,
			OnClick = (e) => Debug.WriteLine("Add to Queue"),
			Color = MDColor.OnSurfaceColor

		},
		new SongMenuItem {
			Title = "Add to Playlist",
			Icon = IconPacks.IconKind.MaterialCommunity.PlaylistPlus,
			OnClick = (e) => Debug.WriteLine("Add to Playlist"),
			Color = MDColor.OnSurfaceColor
		},
		new SongMenuItem {
			Title = "Other songs from {0}",
			Icon = IconPacks.IconKind.MaterialCommunity.AccountMusic,
			OnClick = (e) => Debug.WriteLine("Other songs from {Artist}"),
			Color = MDColor.OnSurfaceColor
		},
		new SongMenuItem {
			Title = "Open in Youtube",
			Icon = IconPacks.IconKind.MaterialCommunity.Youtube,
			OnClick = (e) => {
   Browser.Default.OpenAsync(new Uri($"vnd.youtube://watch/{e.YoutubeId}"), BrowserLaunchMode.SystemPreferred);
			},
			Color = MDColor.OnSurfaceColor
		},
		new SongMenuItem {
			Title = "Share",
			Icon = IconPacks.IconKind.MaterialCommunity.ShareVariant,
			OnClick = (e) => Debug.WriteLine("Share"),
			Color = MDColor.OnSurfaceColor
		},
		new SongMenuItem {
			Title = "Delete",
			Icon = IconPacks.IconKind.MaterialCommunity.Delete,
			OnClick = (e) => Debug.WriteLine("Delete"),
			Color = MDColor.ErrorColor
		}


	};
}