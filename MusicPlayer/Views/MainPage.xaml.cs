

using System.ComponentModel;
using System.Diagnostics;
using Material.Components.Maui;

namespace MusicPlayer;

public partial class MainPage : ContentView {

	private static List<Database.Song> musicList = new List<Database.Song>();

	public Command<Database.Song> LongPressCommand = new Command<Database.Song>((Database.Song song) => {
		Debug.WriteLine("Long Pressed " + song.Title);
	});
	public Command<Database.Song> TapCommand = new Command<Database.Song>((Database.Song song) => {
		Debug.WriteLine("Tapped " + song.Title);
	});

	public MainPage() {
		InitializeComponent();

		BindingContext = this;

		AppState.SubscribeToNavigation(RouteKey.Home, () => {
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

		Database.UpdateSong(song);

	}
	private void updateList() {
		musicList = Database.GetSongs();
		SongsList.ItemsSource = musicList;
		AppState.hasUpdatedMusic = false;
	}

	private void SongTapped(object sender, TappedEventArgs e) {
		var song = (Database.Song)((Grid)sender).BindingContext;
		Debug.WriteLine(song.Title);
	}



}