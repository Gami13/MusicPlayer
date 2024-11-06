using CommunityToolkit.Maui.Behaviors;

namespace MusicPlayer
{
    public class TintableIcon : Image
    {
        public static readonly BindableProperty TintColorProperty = BindableProperty.Create(nameof(TintColor), typeof(Color), typeof(TintableIcon), null, propertyChanged: UpdateTintColor);
        public Color TintColor
        {
            get => (Color)GetValue(TintColorProperty);
            set => SetValue(TintColorProperty, value);
        }
        public TintableIcon()
        {
            this.Behaviors.Add(new IconTintColorBehavior());
            if (TintColor != null)
            {
                SetTint(TintColor);
            }
        }
        private static void UpdateTintColor(object sender, object oldValue, object newValue)
        {
            if (sender is TintableIcon image && newValue is Color color)
            {
                image.SetTint(color);
            }
        }
        private void SetTint(Color color)
        {
            this.Behaviors.First().SetValue(IconTintColorBehavior.TintColorProperty, color);
        }
    }
}