using SQLite;

namespace MusicPlayer
{
    public static partial class Database
    {
        private static SQLiteConnection? database;
        public static void createDatabase()
        {
            var databasePath = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), Constants.DatabaseFile);
            database = new SQLiteConnection(databasePath);
            database.CreateTable<Song>();
            database.CreateTable<Playlist>();
            database.CreateTable<PlaylistLinker>();

            if (Constants.IsDebug)
            {

                // Database.setStepTarget(100_000);
                // Database.setBirthDate(new DateTime(2005, 2, 4));
                // Database.setHeight(1690);
                // Database.setWeight(53000);
                // Database.setSex(SexType.IllegalAliensAfterTransgenderOperationsDoneInPrison);
            }
        }

    }
}