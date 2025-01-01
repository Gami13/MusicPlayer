using System.Diagnostics;
using Material.Components.Maui;
namespace MusicPlayer;


public struct NavigationSubscription
{
	public RouteKey RouteKey;
	public Action OnNavigate;


}

public partial class NavigationManager : ContentPage
{
	private static RouteKey lastFocusedView = RouteKey.Home;
	private Stack<RouteKey> navigationStack = new();

	public NavigationManager(ContentView initialContent)
	{
		AppState.Load();

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
		AppState.NavigationManager = this;
	}

	private void Navigate(object? sender, EventArgs e)
	{
		if (sender is NavigationBarItem item && item.BindingContext is RouteKey routeKey && lastFocusedView != routeKey)
		{
			NavigateTo(routeKey);
		}

	}

	public void NavigateTo(RouteKey routeKey)
	{
		navigationStack.Push(lastFocusedView);
		navigate(routeKey);
	}



	private void executeSubscription(RouteKey route)
	{
		foreach (var subscription in AppState.NavigationSubscriptions)
		{
			if (subscription.RouteKey == route)
			{
				subscription.OnNavigate();
			}
		}

	}


	protected override bool OnBackButtonPressed()
	{
		if (navigationStack.Count > 0)
		{
			var previousRoute = navigationStack.Pop();
			navigate(previousRoute);
			return true;
		}
		return base.OnBackButtonPressed();
	}


	private void unfocusLast()
	{
		if (Constants.Routes[lastFocusedView].IsVisible)
		{
			NavigationDrawer.Items[(int)lastFocusedView].IconData = Constants.Routes[lastFocusedView].Icon;
			NavigationDrawer.Items[(int)lastFocusedView].IsActived = false;
		}
	}
	private void focus(RouteKey route)
	{
		NavigationDrawer.Items[(int)route].IconData = Constants.Routes[route].IconFocused;
		NavigationDrawer.Items[(int)route].IsActived = true;
		lastFocusedView = route;
	}

	private void navigate(RouteKey route)
	{
		NavigationDestinationContent.Content = Constants.Routes[route].View.Content;
		unfocusLast();
		focus(route);
		executeSubscription(route);

	}

}
