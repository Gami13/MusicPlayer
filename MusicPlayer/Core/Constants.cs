using MusicPlayer;

public enum RouteKey
{
    Home = 0,
    Player = 1,
    Search = 2,
    Settings = 3,
    Download = 4
}



public struct Route
{
    public required ContentView View { get; set; }
    public string? Icon { get; set; }
    public string? IconFocused { get; set; }
    public TranslationKey TranslationKey { get; set; }
    public required bool IsVisible { get; set; }
};

public struct Constants
{
    public const int MUSIC_DIRECTORY_REQUEST_CODE = 1;
    public const string DatabaseFile = "MusicPlayer.db3";
    public const bool IsDebug = true;
    public static string YoutubeSearchUrl(string q) => $"https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=10&key={Secrets.YoutubeApiKey}&q={q}&type=video";
    public static readonly Dictionary<RouteKey, Route> Routes = new()
    {
        [RouteKey.Home] = new Route
        {
            View = new MainPage(),
            Icon = IconPacks.IconKind.MaterialCommunity.HomeOutline,
            IconFocused = IconPacks.IconKind.MaterialCommunity.Home,
            TranslationKey = TranslationKey.home,
            IsVisible = true
        },
        [RouteKey.Player] = new Route
        {
            View = new PlayerPage(),
            Icon = IconPacks.IconKind.MaterialCommunity.PlayCircleOutline,
            IconFocused = IconPacks.IconKind.MaterialCommunity.PlayCircle,
            TranslationKey = TranslationKey.player,
            IsVisible = true
        },
        [RouteKey.Search] = new Route
        {
            View = new SearchPage(),
            Icon = IconPacks.IconKind.MaterialCommunity.DownloadOutline,
            IconFocused = IconPacks.IconKind.MaterialCommunity.Download,
            TranslationKey = TranslationKey.download,
            IsVisible = true
        },
        [RouteKey.Settings] = new Route
        {
            View = new SettingsPage(),
            Icon = IconPacks.IconKind.MaterialCommunity.CogOutline,
            IconFocused = IconPacks.IconKind.MaterialCommunity.Cog,
            TranslationKey = TranslationKey.settings,
            IsVisible = true
        },
        [RouteKey.Download] = new Route
        {
            View = new DownloadPage(),
            Icon = null,
            IconFocused = null,
            TranslationKey = TranslationKey.home,
            IsVisible = false
        }
    };
}