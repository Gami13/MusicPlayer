using System.Diagnostics;
using Material.Components.Maui;
using Microsoft.Maui.Layouts;
namespace MusicPlayer;


public struct NavigationSubscription {
	public RouteKey RouteKey;
	public Action OnNavigate;
}

public partial class NavigationManager : ContentPage {
	private readonly Stack<RouteKey> navigationStack = new();
	private Card? popUp;
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
		}
		SetIcon(RouteKey.Home, true);
		navigationStack.Push(RouteKey.Home);
		AppState.NavigationManager = this;

		// AddPopUp(new Label { Text = "Hello" });

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
	public void AddPopUp(View view) {
		PopUpLayer.InputTransparent = false;
		var grid = new Grid();

		// Add row definitions
		grid.RowDefinitions.Add(new RowDefinition { Height = GridLength.Star });
		grid.RowDefinitions.Add(new RowDefinition { Height = GridLength.Auto });

		// Create FlexLayout
		var flexLayout = new FlexLayout {
			Direction = FlexDirection.Column
		};

		// Create Card
		var card = new Card {
			VerticalOptions = LayoutOptions.End,
			HorizontalOptions = LayoutOptions.Fill,
			Margin = new Thickness(0),
			Padding = new Thickness(16),
			MinimumHeightRequest = 128,
			BackgroundColor = Application.Current?.Resources["SurfaceColor"] is Color surfaceColor ? surfaceColor : Color.FromRgba(255, 255, 255, 0.9),
			Elevation = Material.Components.Maui.Tokens.Elevation.Level5,
			Content = flexLayout
		};

		// Set Grid.Row for card
		Grid.SetRow(card, 1);

		// Add card to grid
		grid.Children.Add(card);

		//Replace PopUpLayer Content
		PopUpLayer.Content = grid;

		popUp = card;
		PopUpLayer.BackgroundColor = Color.FromRgba(0, 0, 0, 0.5);

	}
	public void RemovePopUp() {
		PopUpLayer.Content = null;
		PopUpLayer.InputTransparent = true;
		PopUpLayer.BackgroundColor = Colors.Transparent;
	}
	private void PopUpLayerTap(object sender, TappedEventArgs e) {
		Debug.WriteLine("Tapped on PopUpLayer");


		if (e.GetPosition(popUp)?.Y < 0) // Check if tap was above the card
		{
			RemovePopUp();
		}
	}




}
