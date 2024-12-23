using System.Diagnostics;
using System.Text.Json;
using CommunityToolkit.Maui.Storage;
using CommunityToolkit.Mvvm.Messaging;

namespace MusicPlayer
{


    public static partial class Utilities
    {



        public static async Task UpdateStorageLocation()
        {
            var result = await FolderPicker.Default.PickAsync();
            if (result.IsSuccessful && result.Folder?.Path != null)
            {
                WeakReferenceMessenger.Default.Send(new SelectedDirectoryChanged(result.Folder.Path.ToString()));
            }
        }

    }
}