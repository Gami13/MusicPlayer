using System.Globalization;

namespace MusicPlayer;
public class TimeConverter : IValueConverter {

	public object? Convert(object? value, Type targetType, object? parameter, CultureInfo culture) {
		if (value is int time) {
			var minutes = time / 60;
			var seconds = time % 60;
			return $"{minutes}:{seconds:00}";
		}
		return "";
	}

	public object? ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture) {
		throw new NotSupportedException();
	}

}