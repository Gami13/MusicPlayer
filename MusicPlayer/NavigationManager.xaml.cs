using Material.Components.Maui;
namespace MusicPlayer;

public struct Route()
{
	public required ContentView View { get; set; }
	public required string Icon { get; set; }
	public required string IconFocused { get; set; }
	public LocalizationKey LocalizationKey { get; set; }
};

public partial class NavigationManager : ContentPage
{
	private static int lastFocusedViewIdx = 0;
	private Stack<int> navigationStack = new();

	public NavigationManager(ContentView initialContent)
	{
		InitializeComponent();
		LocalizationResourceManager.Instance.PropertyChanged += (_, _) => { };
		NavigationDestinationContent.Content = initialContent;
		foreach (var route in Constants.Routes)
		{
			var item = new NavigationBarItem
			{
				IconData = route.Icon,
			};
			Language.SetLocalizedBinding(item, NavigationBarItem.TextProperty, route.LocalizationKey);
			item.Clicked += (_, _) =>
			{
				var currentContent = NavigationDestinationContent.Content as ContentView;
				var temp = Array.IndexOf(Constants.Routes, route);
				if (temp != lastFocusedViewIdx)
				{
					navigationStack.Push(lastFocusedViewIdx);
					NavigationDrawer.Items[lastFocusedViewIdx].IconData = Constants.Routes[lastFocusedViewIdx].Icon;
					lastFocusedViewIdx = temp;
				}
				NavigationDestinationContent.Content = route.View.Content;
				item.IconData = route.IconFocused;
			};
			NavigationDrawer.Items.Add(item);
		}
		NavigationDrawer.Items[0].IconData = Constants.Routes[0].IconFocused;
		AppState.Load();

	}
	protected override bool OnBackButtonPressed()
	{
		if (navigationStack.Count > 0)
		{
			var previousIndex = navigationStack.Pop();
			NavigationDestinationContent.Content = Constants.Routes[previousIndex].View.Content;
			NavigationDrawer.Items[lastFocusedViewIdx].IconData = Constants.Routes[lastFocusedViewIdx].Icon;
			NavigationDrawer.Items[lastFocusedViewIdx].IsActived = false;
			NavigationDrawer.Items[previousIndex].IconData = Constants.Routes[previousIndex].IconFocused;
			NavigationDrawer.Items[previousIndex].IsActived = true;

			lastFocusedViewIdx = previousIndex;
			return true;
		}
		return base.OnBackButtonPressed();
	}




}
