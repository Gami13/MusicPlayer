

using Material.Components.Maui;

namespace MusicPlayer;

public partial class MainPage : ContentView {

	private static List<Database.Song> musicList = new List<Database.Song>();



	public MainPage() {
		InitializeComponent();

		musicList = Database.GetSongs();
		AppState.SubscribeToNavigation(RouteKey.Home, () => {
			if (AppState.hasUpdatedMusic) {
				musicList = Database.GetSongs();
				AppState.hasUpdatedMusic = false;
			}
		});
		SongsList.ItemsSource = musicList;
	}



	private void Chip_Clicked(object sender, TouchEventArgs e) {
		var chip = (Chip)sender;
		chip.IsSelected = false;
	}


}