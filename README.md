# spaceshooter-java

Dumping the source files for a university assignment from 2020.  

2D game written in java (without any external libraries). Includes-
- Standard 2d game systems - collision, hitboxes, hurtboxes, sprites (with rotation), user input, music, sound effects
- Randomly generated infinite star field (background)
- Experience system to upgrade the player's ship
- Multiple weapons, multiple enemy types with unique AI (collision avoidance, collision seeking etc)
- Boss fights with unlockable upgrades  

## How to Run

I used netbeans to build the project at the time, but you can run the game using just javac and java - 
```
javac -d .\out .\src\spaceshooter\*.java .\src\spaceshooter\gfx\*.java
```
```
java -cp ".\out;.\res" spaceshooter.Game
```

## Resources used

[Kenney - Game assets](https://www.kenney.nl/)  
[OpenGameArt - Game assets](https://opengameart.org/)  
