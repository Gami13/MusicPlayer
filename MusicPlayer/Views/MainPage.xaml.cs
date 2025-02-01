

using System.ComponentModel;
using System.Diagnostics;
using Material.Components.Maui;

namespace MusicPlayer;

public partial class MainPage : ContentView {

	private static List<Database.Song> musicList = new List<Database.Song>();

	public Command<Database.Song> LongPressCommand { get; set; } = new Command<Database.Song>(AppState.ShowSongMenu);


	public MainPage() {
		InitializeComponent();

		BindingContext = this;

		AppState.SubscribeToNavigation(RouteKey.Home, () => {
			Debug.WriteLine("MainPage: OnNavigate " + AppState.hasUpdatedMusic);
			if (AppState.hasUpdatedMusic) {
				updateList();
			}
		});

		updateList();
	}


	private void Chip_Clicked(object sender, TouchEventArgs e) {
		var chip = (Chip)sender;
		chip.IsSelected = false;
	}

	private void Favorite(object sender, TouchEventArgs e) {
		var button = (IconButton)sender;
		var song = (Database.Song)button.BindingContext;
		song.IsFavorite = !song.IsFavorite;
		Debug.WriteLine("Favorite status: " + song.IsFavorite);

		Database.UpdateSong(song);

	}
	private void updateList() {
		musicList = Database.GetSongs();
		SongsList.ItemsSource = musicList;
		AppState.hasUpdatedMusic = false;
	}

	private void SongTapped(object sender, TappedEventArgs e) {
		var songElement = (Grid)sender;
		var song = (Database.Song)songElement.BindingContext;
		var position = e.GetPosition(songElement);
		if (position == null) {
			return;
		}
		var point = position.Value;
		Debug.WriteLine("Tapped at " + point.X + " " + point.Y);
		//if point is in the favorite button
		if (point.X > songElement.Width - 48 && point.X < songElement.Width - 16) {
			song.IsFavorite = !song.IsFavorite;
			Database.UpdateSong(song);
			return;
		}

		//do the other stuff here

	}



}