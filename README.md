## Housing Creative Tab
In an effort to make Hypixel Housing more latest-version-friendly, this mod adds a few main utilities.

1) A button to filter the creative menu to show blocks available on Housing
   - When the game is launched, the state of whether or not latest version items are shown is set when the creative menu is first opened, and will be set based on whether or not the mod detects you are in Housing.
   - Some blocks, like Smooth Stone, use data values, so even if the real block isn't allowed on Hypixel, you can still take it from the creative menu
2) Protool id overlay
   - Based on a previous project of mine, the mod comes with a resource pack that adds legacy block ids to the end of block names.
3) Item editing commands
   1) `/rename <name>` sets the held item's name
   2) `/damage <amount>` sets the held item's durability
   3) `/count <amoount>` sets the held item's stack size
   4) `/unbreakable <true/false>` sets the held item's unbreakable tag
   5) `/itemtype <id>` changes the held item's base item
   6) `/lore <add/remove/edit> <options>` changes the held item's Lore component
   7) `/armorcolor <hex>` changes the held item's dye color
   8) `/itemmodel <id>` sets the held item's item model (will only affect visuals on modern versions)
   9) `/armortrim <template> <material>` sets the held item's armor trim component (will only affect visuals on modern versions)
   10) `/copyLore` and `/pasteLore` allows copying and pasting lore between items

Should you find errors in the mod (e.g. item is incorrectly filtered out or incorrect/missing translation), you can make a github issue or contact me on discord (@busterbrown1218)