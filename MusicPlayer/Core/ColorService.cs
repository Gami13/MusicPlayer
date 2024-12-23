using MaterialColorUtilities.Maui;
using MaterialColorUtilities.Palettes;
using MaterialColorUtilities.Schemes;
using Microsoft.Extensions.Options;
using System.Diagnostics;

public class CustomMaterialColorService : MaterialColorService<CorePalette, Scheme<uint>, Scheme<Color>, LightSchemeMapper, DarkSchemeMapper>
{

    private ResourceDictionary _appResources = null!;
    public CustomMaterialColorService(IOptions<MaterialColorOptions> options, IDynamicColorService dynamicColorService, IPreferences preferences) : base(options, dynamicColorService, preferences)
    {
    }

    protected override void Apply()
    {

        foreach (KeyValuePair<string, Color> color in SchemeMaui.Enumerate())
        {
            _appResources[color.Key + "Color"] = color.Value;
        }

    }
    public override void Initialize(ResourceDictionary resourceDictionary)
    {
        _appResources = resourceDictionary;
        base.Initialize(resourceDictionary);
    }
}

