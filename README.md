# ArcadeGames (Paper 1.21.10, Java 21)

Five minigames: **Prop Hunt, BlockParty, TNTRun, Party Games, Mini Walls**.

## Build
```
mvn clean package
```
Jar ends up in `target/ArcadeGames.jar` -> drop into `plugins/`.

## Player commands
- `/arcade join [arena|game]` (alias `/ag`) - e.g. `/ag join tntrun`
  - `/ag join` picks a random game that has a free arena, then a random free arena of it
  - `/ag join tntrun` picks a random free TNT Run arena
  - `/ag join arena1` joins that arena; if it is full / running / not ready you are sent to another arena of the same game
- `/arcade gui [game]` (or just `/arcade`) - opens the arena menu; click an arena to join it
- `/arcade leave`, `/arcade list`
- Right-click the red bed in the waiting lobby to leave.

## Everything is editable (folder `plugins/ArcadeGames/`)
The folder is created the first time the plugin loads. Edit a file, then run `/arcade reload` - no restart needed.

| File | What is in it |
|---|---|
| `config.yml` | lobby countdown, end delay, leave item, **matchmaking** (random / fullest / emptiest / ordered, fallback when full), where players go after a match |
| `gui.yml` | **the arena menu**: title, size, which slots hold arenas, background, empty slots, the item / name / lore for every arena state (players, min, max, free places ...), buttons, paging, sorting |
| `messages.yml` | every message, title and action bar players see (colour codes with `&`, `{placeholders}` are filled in) |
| `games/prophunt.yml` | hide / seek time, seekers per player, items, allowed blocks, Bedrock offsets, default player limits |
| `games/blockparty.yml` | rounds, round speed, tile size, pause between rounds |
| `games/tntrun.yml` | double jumps and their power, start delay, how fast blocks vanish |
| `games/partygames.yml` | which mini-games run, their length / titles / difficulty, points |
| `games/miniwalls.yml` | Wither health, time limit, the starting kit |
| `arenas.yml` | arena locations (written by the setup commands below) |

Every game file also has `enabled: true/false` (a disabled game is skipped by matchmaking) and
`default-min-players` / `default-max-players` for newly created arenas.
A key you delete from a file simply falls back to the built-in default.

### Matchmaking (`config.yml`)
```yaml
matchmaking:
  mode: random                      # random | fullest | emptiest | ordered
  prefer-lobbies-with-players: true # with "random": new players go where somebody already waits, so games start
  fallback-if-full: true            # full/running arena -> send to another arena of the same game
  join-message: true
```
This works the same for all five games.

## Admin setup (permission `arcade.admin`)
Stand where you want the point to be, then:

| Command | What it does |
|---|---|
| `/arcade create <name> <game>` | new arena (`prophunt` `blockparty` `tntrun` `partygames` `miniwalls`) |
| `/arcade setwaiting <name>` | waiting lobby |
| `/arcade setspawn <name> [n]` | add/replace spawn n |
| `/arcade setwither <name> <1-4>` | Mini Walls wither position for team n |
| `/arcade pos1 / pos2 <name>` | region corners (stand ON the corner block) |
| `/arcade setplayers <name> <min> <max>` | player limits |
| `/arcade info / forcestart / forcestop / delete <name>` | management |
| `/arcade reload` | re-read config.yml, messages.yml and games/*.yml |

### Per game
- **TNTRun** - waiting, spawns, pos1/pos2 = bottom corner and top corner of the whole floor stack (sand/gravel over TNT works). Falling below the region eliminates you.
- **BlockParty** - waiting, spawns above the floor, pos1/pos2 = two corners of the flat floor. The floor is regenerated every round and restored afterwards.
- **Party Games** - waiting, spawns standing on the platform, pos1/pos2 = the platform's top layer. Needs a void/drop around the platform. Plays Anvil Rain, Bombardment, Sumo, Spleef in random order.
- **Prop Hunt** - waiting, spawn 1 = hiders, spawn 2 = seekers (optional). Hiders are invisible with a block-display disguise; right-click blocks with the nether star to copy them.
- **Mini Walls** - waiting, `setspawn 1..4` + `setwither 1..4` (2-4 teams). Wither dead = no more respawns for that team; last team alive wins.

Arenas are saved in `plugins/ArcadeGames/arenas.yml`.

## Upgrading from 1.0
The Prop Hunt block lists / offsets moved from `config.yml` to `games/prophunt.yml`
(same key names, without the `prophunt:` prefix). Copy your values over once.

## New in 1.2
- **Always back to spawn**: leaving, a finished or stopped match, `/arcade forcestop`, disconnecting - players always end up at the spawn from `config.yml`, never back where they joined from. (`return-location: previous` in `config.yml` restores the old behaviour.)
- **Arena menu** (`/arcade gui`, `/arcade gui <game>`, or plain `/arcade`), fully editable in `gui.yml`.
