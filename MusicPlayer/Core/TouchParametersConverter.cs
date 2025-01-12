using System.Globalization;
using CommunityToolkit.Maui.Core.Extensions;
using CommunityToolkit.Maui.Core;

namespace MusicPlayer;
public class TouchParametersConverter : IMultiValueConverter {
	public object Convert(object[] values, Type targetType, object parameter, CultureInfo culture) {
		return new TouchParameters {
			Song = values[0] as Database.Song,
			TouchPoints = values[1] as IList<Point>
		};
	}

	public object[] ConvertBack(object value, Type[] targetTypes, object parameter, CultureInfo culture) {
		throw new NotImplementedException();
	}
}

public class TouchParameters {
	public Database.Song Song { get; set; }
	public IList<Point> TouchPoints { get; set; }
}