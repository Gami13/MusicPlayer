using Material.Components.Maui;
using MauiContentButton;
using Microsoft.Maui.Controls.Shapes;

namespace MusicPlayer;

public static class Components {
	public static Grid CreateSongHeader(Database.Song song) {
		var innerGrid = new Grid {
			HorizontalOptions = LayoutOptions.Fill,

			Padding = new Thickness(8, 0),
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
				Source = ImageSource.FromStream(() => new MemoryStream(song.Cover)),
				Aspect = Aspect.AspectFill
			}
		};



		var textStack = new StackLayout {
			VerticalOptions = LayoutOptions.Center,
			Spacing = 0,
			Scale = 0.9,
			Orientation = StackOrientation.Vertical
		};

		var titleLabel = new Label {
			Text = song.Title,
			TextColor = MDColor.PrimaryColor.GetColor(),
			FontSize = 18,
			FontAttributes = FontAttributes.Bold
		};
		textStack.Children.Add(titleLabel);

		var artistRow = new HorizontalStackLayout { Spacing = 4 };
		artistRow.Children.Add(new Label {
			Text = song.Artist + " • " + Utilities.GetDurationString(song.Duration),
			TextColor = MDColor.OnSurfaceColor.GetColor(),
			FontSize = 16
		});

		textStack.Children.Add(artistRow);
#pragma warning disable CS8602 // Dereference of a possibly null reference.
		var playButton = new IconButton {
			IconData = IconPacks.IconKind.MaterialCommunity.Play,
			Style = (Style)Application.Current.Resources["FilledTonalIconButtonStyle"]

		};
#pragma warning restore CS8602 // Dereference of a possibly null reference.



		Grid.SetColumn(imageBorder, 0);
		innerGrid.Children.Add(imageBorder);
		Grid.SetColumn(textStack, 1);
		innerGrid.Children.Add(textStack);
		Grid.SetColumn(playButton, 2);
		innerGrid.Children.Add(playButton);
		return innerGrid;

	}

	public static ContentButton CreateSongMenuListItem(SongMenuItem item, Database.Song song) {
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
						IconData = item.Icon,
						IconColor = item.Color.GetColor(),
						Style = (Style)Application.Current.Resources["StandardIconButtonStyle"],
						InputTransparent = true,
						HeightRequest = 48
					},
					new Label
					{
						Text = String.Format(item.Title, song.Artist),
						VerticalTextAlignment = TextAlignment.Center,
						TextColor = item.Color.GetColor(),
						FontSize = 18
					}
				}
			},


		};
		contentButton.Clicked += (s, e) => item.OnClick(song);
		return contentButton;

	}
}