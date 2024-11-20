using Material.Components.Maui;

namespace MusicPlayer;

public sealed record NavigationDestination
{
	private NavigationDestination(string value) => Value = value;

	public static NavigationDestination Home = new("Home");
	public static NavigationDestination Settings = new("Settings");
	public string Value { get; }
	public ContentPage ToPage()
	{
		return Value switch
		{
			"Home" => new MainPage(),
			"Settings" => new SettingsPage(),
			_ => throw new ArgumentException($"Unknown navigation destination: {Value}")
		};
	}
}
public partial class NavigationManager : ContentPage

{
	private static Dictionary<NavigationDestination, (string, string)> NavigationIcons = new Dictionary<NavigationDestination, (string, string)>{
		{NavigationDestination.Home, (IconPacks.IconKind.MaterialCommunity.HomeOutline, IconPacks.IconKind.MaterialCommunity.Home) },
		{
			NavigationDestination.Settings, (IconPacks.IconKind.MaterialCommunity.CogOutline, IconPacks.IconKind.MaterialCommunity.Cog)
		}
	};
	public NavigationManager(ContentPage initialPage)
	{
		InitializeComponent();
		ContentView.Content = initialPage.Content;
		FocusDestination(NavigationDestination.Home);
	}

	private void NavigateClicked(object sender, EventArgs e)
	{
		NavigationDestination destination = (NavigationDestination)((NavigationBarItem)sender).BindingContext;
		FocusDestination(destination);


		ContentView.Content = destination.ToPage().Content;
	}
	private void FocusDestination(NavigationDestination destination)
	{
		var children = NavigationDrawer.Items;
		foreach (var c in children)
		{
			NavigationDestination context = (NavigationDestination)c.BindingContext;

			if (destination == context)
			{
				c.IconData = NavigationIcons[context].Item2;
			}
			else
			{
				c.IconData = NavigationIcons[context].Item1;
			}
		}

	}
}
