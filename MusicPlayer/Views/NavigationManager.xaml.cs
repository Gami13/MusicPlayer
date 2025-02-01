using System.Diagnostics;
using System.Threading.Tasks;
using CommunityToolkit.Maui.Extensions;
using Material.Components.Maui;
using MauiContentButton;

// using MauiContentButton;
using Microsoft.Maui.Controls.Shapes;
using Microsoft.Maui.Layouts;
namespace MusicPlayer;


public struct NavigationSubscription {
	public RouteKey RouteKey;
	public Action OnNavigate;
}

public partial class NavigationManager : ContentPage {
	private readonly Stack<RouteKey> navigationStack = new();

	public Command PressCommand { get; set; } = new Command(() => Debug.WriteLine("Pressed "));
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
			Debug.WriteLine("Added " + route.Key);
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
			Debug.WriteLine("here");
			return;
		}
		var current = navigationStack.Pop();
		var previous = navigationStack.Count > 0 ? navigationStack.Peek() : current;
		Debug.WriteLine("here2");
		if (current == destination) {
			navigationStack.Push(current);
		}
		else if (previous != destination) {
			Debug.WriteLine("here3");
			navigationStack.Push(current);
			navigationStack.Push(destination);
		}
		//if previous == destination, do nothing
		Debug.WriteLine("here4");
		if (Constants.Routes[current].IsVisible) {

			SetIcon(current, false);
		}
		if (Constants.Routes[destination].IsVisible) {

			SetIcon(destination, true);
		}

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
	public async Task AddPopUp(View view) {

		PopUpLayer.InputTransparent = false;
		PopUpLayer.BackgroundColor = Color.FromRgba(0, 0, 0, 0);

		view.TranslationY = PopUpLayer.Height;
		PopUpLayer.Content = view;

		await Task.WhenAll(view.TranslateTo(0, 0, 250, Easing.CubicInOut), PopUpLayer.BackgroundColorTo(Color.FromRgba(0, 0, 0, 0.5), 125));

	}
	public void RemovePopUp() {
		PopUpLayer.Content = null;
		PopUpLayer.InputTransparent = true;
		PopUpLayer.BackgroundColor = Colors.Transparent;
	}
	private void PopUpLayerTap(object sender, TappedEventArgs e) {
		Debug.WriteLine("Tapped on PopUpLayer");



		RemovePopUp();

	}




}
