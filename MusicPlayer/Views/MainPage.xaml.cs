

using System.ComponentModel;
using System.Diagnostics;
using Material.Components.Maui;

namespace MusicPlayer;

public partial class MainPage : ContentView {

	private static List<Database.Song> musicList = new List<Database.Song>();

	public Command<Database.Song> LongPressCommand { get; set; } = new Command<Database.Song>((song) => Debug.WriteLine("Long Pressed " + song.Title));
	public Command<TouchParameters> TapCommand { get; set; } = new Command<TouchParameters>((TouchParameters parameters) => {
		var song = parameters.Song;
		Debug.WriteLine("Tapped " + song.Title);
		var points = parameters.TouchPoints;
		Debug.WriteLine(points);
		// Debug.WriteLine($"Touch point X: {points[0].X} Y: {points[0].Y}");
	});

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

		var song = (Database.Song)((Grid)sender).BindingContext;
		Debug.WriteLine(song.Title);
	}



}