using System.Diagnostics;
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
	private Card? popUpCard;
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

		var mainGrid = new Grid {
			RowDefinitions =
					{
				new RowDefinition { Height = GridLength.Star },
				new RowDefinition { Height = GridLength.Auto }
			}
		};

		var card = new Card {
			VerticalOptions = LayoutOptions.End,
			HorizontalOptions = LayoutOptions.Fill,
			Margin = new Thickness(0),
			Padding = new Thickness(0),
			MinimumHeightRequest = 128,
			BackgroundColor = (Color)Application.Current.Resources["SurfaceContainerLowColor"]

		}
		;
		Grid.SetRow(card, 1);

		var stackLayout = new StackLayout {
			Padding = 8,
			Spacing = 8,
			Orientation = StackOrientation.Vertical
		};

		var innerGrid = new Grid {
			HorizontalOptions = LayoutOptions.Fill,
			Padding = 0,
			HeightRequest = 60,
			RowSpacing = 6,
			ColumnSpacing = 8,
			ColumnDefinitions = {
				new ColumnDefinition { Width = 60 },
				new ColumnDefinition { Width = GridLength.Star },
				new ColumnDefinition { Width = 64 }
			},
			RowDefinitions = { new RowDefinition { Height = GridLength.Star } }
		};

		var imageBorder = new Border {
			HeightRequest = 60,
			WidthRequest = 60,
			Stroke = Colors.Transparent,
			StrokeShape = new RoundRectangle { CornerRadius = 8 },
			Margin = 0,
			StrokeThickness = 0,
			Content = new Image {
				Source = "https://i1.sndcdn.com/artworks-mnk5VVgL4ZTg-0-t500x500.jpg",
				Aspect = Aspect.AspectFill
			}
		};
		Grid.SetColumn(imageBorder, 0);
		innerGrid.Children.Add(imageBorder);

		var textStack = new StackLayout {
			VerticalOptions = LayoutOptions.Center,
			Spacing = 0,
			Scale = 0.9,
			Orientation = StackOrientation.Vertical
		};

		var titleLabel = new Label {
			Text = "Till I Collapse",
			TextColor = (Color)Application.Current.Resources["PrimaryColor"],
			FontSize = 18,
			FontAttributes = FontAttributes.Bold
		};
		textStack.Children.Add(titleLabel);

		var artistRow = new HorizontalStackLayout { Spacing = 4 };
		artistRow.Children.Add(new Label {
			Text = "Eminem" + " • " + "4:58",
			TextColor = (Color)Application.Current.Resources["OnSurfaceColor"],
			FontSize = 16
		});

		textStack.Children.Add(artistRow);

		Grid.SetColumn(textStack, 1);
		innerGrid.Children.Add(textStack);

		var playButton = new IconButton {
			IconData = IconPacks.IconKind.MaterialCommunity.Play,
			Style = (Style)Application.Current.Resources["FilledTonalIconButtonStyle"]

		}
		;
		Grid.SetColumn(playButton, 2);
		innerGrid.Children.Add(playButton);

		stackLayout.Children.Add(innerGrid);

		stackLayout.Children.Add(new BoxView {
			HeightRequest = 1,
			BackgroundColor = (Color)Application.Current.Resources["OutlineVariantColor"],
			Margin = new Thickness(8, 0)
		});

		var contentButton = new ContentButton {
			BackgroundColor = Colors.Transparent,
			Content = new StackLayout {
				Spacing = 8,
				HorizontalOptions = LayoutOptions.Fill,
				Orientation = StackOrientation.Horizontal,
				VerticalOptions = LayoutOptions.Center,
				Children =
				{
					new IconButton

					{
						IconData = IconPacks.IconKind.MaterialCommunity.PlaylistPlay,
						Style = (Style)Application.Current.Resources["StandardIconButtonStyle"],
						InputTransparent = true,
						HeightRequest = 48
					},
					new Label
					{
						Text = "Play Next",
						VerticalTextAlignment = TextAlignment.Center,
						TextColor = (Color)Application.Current.Resources["OnSurfaceColor"],
						FontSize = 18
					}
				}
			}

		}
		;
		stackLayout.Children.Add(contentButton);

		card.Content = stackLayout;
		mainGrid.Children.Add(card);

		popUpCard = card;

		PopUpLayer.Content = mainGrid;
		PopUpLayer.BackgroundColor = Color.FromRgba(0, 0, 0, 0.5);

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
