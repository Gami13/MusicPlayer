using CommunityToolkit.Maui;
using Material.Components.Maui.Extensions;
using MaterialColorUtilities.Maui;
using Microsoft.Extensions.Logging;

namespace MusicPlayer;

public static class MauiProgram
{
    public static MauiApp CreateMauiApp()
    {
        var builder = MauiApp.CreateBuilder();
        builder.UseMaterialComponents()
            .UseMauiApp<App>()
            .UseMauiCommunityToolkit()
            .UseMaterialColors<CustomMaterialColorService>()
            .ConfigureFonts(fonts =>
            {
                fonts.AddFont("OpenSans-Regular.ttf", "OpenSansRegular");
                fonts.AddFont("OpenSans-Semibold.ttf", "OpenSansSemibold");
            })
            .Logging.AddDebug();

        return builder.Build();
    }
}