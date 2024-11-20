using System;


namespace MusicPlayer
{
	[ContentProperty(nameof(Key))]
	public class TranslateExtension : IMarkupExtension<BindingBase>
	{
		public LocalizationKeys Key { get; set; }

		public BindingBase ProvideValue(IServiceProvider serviceProvider)
		{
			return new Binding($"[{Key}]", source: LocalizationResourceManager.Instance);
		}

		object IMarkupExtension.ProvideValue(IServiceProvider serviceProvider)
		{
			return ProvideValue(serviceProvider);
		}
	}

}

