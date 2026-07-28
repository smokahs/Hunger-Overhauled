<p align="center">
  <img src="webassets/icon.png" width="200" alt="Hunger Overhauled Icon"/>
</p>


# Hunger Overhauled

A 1.20.1 Forge fork of [Hunger Overhaul](https://github.com/progwml6/HungerOverhaul) by iguanaman, with additions, updates, and fixes!

Requires [AppleSkin](https://www.curseforge.com/minecraft/mc-mods/appleskin) on client.

## Hunger... but Better!

**Hunger and health**
- Faster hunger drain, scaling with difficulty
- Hunger drains on peaceful too
- Configurable starvation damage (default: starving to zero kills you)
- Slow, difficulty-scaled health regen that gets slower the more hurt you are
- Minimum hunger to heal, healing hunger drain toggle
- Hunger set on respawn, scaled by difficulty
- Nausea / slowness / weakness / mining fatigue at low health or hunger
- New UI popups "Hurt / Injured / Dying" and "Peckish / Hungry / Starving" to aid players and add an rpg-like feel.

**Food**
- Food values divided down, with an explicit table for vanilla items
- Stack sizes scaled to how filling a food is
- Eating animation length scaled to how filling a food is
- "Well Fed" effect that speeds up health regen
- Food value tooltips ("Nourishing light meal")
- Optional: eating restores health

**Farming**
- Crop, sapling, cocoa, cactus, sugarcane and nether wart growth slowed
- Crops grow at full speed only in the right biomes, and only in sunlight
- Right click to harvest, with configurable seed and produce yields
- Same yield control when breaking crops
- Bonemeal is unreliable and less effective
- Hoes only make farmland near water; tilling dry grass just tears it up, sometimes for a seed
- Seeds removed from grass drops, so tilling is the early source
- Wood and stone hoe recipes removed, wheat -> seed recipe added
- Slower breeding, slower growing up, slower egg laying, cows need time between milkings

**World**
- High tier food in butcher trades, crops bought and saplings sold by farmers
- High tier food in dungeon, mineshaft, pyramid and jungle temple chests
- Extra crop fields in villages, sometimes reeds or pumpkins/melons

## Config

`config/hungeroverhauled-common.toml` and `config/hungeroverhauled-client.toml`. Option names match the 1.12 config, so an
old `HungerOverhaul.cfg` is a usable reference for what to set.

### Per-mod food values and growth

The 1.12 version had hardcoded support for HarvestCraft, Natura, Biomes O' Plenty, and Tinkers'. They no no longer exist. You can add anything mod related, in `config/hungeroverhauled/`. Any file ending in `.json` in that folder is read; `_example.json.txt` is written on first launch.

```json
{
  "foods": [
    { "name": "pamhc2foodcore:steamedspinach", "hunger": 3, "saturationModifier": 0.15 }
  ],
  "foodsBlacklist": ["croptopia:anchovy", "#forge:raw_fishes"],
  "dropsBlacklist": ["farmersdelight:rice"],
  "harvestBlacklist": ["farmersdelight:rice"],
  "growth": [
    {
      "name": "pamhc2trees:apricotfruit",
      "category": "tree_crop",
      "needsSunlight": true,
      "biomes": ["#minecraft:is_jungle"]
    }
  ]
}
```

`/hungeroverhauled reload` rereads the json and reapplies food values without a restart.


## License

MIT
