using MusicPlayer;

public struct Constants
{
    public const string DatabaseFile = "MusicPlayer.db3";
    public const bool IsDebug = true;
    public static string YoutubeSearchUrl(string q) => $"https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=10&key={Secrets.YoutubeApiKey}&q={q}&type=video";
    public static readonly Route[] Routes =
    [
        new Route
    {

        View = new MainPage(),
        Icon = IconPacks.IconKind.MaterialCommunity.HomeOutline,
        IconFocused = IconPacks.IconKind.MaterialCommunity.Home,
        LocalizationKey = LocalizationKey.home
    },
    new Route
    {
        View = new DownloadPage(),
        Icon = IconPacks.IconKind.MaterialCommunity.DownloadOutline,
        IconFocused = IconPacks.IconKind.MaterialCommunity.Download,
        LocalizationKey = LocalizationKey.download
        },
    new Route
    {
        View = new SettingsPage(),
        Icon = IconPacks.IconKind.MaterialCommunity.CogOutline,
        IconFocused = IconPacks.IconKind.MaterialCommunity.Cog,
        LocalizationKey = LocalizationKey.settings
    },

    ];
}