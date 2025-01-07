using System.Globalization;

namespace MusicPlayer;
public class Base64ToImageSource : IValueConverter {

	public object? Convert(object? value, Type targetType, object? parameter, CultureInfo culture) {
		var base64 = value as string ?? throw new ArgumentNullException(nameof(value), "Value cannot be null");
		return ImageSource.FromStream(
		() => new MemoryStream(System.Convert.FromBase64String(base64)));
	}

	public object? ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture) {
		throw new NotSupportedException();
	}

}