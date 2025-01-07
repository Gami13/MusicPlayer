

using System.ComponentModel;
using System.Diagnostics;
using Material.Components.Maui;

namespace MusicPlayer;

public partial class MainPage : ContentView {

	private static List<Database.Song> musicList = new List<Database.Song>();



	public MainPage() {
		InitializeComponent();


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

	private void SongsList_ItemSelected(object sender, SelectedItemChangedEventArgs e) {
		var x = (ListView)sender;
		x.SelectedItem = null;
		Debug.WriteLine("Selected: " + e.SelectedItemIndex + "  " + e.SelectedItem);
		if (e.SelectedItemIndex == -1) return;

		//play song here ig


	}



}