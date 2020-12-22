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

If the Optifine patcher is enabled, the following change will be made to the AbstractClientPlayer class.

It is worth noting that turning capes off in the optifine settings **will not** impact SkinChanger with capes on. Meaning you
will either need to disable capes in the mod settings, or disable the patch in the mod settings.
```diff
public ResourceLocation getLocationCape() {
+   ResourceLocation playerCape = SkinChangerMod.getInstance().getStorage().getPlayerCape(getGameProfile());
+   
+   if (playerCape != null) {
+       return playerCape;
+   }

    if (!Config.isShowCapes()) {
        return null;
    }
    
    if (this.reloadCapeTimeMs != 0L && System.currentTimeMillis() > this.reloadCapeTimeMs) {
        CapeUtils.reloadCape(this);
        this.reloadCapeTimeMs = 0L;
    }
    if (this.locationOfCape != null) {
        return this.locationOfCape;
    }
    final NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
    return (networkplayerinfo == null) ? null : networkplayerinfo.getLocationCape();
}
``` 