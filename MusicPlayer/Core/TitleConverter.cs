using System.Globalization;

namespace MusicPlayer;
public class TitleConverter : IValueConverter {

	public object? Convert(object? value, Type targetType, object? parameter, CultureInfo culture) {
		if (value is string title) {
			if (title.Length > 30) {
				return title.Substring(0, 30) + "...";
			}
			return title;
		}
		return "";
	}

	public object? ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture) {
		throw new NotSupportedException();
	}

}