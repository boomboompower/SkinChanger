## The access transformer

The transformer modifies the class, here is how the changes impact the game code.
All the transformer does is append an additional conditional check to the `getLocationSkin`, `getLocationCape` and `getSkinType` methods.

This functionality can be disabled if it causes issues with the game

```diff
public String getSkinType() {
+   String skinType = SkinChangerMod.getInstance().getStorage().getSkinType(this.gameProfile);
+   
+   if (skinType != null) {
+       return skinType;
+   }
    
    return this.skinType == null ? DefaultPlayerSkin.getSkinType(this.gameProfile.getId()) : this.skinType;
}

public ResourceLocation getLocationSkin() {
+   ResourceLocation customSkin = SkinChangerMod.getInstance().getStorage().getPlayerSkin(this.gameProfile);
+   
+   if (customSkin != null) {
+       return customSkin;
+   }
    
    if (this.locationSkin == null) {
        this.loadPlayerTextures();
    }
    
    return (ResourceLocation)Objects.firstNonNull(this.locationSkin, DefaultPlayerSkin.getDefaultSkin(this.gameProfile.getId()));
}

public ResourceLocation getLocationCape() {
+   ResourceLocation customCape = SkinChangerMod.getInstance().getStorage().getPlayerSkin(this.gameProfile);
+   
+   if (customCape != null) {
+       return customCape;
+   }
    
    if (this.locationCape == null) {
        this.loadPlayerTextures();
    }
    
    return this.locationCape;
}
```