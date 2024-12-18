using System.ComponentModel;
using System.Globalization;
using MusicPlayer.Resources;

namespace MusicPlayer
{
	public class LocalizationResourceManager : INotifyPropertyChanged
	{
		public event PropertyChangedEventHandler? PropertyChanged;

		private static LocalizationResourceManager? _instance;
		public static LocalizationResourceManager Instance => _instance ??= new LocalizationResourceManager();

		public void SetCulture(CultureInfo culture)
		{
			AppResources.Culture = culture;
			PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(null));
		}

		public string this[string text] => AppResources.ResourceManager.GetString(text, AppResources.Culture) ?? string.Empty;
	}

}


