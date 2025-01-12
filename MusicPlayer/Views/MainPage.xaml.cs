

using System.ComponentModel;
using System.Diagnostics;
using Material.Components.Maui;

namespace MusicPlayer;

public partial class MainPage : ContentView {

	private static List<Database.Song> musicList = new List<Database.Song>();

	public Command<Database.Song> LongPressCommand { get; set; } = new Command<Database.Song>((song) => Debug.WriteLine("Long Pressed " + song.Title));


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
		var button = (Grid)sender;
		var song = (Database.Song)button.BindingContext;
		var position = e.GetPosition(button);
		if (position == null) {
			return;
		}
		var point = position.Value;
		if (point.X > 340 && point.X < 368) {
			song.IsFavorite = !song.IsFavorite;
			Debug.WriteLine("Point X: " + point.X + " Y: " + point.Y);
			Debug.WriteLine("Favorite status: " + song.IsFavorite);

			Database.UpdateSong(song);
			return;
		}

		//do the other stuff here

	}



}