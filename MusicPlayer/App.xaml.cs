namespace MusicPlayer;

public partial class App : Application
{
    public App(CustomMaterialColorService colorService)
    {
        Database.createDatabase();
        InitializeComponent();
        colorService.Initialize(this.Resources);

        MainPage = new AppShell();

    }
}