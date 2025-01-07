using SQLite;

namespace MusicPlayer {
	public static partial class Database {
		private static SQLiteConnection database = new SQLiteConnection(Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), Constants.DatabaseFile));
		public static void createDatabase() {
			database.CreateTable<Song>();
			database.CreateTable<Playlist>();
			database.CreateTable<PlaylistLinker>();
		}

	}
}