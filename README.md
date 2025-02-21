# Player Tracker Compass (Fabric 1.21)

This mod adds a special **Tracking Compass** that allows players to track other players on the server. The compass updates automatically to point to the tracked player's position. Once a player is tracked, the compass will not be droppable or movable to other inventories like chests or hoppers, ensuring that the tracking functionality is preserved.

Join My Discord: https://discord.gg/W49Mf7nbuK

---

## **Features**

- **Tracking Compass**:  
  A special compass item that tracks a specific player's position.

- **Undroppable**:  
  The tracking compass cannot be dropped, moved into chests, hoppers, or other containers.

- **Persistent Tracking**:  
  The compass stores the UUID of the tracked player, meaning the tracking persists even after server restarts.

- **Set Target with Command**:  
  Use the `/trackercompass` command to set the player to track. The compass will automatically point to their position.

---

## **Installation**

1. Ensure you have **Fabric** and **Fabric API** installed for Minecraft 1.21.
2. Download the mod `.jar` file from the releases section.
3. Place the `.jar` file into your **mods** folder.
4. Start your Minecraft client or server with Fabric.

---

## **Commands**

- `/trackercompass <player>`  
  Sets the target player to track. Only players with **permission level 4 or higher** can use this command.

---

## **How to Use**

1. **Get the Tracking Compass**  
   When you join the server, you will receive a **Tracking Compass** if you don’t have one already in your inventory.

2. **Set a Target**  
   Use the `/trackercompass <player>` command to start tracking the selected player. The compass will then point to their position.

3. **Tracking**  
   The compass will automatically update its direction to point towards the tracked player. The compass will show "Tracking: <PlayerName>" in its name.

4. **Persistence**  
   The compass will remain in your inventory, and the tracking information will persist even after restarting the server.

---

## **Mod Settings**

This mod currently does not include any configurable options, but the following functionality is built-in:

- **Prevents the compass from being dropped** via the `Q` key.
- **Prevents the compass from being moved to containers**, such as chests or hoppers.
- **Prevents the compass from being dropped on death**.

---

## **Known Issues**

- None at the moment! Feel free to submit issues or feature requests on the [GitHub Issues page](#).

---

## **License**

This project is licensed under the GNU GENERAL PUBLIC LICENSE – see the [LICENSE.txt](LICENSE.txt) file for details.

---

## **Contributing**

Feel free to open an issue or pull request if you want to contribute to the development of this mod! 

---