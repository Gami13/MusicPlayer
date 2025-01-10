using System.ComponentModel;
using System.Diagnostics;
using System.Runtime.CompilerServices;
using SQLite;

namespace MusicPlayer {
	public static partial class Database {

		public class Song : INotifyPropertyChanged {

			[PrimaryKey, AutoIncrement]
			public int Id { get; set; }
			public string Title { get; set; } = "";
			public string StoragePath { get; set; } = "";
			public int Duration { get; set; }
			public string Album { get; set; } = "";
			public string Artist { get; set; } = "";
			public string Genre { get; set; } = "";
			public int Year { get; set; }
			public string Cover { get; set; } = "";
			private bool _isFavorite;

			public bool IsFavorite {
				get { return _isFavorite; }
				set {
					if (_isFavorite != value) {
						_isFavorite = value;
						OnPropertyChanged();
					}
				}
			}
			public event PropertyChangedEventHandler PropertyChanged;

			protected void OnPropertyChanged([CallerMemberName] string propertyName = null) {
				PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
			}

		}


		public static void AddSong(Song song) {
			Debug.WriteLine(song);
			database.Insert(song);
			AppState.hasUpdatedMusic = true;
		}

		public static Song GetSong(int id) {
			return database.Get<Song>(id);
		}

		public static void UpdateSong(Song song) {
			database.Update(song);
			AppState.hasUpdatedMusic = true;
		}

		public static void DeleteSong(Song song) {
			//TODO: Delete the file from storage

			database.Delete(song);
			AppState.hasUpdatedMusic = true;

		}

		public static List<Song> GetSongs() {
			return database.Table<Song>().ToList();

		}









	}
}