using System.Diagnostics;

using CommunityToolkit.Maui.Storage;
using CommunityToolkit.Mvvm.Messaging;
using CommunityToolkit.Mvvm.Messaging.Messages;

namespace MusicPlayer;
public class SelectedDirectoryChanged : ValueChangedMessage<string>
{
	public SelectedDirectoryChanged(string value) : base(value)
	{
	}
}
public partial class SettingsPage : ContentView
{
	public SettingsPage()
	{
		InitializeComponent();

		foreach (Language.Code lang in Language.Codes)
		{
			LanguageSelector.Items.Add(new Material.Components.Maui.MenuItem { Text = lang.Name() });
		}
		LanguageSelector.SelectedIndex = (int)Language.GetCurrent();

		WeakReferenceMessenger.Default.Register<SelectedDirectoryChanged>(this, (recipient, uri) =>
			{
				AppState.MusicDirectory = uri.Value.ToString();
				UpdateDirectoryLabel();
			});
		UpdateDirectoryLabel();


	}


	private void ChangeLanguage(object sender, EventArgs e)
	{
		var language = Language.Codes[LanguageSelector.SelectedIndex];
		Language.Change(language);

	}
	private async void SelectStorageLocation(object sender, EventArgs e)
	{

		if (OperatingSystem.IsWindows())
		{
			var result = await FolderPicker.Default.PickAsync();
			if (result.IsSuccessful && result.Folder?.Path != null)
			{

				AppState.MusicDirectory = result.Folder.Path.ToString() ?? string.Empty;
				Debug.WriteLine($"Selected directory: {AppState.MusicDirectory}");
				AppState.MusicDirectory = result.Folder.Path;
				UpdateDirectoryLabel();


			}

		}
#if ANDROID
		if (OperatingSystem.IsAndroid())
		{
			//Get the directory with correct authority

			var intent = new Intent(Intent.ActionOpenDocumentTree);
			intent.AddFlags(ActivityFlags.GrantPersistableUriPermission |
					  ActivityFlags.GrantReadUriPermission |
					  ActivityFlags.GrantWriteUriPermission);


			if (Platform.CurrentActivity != null)
			{
				Platform.CurrentActivity.StartActivityForResult(intent, Constants.MUSIC_DIRECTORY_REQUEST_CODE);
			}








		}

#endif

	}


	private void UpdateDirectoryLabel()
	{
		if (AppState.MusicDirectory == "")
		{
			Language.SetLocalizedBinding(DirectoryLabel, Label.TextProperty, TranslationKey.noDirectorySelected);
		}
		else
		{
			Language.SetLocalizedBinding(DirectoryLabel, Label.TextProperty, TranslationKey.selectedDirectory, AppState.MusicDirectory.Split("%3A").Last());
		}
	}



	private void Save(object sender, TouchEventArgs e)
	{
		AppState.Save();
	}

}