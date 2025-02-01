namespace MusicPlayer {
	public enum MDColor {
		PrimaryColor,
		OnPrimaryColor,
		PrimaryContainerColor,
		OnPrimaryContainerColor,
		SecondaryColor,
		OnSecondaryColor,
		SecondaryContainerColor,
		OnSecondaryContainerColor,
		TertiaryColor,
		OnTertiaryColor,
		TertiaryContainerColor,
		OnTertiaryContainerColor,
		ErrorColor,
		OnErrorColor,
		ErrorContainerColor,
		OnErrorContainerColor,
		BackgroundColor,
		OnBackgroundColor,
		SurfaceColor,
		OnSurfaceColor,
		SurfaceVariantColor,
		OnSurfaceVariantColor,
		OutlineColor,
		ShadowColor,
		InverseSurfaceColor,
		InverseOnSurfaceColor,
		InversePrimaryColor,
		Surface1Color,
		Surface2Color,
		Surface3Color,
		Surface4Color,
		Surface5Color,
		SurfaceDimColor,
		SurfaceBrightColor,
		SurfaceContainerLowestColor,
		SurfaceContainerLowColor,
		SurfaceContainerColor,
		SurfaceContainerHighColor,
		SurfaceContainerHighestColor,
		OutlineVariantColor
	}

	public static class MDColorExtensions {
		public static string GetKeyString(this MDColor color) {
			return color.ToString();
		}

		public static bool TryGetMDColors(string keyString, out MDColor color) {
			return Enum.TryParse(keyString, out color);
		}

		public static MDColor GetMDColors(string keyString) {
			if (Enum.TryParse(keyString, out MDColor color)) {
				return color;
			}
			throw new ArgumentException($"Invalid key string: {keyString}", nameof(keyString));
		}
		public static Microsoft.Maui.Graphics.Color GetColor(this MDColor color) {
			if (Application.Current == null) {
				return Microsoft.Maui.Graphics.Color.FromRgba(0, 0, 0, 0);
			}
			return (Microsoft.Maui.Graphics.Color)Application.Current.Resources[color.GetKeyString()];
		}
	}
}