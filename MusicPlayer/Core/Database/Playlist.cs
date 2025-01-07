
using SQLite;

namespace MusicPlayer
{
	public static partial class Database
	{


		public class Playlist
		{

			[PrimaryKey, AutoIncrement]
			public int Id { get; set; }
			public required string Name { get; set; }
			public string? Description { get; set; }
			public bool IsFavorite { get; set; }
		}

		public class PlaylistLinker
		{

			[PrimaryKey, AutoIncrement]
			public int Id { get; set; }
			public int PlaylistId { get; set; }
			public int SongId { get; set; }

		}


	}
}