using CommunityToolkit.Maui;
using Material.Components.Maui.Extensions;
using MaterialColorUtilities.Maui;
using MauiContentButton;

// using MauiContentButton;
using Microsoft.Extensions.Logging;

using Plugin.Maui.Audio;

namespace MusicPlayer;

public static class MauiProgram {
	public static MauiApp CreateMauiApp() {
		var builder = MauiApp.CreateBuilder();
		builder.UseMaterialComponents()
			.UseMauiApp<App>()
			.AddAudio().AddMauiContentButtonHandler()
			// .AddMauiContentButtonHandler()
			.UseMauiCommunityToolkit()
			.UseMaterialColors<CustomMaterialColorService>()
			.ConfigureFonts(fonts => {
				fonts.AddFont("OpenSans-Regular.ttf", "OpenSansRegular");
				fonts.AddFont("OpenSans-Semibold.ttf", "OpenSansSemibold");
			})
			.Logging.AddDebug();

		return builder.Build();
	}
}