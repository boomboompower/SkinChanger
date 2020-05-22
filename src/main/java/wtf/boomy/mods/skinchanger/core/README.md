## The access transformer

The transformer modifies the class, here is how the changes impact the game code.
All the transformer does is append an additional conditional check to the `getLocationSkin` and `getLocationCape` methods.

This functionality can be disabled if it causes issues with the game

```diff
public String getSkinType() {
+   String skinChangerType = SkinChangerMod.getInstance().getStorage().getSkinType(this.gameProfile);
+   
+   if (skinChangerType != null) {
+       return skinChangerType;
+   }
    
    return this.skinType == null ? DefaultPlayerSkin.getSkinType(this.gameProfile.getId()) : this.skinType;
}

public ResourceLocation getLocationSkin() {
+   if (SkinChangerMod.getInstance().getCosmeticFactory().getSkinStorage().isUsingSkin(this.gameProfile)) {
+       return SkinChangerMod.getInstance().getCosmeticFactory().getSkinStorage().getSkin();
+   }
    
    if (this.locationSkin == null) {
        this.loadPlayerTextures();
    }
    
    return (ResourceLocation)Objects.firstNonNull(this.locationSkin, DefaultPlayerSkin.getDefaultSkin(this.gameProfile.getId()));
}

public ResourceLocation getLocationCape() {
+   if (SkinChangerMod.getInstance().getCosmeticFactory().getSkinStorage().isUsingCape(this.gameProfile)) {
+       return SkinChangerMod.getInstance().getCosmeticFactory().getSkinStorage().getCape();
+   }
    
    if (this.locationCape == null) {
        this.loadPlayerTextures();
    }
    
    return this.locationCape;
}
```