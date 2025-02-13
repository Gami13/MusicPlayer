

namespace MusicPlayer;

public partial class PlayerPage : ContentView {




	public PlayerPage() {
		InitializeComponent();




	}

	private void Button_Clicked(object sender, TouchEventArgs e) {
		AudioPlayer.Play();
	}





}