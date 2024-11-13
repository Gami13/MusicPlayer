using Material.Components.Maui;

namespace MusicPlayer;
public partial class WhereYouAreInator : ContentPage
{
	public WhereYouAreInator(ContentPage initialPage)
	{
		InitializeComponent();
		ContentView.Content = initialPage.Content;
	}

	private void OnHomeButtonClicked(object sender, EventArgs e)
	{
		NavigateTo(new MainPage());
	}

	private void OnSettingsButtonClicked(object sender, EventArgs e)
	{
		NavigateTo(new SettingsPage());
	}
	private void NavigateClicked(object sender, EventArgs e)
	{
		string destination = (string)((NavigationBarItem)sender).BindingContext;




		switch (destination)
		{
			case "Home":
				NavigateTo(new MainPage());
				break;
			case "Settings":
				NavigateTo(new SettingsPage());
				break;
		}

	}

	private void NavigateTo(ContentPage page)
	{
		ContentView.Content = page.Content;
	}
}
