using CommunityToolkit.Maui.Storage;

namespace MusicPlayer;

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

		if (AppState.MusicDirectory == "")
		{
			Language.SetLocalizedBinding(DirectoryLabel, Label.TextProperty, LocalizationKey.noDirectorySelected);
		}
		else
		{
			Language.SetLocalizedBinding(DirectoryLabel, Label.TextProperty, LocalizationKey.selectedDirectory, AppState.MusicDirectory.Split("/").Last());
		}

	}


	private void ChangeLanguage(object sender, EventArgs e)
	{
		var language = Language.Codes[LanguageSelector.SelectedIndex];
		Language.Change(language);

	}
	private async void SelectStorageLocation(object sender, EventArgs e)
	{
		var result = await FolderPicker.Default.PickAsync();
		if (result.IsSuccessful)
		{
			AppState.MusicDirectory = result.Folder.Path;
			Language.SetLocalizedBinding(DirectoryLabel, Label.TextProperty, LocalizationKey.selectedDirectory, result.Folder.Path.Split("/").Last());
		}
	}

	private void Save(object sender, TouchEventArgs e)
	{
		AppState.Save();
	}

}