using System.Diagnostics;
using System.Text.Json;
using Android.Content;

namespace MusicPlayer
{


    public static partial class Utilities
    {

        public static async Task UpdateStorageLocation()
        {
            var intent = new Intent(Intent.ActionOpenDocumentTree);
            intent.AddFlags(ActivityFlags.GrantPersistableUriPermission |
                      ActivityFlags.GrantReadUriPermission |
                      ActivityFlags.GrantWriteUriPermission);

            if (Platform.CurrentActivity != null)
            {
                Platform.CurrentActivity.StartActivityForResult(intent, Constants.MUSIC_DIRECTORY_REQUEST_CODE);
            }
            await Task.CompletedTask;
        }



    }
}