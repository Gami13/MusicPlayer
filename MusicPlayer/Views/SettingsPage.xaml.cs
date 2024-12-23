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

		await Utilities.UpdateStorageLocation();
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