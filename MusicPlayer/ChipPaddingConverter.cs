using System.Globalization;
namespace MusicPlayer;
public class ChipPaddingConverter : IValueConverter
{
	// private int desiredLength = 15; //monospace
	private int desiredLength = 18; //default


	public object Convert(object? value, Type targetType, object? parameter, CultureInfo culture)
	{
		string val = value?.ToString() ?? string.Empty;
		if (val.Length > desiredLength)
		{
			return val.Substring(0, desiredLength - 3) + "...";
		}
		// while (val.Length < desiredLength)
		// {
		// 	val += " ";

		// }
		return val;

	}

	public object ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture)
	{
		throw new NotImplementedException();
	}
}