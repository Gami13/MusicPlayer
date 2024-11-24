

using System.Diagnostics;
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
			Debug.WriteLine($"The folder was picked: Name - {result.Folder.Name}, Path - {result.Folder.Path}");
		}
		else
		{
			Debug.WriteLine($"The folder was not picked with error: {result.Exception.Message}");
		}

	}

}