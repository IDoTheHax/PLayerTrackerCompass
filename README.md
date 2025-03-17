# Player Tracker Compass Mod

## Description
The Player Tracker Compass Mod is a Minecraft Fabric mod that adds a special compass item that allows players to track the location of up to two designated target players. The mod is designed for server administrators and provides a powerful tool for tracking player movements in multiplayer environments. Key features include:

- Custom tracking compass item that points to the nearest tracked player
- Support for tracking up to two players simultaneously
- Automatic compass updates every 10 seconds
- Prevention of tracked players receiving compasses
- Command-based system for managing tracking
- Visual feedback through compass naming and colors
- Dimension-aware tracking (only tracks players in the same dimension)

The tracking compass has a magical glint effect and is fireproof, making it a durable tracking tool. The mod requires operator permissions (level 4) to use tracking commands, ensuring controlled access to tracking features.

## Usage

### Installation
1. Ensure you have Fabric Loader and Fabric API installed on your Minecraft server
2. Place the mod JAR file in your server's `mods` folder
3. Start the server to load the mod

### Features and Commands

#### Tracking Compass Item
- All non-tracked players automatically receive a tracking compass upon joining or respawning
- The compass appears in the player's inventory with a magical glint effect
- Tracked players are prevented from receiving tracking compasses
- The compass automatically updates every 10 seconds to point to the nearest tracked player
- Right-clicking the compass updates its tracking target to the nearest valid tracked player

#### Commands
All commands require operator permission level 4 to execute.

1. **Start Tracking a Player**
```
/trackercompass <player>
```
- Adds the specified player to the tracking list (maximum 2 players)
- Removes any tracking compasses from the tracked player's inventory
- Updates all other players' compasses to track the nearest target
- Displays feedback showing the number of currently tracked players (e.g., "Now tracking player: PlayerName (1/2 targets)")
- Fails if trying to track more than 2 players

2. **Stop Tracking**
```
/trackercompass stop
```
- Clears all tracking targets
- Resets all tracking compasses to their default state
- Removes tracking data from all players
- Displays "Stopped tracking" feedback

#### Compass Behavior
- Points to the nearest tracked player in the same dimension
- Shows "Tracking: PlayerName" in the item name when tracking a player
- Displays "No valid targets" if no tracked players are in the same dimension
- Updates automatically every 10 seconds
- Can be manually updated by right-clicking
- Shows appropriate messages when:
    - No targets are set ("Use /trackercompass to set a target first")
    - No valid targets in current dimension ("No valid targets in your dimension")

### Notes
- Tracked players cannot receive tracking compasses through any means (joining, respawning, or manual giving)
- The compass only tracks players in the same dimension as the holder
- The mod uses server-side processing and is compatible with vanilla clients
- All tracking data is cleared when tracking is stopped
- The mod uses color-coded messages for better visibility:
    - Red for errors and stopping tracking
    - Yellow for information messages
    - Light red for tracking confirmation
    - Alternate white for compass names

### Technical Details
- Mod ID: `playertrackercompass`
- Item ID: `playertrackercompass:tracking_compass`
- Uses Polymer API for item rendering
- Implements server-side tick events for updates
- Handles player join and respawn events
- Uses Minecraft's command system for control

This mod is ideal for server administrators running manhunt-style games or needing to monitor specific player movements while maintaining game balance through permission controls and tracking limitations.