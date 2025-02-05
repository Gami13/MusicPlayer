using System.ComponentModel;
using System.Diagnostics;
using System.Runtime.CompilerServices;
using SQLite;

namespace MusicPlayer {
	public static partial class Database {

		public class Song : INotifyPropertyChanged {

			[PrimaryKey, AutoIncrement]
			public int Id { get; set; }
			public string YoutubeId { get; set; } = "";
			public string Title { get; set; } = "";
			public string StoragePath { get; set; } = "";
			public int Duration { get; set; }
			public string Album { get; set; } = "";
			public string Artist { get; set; } = "";
			public string Genre { get; set; } = "";
			public int Year { get; set; }
			public byte[] Cover { get; set; } = new byte[0];
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
			public event PropertyChangedEventHandler? PropertyChanged;

			protected void OnPropertyChanged([CallerMemberName] string? propertyName = null) {
				PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
			}

		}


		public static void AddSong(Song song) {
			Debug.WriteLine("Added: " + song);
			database.Insert(song);
			AppState.SongsList.Add(song);
		}

		public static Song GetSong(int id) {
			return database.Get<Song>(id);
		}

		public static void UpdateSong(Song song) {
			database.Update(song);
		}

		public static void DeleteSong(Song song) {
			//TODO: Delete the file from storage

			database.Delete(song);

		}

		public static List<Song> GetSongs() {
			Debug.WriteLine("Getting songs");
			return database.Table<Song>().ToList();

		}









	}
}