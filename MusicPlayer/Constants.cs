using MusicPlayer;

public struct Constants
{
    public const string DatabaseFile = "MusicPlayer.db3";
    public const bool IsDebug = true;
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
        View = new SettingsPage(),
        Icon = IconPacks.IconKind.MaterialCommunity.CogOutline,
        IconFocused = IconPacks.IconKind.MaterialCommunity.Cog,
        LocalizationKey = LocalizationKey.settings
    }
    ];
}