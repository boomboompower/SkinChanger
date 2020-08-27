# SkinChanger
A Minecraft mod which allows players to change their cape and skin directly from an in-game menu. Skins can be retrieved from files, urls and directly from player names and UUIDs.

##### Badges
[![Size](https://img.shields.io/github/languages/code-size/boomboompower/SkinChanger?style=flat-square)](https://github.com/boomboompower/SkinChanger)
[![Downloads](https://img.shields.io/github/downloads/boomboompower/SkinChanger/total.svg?style=flat-square)](https://github.com/boomboompower/SkinChanger/releases)
[![CI](https://github.com/boomboompower/SkinChanger/workflows/Java%20CI%20with%20Forge/badge.svg?style=flat-square)](https://github.com/boomboompower/SkinChanger/actions)

### Why?
The SkinChanger mod created when many mods offered capes in their clients/mods as a perk for donating. Minecraft's EULA did not (and still doesn't) allow for mods/clients to profit from exclusive goods they've created in the game. Since mods with payable capes kept popping up, I decided to create SkinChanger; a free alternative to those mods. The mod initially created with only skins in mind, however soon it also grew to support cape modification due to popular demand. 

After two years of inactivity, I decided it was time for an overhaul. Which is what is happening. The next release will be a complete rewriting.

Featuring:
* A new UI focusing more on the player aspect
* More ways to get skins/capes (urls, local files)
* Compatibility with other mods
* Accessibility across different versions.

### Features
For skins: 
* Skin from file
* Skin from URL
* Skin from a player's name/UUID

For capes: 
* Cape from file
* Cape from a URL
* Cape from optifine

### Downloads
Downloads are available on **[this website](https://mods.boomy.wtf/#skinchanger)** or in the `Releases` tab.

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

#### IntelliJ IDEA
1.  Clone the repo: `git clone
    https://github.com/boomboompower/SkinChanger`
2. Run `./gradlew setupDecompWorkspace idea genIntellijRuns`
3. Open the project in IntelliJ
4. Import the gradle project, sync gradle
5. Run `./gradlew build` to build the mod
6. Run the installer located in `./build/libs/SkinChanger.jar`

#### Contributors
* [boomboompower](https://github.com/boomboompower)

##### Support
[![Become a Patreon](https://c5.patreon.com/external/logo/become_a_patron_button.png)](https://www.patreon.com/boomboompower)

#### Redistribution
If you would like to redistribute SkinChanger via a modpack, client or by any other means; please contact me. This is to ensure the code here is being used for ethical purposes.

#### Monetization
***I do not support or condone monetization of the features of this mod/code; as per the license it is not allowed.***

* I do not endorse modified distributions which lock these features behind a paywall. 
* Skins and Capes should not be monetizable components of the Minecraft client which is why I created this as a free mod.
