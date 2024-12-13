using System.Diagnostics;
using SQLite;

namespace MusicPlayer
{
	public static partial class Database
	{

		public class Song
		{





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
			public bool IsFavorite { get; set; }

		}

		public static void AddSong(Song song)
		{
			Debug.WriteLine("adding");
			if (database == null)
			{
				createDatabase();
				AddSong(song);
				return;
			}

			Debug.WriteLine(song);
			database.Insert(song);

		}

		public static Song GetSong(int id)
		{
			if (database == null)
			{
				createDatabase();
				return GetSong(id);

			}

			return database.Get<Song>(id);

		}

		public static void UpdateSong(Song song)
		{
			if (database == null)
			{
				createDatabase();
				UpdateSong(song);
				return;
			}

			database.Update(song);

		}

		public static void DeleteSong(Song song)
		{
			if (database == null)
			{
				createDatabase();
				DeleteSong(song);
				return;
			}

			database.Delete(song);

		}

		public static List<Song> GetSongs()
		{
			if (database == null)
			{
				createDatabase();
				return GetSongs();
			}

			return database.Table<Song>().ToList();

		}









	}
}