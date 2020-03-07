# SkinChanger
A Minecraft mod which allows players to change their cape and skin directly from an in-game menu. Skins can be retrieved from files, urls and directly from player names and UUIDs.

### Why?
The SkinChanger mod was initially created when many mods offered capes in their clients/mods if the user pays for them. Minecraft's EULA did not (and still doesn't) allow for mods/clients to profit from exclusive goods they've created in the game. Since mods with payable capes kept popping up, I decided to create SkinChanger; a free alternative to those mods. I intially intended it to be a skin-only mod however soon it also grew to support cape modification due to popular demand. 

After two years of inactivity, I decided it was time for an overhaul. Which is what is happening. The next release will be a complete rewriting.

Featuring:
* A new UI focusing more on the player aspect
* More ways to get skins/capes (urls, local files)
* Compatability with other mods
* Accessability across different versions.

### Features
* Skin from file
* Skin from URL
* Skin from a player's name/UUID

* Cape from file
* Cape from a URL
* Cape from optifine

### Downloads
Downloads for the mod are currently not available for V3.0

Version 2.0 downloads are available on the [forum thread](https://hypixel.net/threads/1244732/)

### Supported Minecraft versions
Tested Minecraft versions

- [ ] 1.15.X
- [ ] 1.14.X
- [ ] 1.13.X
- [ ] 1.12.X
- [ ] 1.11.X
- [ ] 1.10.X
- [ ] 1.9.X
- [x] 1.8.X
- [ ] 1.7.X

### How to install
The newest version of SkinChanger includes an installer. To install the mod just `double click` the mod file and it will be placed in your mods directory automatically. 

The installer should work on multiple operating systems however it is still being tested.

This has been tested for the following OS's
- [x] Windows
- [ ] MacOS
- [ ] Linux

**Note: The mod is placed in the DEFAULT minecraft directory**

### How to build

#### Eclipse
1. Clone the repo: `git clone https://github.com/boomboompower/SkinChanger`
2. Copy in the `eclipse/` folder from a fresh installation of the [Forge MDK](http://files.minecraftforge.net)
3. Run `./gradlew setupDecompWorkspace eclipse`
4. Open the project in Eclipse
5. Run `./gradlew build` to build the mod
6. Run the installer

#### IntelliJ
1.  Clone the repo: `git clone https://github.com/boomboompowee/SkinChanger`
2. Run `./gradlew setupDecompWorkspace idea genIntellijRuns`
3. Open the project in IntelliJ
4. Import the gradle project, sync gradle
5. Open settings (Control + Alt + S), and search for `Annotation Processors`
6. Check the `Enable annotation processing box
7. Run `./gradlew build` to build the mod
8. Run the installer located in `./build/libs/SkinChanger.jar`

#### Contributors
* [boomboompower](https://github.com/boomboompower)

#### Redistribution
If you would like to redistribute SkinChanger via a modpack, client or by any other means; please contact [@boomboompowerr](https://twitter.com/boomboompowerr) on Twitter. This is to ensure the code here is being used for ethical purposes.


#### Monetization
***I do not support or or condone monetization of this code; using this code for commercial gain is not allowed.***

The source code here is provided for learning purposes and I do not endorse modified distributions which lock these features behind a paywall. Skins and Capes should not be monetizable components of the Minecraft client which is why I created this as a free mod.

I don't profit from mods like these and neither should you.
