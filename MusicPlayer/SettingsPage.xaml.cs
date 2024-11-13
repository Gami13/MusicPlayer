

namespace MusicPlayer;

public partial class SettingsPage : ContentPage
{





	public SettingsPage()
	{
		InitializeComponent();
		LocalizationResourceManager.Instance.PropertyChanged += (_, _) => { };
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

}