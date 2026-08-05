# Changelog

## v2.2
1. Walking costs hunger again
    - Vanilla removed walking exhaustion in 1.11; this brings back the old 0.01 per block cost
    - New `walkExhaustionPerBlock` option in the `hunger` section (default 0.01, set to 0 to disable)
2. Fast saturation regen can be turned off
    - Vanilla 1.9 added free fast healing at a full hunger bar; 1.7.10 never had it
    - New `disableFastRegen` option in the `health` section (default off, GTNH preset turns it on)
3. The onscreen status text can be moved
    - New client options: `healthTextOffsetX/Y` and `hungerTextOffsetX/Y`, so each text moves on its own

## v2.1
1. Added the **Healing Axe**, ported from the 1.7.10 Extra Utilities / New Horizons era
    - Left click a mob to heal it 4 health for 3.5 of your own, or hit an undead one for 16
    - Left click a zombie villager to cure it on the spot
    - Holding it slowly feeds you, but every swing costs 10 exhaustion
    - Never wears out, never lands a normal hit
    - Hold-Shift tooltip listing the passive, the heal, and the cure
    - New `healing axe` config section: `enableHealingAxe` and `enableHealingAxeRecipe` (both default off)
        - Recipe when enabled: netherite ingot + nether star over netherite ingot + stick over stick
        - The item is always registered so worlds holding one keep loading; the toggle only decides whether it is reachable
2. Fixed hunger refilling itself on peaceful
3. Reworked the GTNH preset (`setToNewHorizonsDefaults`)
    - New Horizons values are now written straight into the config file, so the options you see in the file or the config GUI are the real ones
    - Applies on reload as well as load, so flipping the toggle takes hold without a restart
    - Dropped the `newhorizons_backup.json` side file and its restore path

## v2.0
1. Release! A 1.20.1 Forge port of [Hunger Overhaul](https://github.com/progwml6/HungerOverhaul), option names kept from the 1.12 config
2. Hunger and health
    - Faster hunger drain scaling with difficulty, drains on peaceful too
    - Configurable starvation damage (default: starving to zero kills you)
    - Slow difficulty-scaled health regen that gets slower the more hurt you are, with a minimum hunger to heal
    - Hunger set on respawn, scaled by difficulty
    - Nausea / slowness / weakness / mining fatigue at low health or hunger
    - New "Hurt / Injured / Dying" and "Peckish / Hungry / Starving" UI popups
3. Food
    - Food values divided down, with an explicit table for vanilla items
    - Stack size and eating animation length scaled to how filling a food is
    - "Well Fed" effect that speeds up health regen
    - Food value tooltips ("Nourishing light meal")
    - Optional eating restores health
4. Farming
    - Crop, sapling, cocoa, cactus, sugarcane and nether wart growth slowed
    - Full growth speed only in the right biome and in sunlight
    - Right click to harvest, with configurable seed and produce yields; same control when breaking crops
    - Bonemeal is unreliable and less effective
    - Hoes only make farmland near water; tilling dry grass just tears it up, sometimes for a seed
    - Seeds removed from grass drops, so tilling is the early source
    - Wood and stone hoe recipes removed, wheat → seed recipe added
    - Slower breeding, slower growing up, slower egg laying, cooldown between milkings
5. World
    - High tier food in butcher trades, crops bought and saplings sold by farmers
    - High tier food in dungeon, mineshaft, pyramid and jungle temple chests
    - Extra crop fields in villages, sometimes reeds or pumpkins/melons
6. Per-mod food values and growth via `config/hungeroverhauled/*.json` (foods, blacklists, growth categories, biomes), replacing the old hardcoded HarvestCraft / Natura / BOP / Tinkers' support
    - `/hungeroverhauled reload` rereads the json and reapplies food values without a restart
7. Spice of Life: Pot Pie compat
8. GTNH settings section: `setToNewHorizonsDefaults` preset, plus `explodeInhumaneKills` / `inhumaneKillChance`
9. AppleSkin required on client, so its overlay reads the modified values
