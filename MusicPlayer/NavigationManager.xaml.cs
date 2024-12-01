using Material.Components.Maui;
namespace MusicPlayer;



public partial class NavigationManager : ContentPage
{
	private static RouteKey lastFocusedView = RouteKey.Home;
	private Stack<RouteKey> navigationStack = new();

	public NavigationManager(ContentView initialContent)
	{
		InitializeComponent();
		LocalizationResourceManager.Instance.PropertyChanged += (_, _) => { };
		NavigationDestinationContent.Content = initialContent;


		foreach (var route in Constants.Routes)
		{
			if (!route.Value.IsVisible)
			{
				continue;
			}
			var item = new NavigationBarItem
			{
				IconData = route.Value.Icon,
			};
			Language.SetLocalizedBinding(item, NavigationBarItem.TextProperty, route.Value.TranslationKey);
			item.BindingContext = route.Key;
			item.Clicked += Navigate;

			NavigationDrawer.Items.Add(item);
		}
		NavigationDrawer.Items[(int)RouteKey.Home].IconData = Constants.Routes[RouteKey.Home].IconFocused;
		AppState.Load();
		AppState.NavigationManager = this;
	}
	private void Navigate(object? sender, EventArgs e)
	{
		if (sender is NavigationBarItem item && item.BindingContext is RouteKey routeKey)
		{
			NavigateTo(routeKey);
		}

	}
	public void NavigateTo(RouteKey routeKey)
	{
		if (routeKey != lastFocusedView)
		{
			navigationStack.Push(lastFocusedView);
			NavigationDrawer.Items[(int)lastFocusedView].IconData = Constants.Routes[lastFocusedView].Icon;
			NavigationDrawer.Items[(int)lastFocusedView].IsActived = false;
			lastFocusedView = routeKey;
		}
		NavigationDestinationContent.Content = Constants.Routes[routeKey].View.Content;
		if (Constants.Routes[routeKey].IsVisible)
		{
			NavigationDrawer.Items[(int)routeKey].IconData = Constants.Routes[routeKey].IconFocused;
		}
	}
	protected override bool OnBackButtonPressed()
	{
		if (navigationStack.Count > 0)
		{
			var previousRoute = navigationStack.Pop();
			NavigationDestinationContent.Content = Constants.Routes[previousRoute].View.Content;
			if (Constants.Routes[lastFocusedView].IsVisible)
			{
				NavigationDrawer.Items[(int)lastFocusedView].IconData = Constants.Routes[lastFocusedView].Icon;
				NavigationDrawer.Items[(int)lastFocusedView].IsActived = false;
			}

			if (Constants.Routes[previousRoute].IsVisible)
			{
				NavigationDrawer.Items[(int)previousRoute].IconData = Constants.Routes[previousRoute].IconFocused;
				NavigationDrawer.Items[(int)previousRoute].IsActived = true;
			}


			lastFocusedView = previousRoute;
			return true;
		}
		return base.OnBackButtonPressed();
	}




}
