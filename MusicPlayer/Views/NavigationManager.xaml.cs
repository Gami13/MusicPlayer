using System.Diagnostics;
using Material.Components.Maui;
namespace MusicPlayer;


public struct NavigationSubscription {
	public RouteKey RouteKey;
	public Action OnNavigate;
}

public partial class NavigationManager : ContentPage {
	private readonly Stack<RouteKey> navigationStack = new();

	public NavigationManager(ContentView initialContent) {
		AppState.Load();
		InitializeComponent();
		NavigationDestinationContent.Content = initialContent;

		foreach (var route in Constants.Routes) {
			if (!route.Value.IsVisible) continue;
			var item = new NavigationBarItem {
				IconData = route.Value.Icon
			};
			Language.SetLocalizedBinding(item, NavigationBarItem.TextProperty, route.Value.TranslationKey);
			item.BindingContext = route.Key;
			item.Clicked += Navigate;
			NavigationDrawer.Items.Add(item);
		}
		SetIcon(RouteKey.Home, true);
		navigationStack.Push(RouteKey.Home);
		AppState.NavigationManager = this;

	}

	private void Navigate(object? sender, EventArgs e) {
		if (sender is NavigationBarItem item && item.BindingContext is RouteKey routeKey) {
			NavigateTo(routeKey);
		}
	}

	protected override bool OnBackButtonPressed() {
		if (navigationStack.Count > 1) {
			SetIcon(navigationStack.Pop(), false);
			NavigateTo(navigationStack.Peek());
			return true;
		}
		return base.OnBackButtonPressed();
	}

	public void NavigateTo(RouteKey destination) {
		NavigationDestinationContent.Content = Constants.Routes[destination].View.Content;
		if (navigationStack.Count == 0) {
			navigationStack.Push(destination);
			SetIcon(destination, true);
			return;
		}
		var current = navigationStack.Pop();
		var previous = navigationStack.Count > 0 ? navigationStack.Peek() : current;
		if (current == destination) {
			navigationStack.Push(current);
		}
		else if (previous != destination) {
			navigationStack.Push(current);
			navigationStack.Push(destination);
		}
		//if previous == destination, do nothing

		SetIcon(current, false);
		SetIcon(destination, true);

		//Notify subscriptions
		foreach (var subscription in AppState.NavigationSubscriptions)
			if (subscription.RouteKey == destination) subscription.OnNavigate();
		Debug.WriteLine("Navigation stack: " + string.Join(", ", navigationStack));
	}

	private void SetIcon(RouteKey route, bool focused) {
		NavigationDrawer.Items[(int)route].IconData = focused
			? Constants.Routes[route].IconFocused
			: Constants.Routes[route].Icon;
		NavigationDrawer.Items[(int)route].IsActived = focused;
	}
}
