# COMP3111 Project - Tower Defense 
## Team Information
ID: #29 (DarkLight)
 
Course Stream: Honors

Members:
 * Task 1: *Name* (cshoae@connect.ust.hk)
 * Task 2: SHI Jianhua (jshian@connect.ust.hk)
 * Task 3: LEUNG, Tin Long (tlleungac@connect.ust.hk) 

## Grading Process
### Initialization
1. Start the database server.
	1. ...
1. Clone this repository to two different lab machines. They shall be referred to as *Machine A* and *Machine B* respectively.
1. Complete `Grade Task 1`, `Grade Task 2` and `Grade Task 3` on *Machine A*. (See below)
### Grade Task 1
1. Run the file `towerDefence-release.jar` to start the application. Verify the following:
	* The arena is displayed as a square with side length 480px. (Game Physics Requirement)
	* The starting position on the arena is represented by the image `show-up.png`. (Regular Task 1(i))
	* The end zone on the arena is represented by the image `end-zone.png`. (Regular Task 1(i))
	* A resource count of *???* (referred to as *Resource Pool*) is displayed.
1. Left-click and hold on either the `Basic Tower`, `Ice Tower`, `Catapult` or `Laser Tower` button.
1. Continue to hold left-click and move the mouse pointer around the arena. From here onwards, while this action is being done, thet game is asid to be in *Tower Placing Mode*. Verify the behaviour of *Tower Placing Mode*, which are as follows:
	* A sihouette (referred to as *Tower Sihouette*) is displayed. The *Tower Sihouette* is represented by an image according to the tower type: (Regular Task 1(ii)(a))
		* Basic Tower - `/src/main/resources/basicTower.png`
		* Ice Tower - `/src/main/resources/iceTower.png`
		* Catapult - `/src/main/resources/catapult.png`
		* Laser Tower - `/src/main/resources/LaserTower.png`
	* The *Tower Sihouette* is located within, but does not exceed, the grid in which the mouse pointer is located (referred to as *The Grid*). (Game Physics Requirement)
	* If any one of the following is satisfied, *The Grid* glows red, indicating that the tower cannot be built there, i.e. *The Grid* is invalid. (Honors Task 1(i))
		* *The Grid* is within the starting position.
		* *The Grid* is within the end-zone.
		* *The Grid* contains at least one tower.
		* *The Grid* disconnects the starting position from the end-zone, i.e. a path can no longer be found between the two.
		* The *Resource Pool* is lower than the tower's cost.
		* *The Grid* contains at least one monster.
		* *The Grid* prevents at least one monster from reaching the end-zone by completely blocking its path.
	* Otherwise, *The Grid* glows green, indicating that the tower can be built there, i.e. *The Grid* is valid.
1. Move the mouse pointer to a valid grid before releasing left-click. Verify the behaviour of attempting to build a tower in a valid grid, which are as follows:
	* The tower is built, i.e. a solid, permament version of the *Tower Sihouette* exactly replaces the *Tower Sihouette*, and begins to exist on the arena. (Regular Task 1(ii))
	* The cost of the tower is deducted from the *Resource Pool*. (Regular Task 1(ii)(b))
	* *Tower Placing Mode* is exited.
1. Enter *Tower Placing Mode* again, but move the mouse pointer over a tower. Verify that *The Grid* glows red.
1. Verify the behaviour of attempting to build a tower in an invalid grid for following conditions:
	* *The Grid* is within the starting position.
	* *The Grid* is within the end-zone.
	* *The Grid* contains at least one tower.
1. The behaviour should be as follows:
	* The tower is not built.
	* *Tower Placing Mode* is exited.
1. Enter *Tower Placing Mode* and move the mouse pointer outside the arena and release left-click. Verify that the resulting behavior is identical to that of attempting to place a tower in an invalid grid.
1. Enter *Tower Placing Mode* and right-click anywhere. Verify that the resulting behavior is identical to that of attempting to place a tower in an invalid grid.
1. Mouse over the tower previously built. Verify the following:
	* The tower's *Tower Information* is displayed on the graphic interface. (Regular Task 1(ii)(c))
	* The pixels within the tower's range is shaded. (Regular Task 1(ii)(c)(a))
1. Move the mouse pointer away from the above tower. Verify that the pixels within the tower's range are no longer shaded. (Regular Task 1(ii)(c)(a)(a))
1. Build a few more towers, but this time, try to enclose the starting area with the towers. Upon attempting to place the last tower that finishes the enclosure, verify the behaviour of attempting to build a tower in an invalid grid for the following condition:
	* *The Grid* disconnects the starting position from the end-zone, i.e. a path can no longer be found between the two.
1. Build even more towers until the *Resource Pool* is lower than the cost of any one tower. Attempt to build a tower with cost greater than the current *Resource Pool*. Verify the behaviour of attempting to build a tower in an invalid grid for the following condition:
	* The *Resource Pool* is lower than the tower's cost.
1. Also verify:
	* A dialog box appears which prompts that there is insufficient resources to build the tower. (Regular Task 1(ii)(d))
1. Mouse over any one of the towers previously built, and then left-click on it. Verify the following:
	* Two buttons `destroy the tower` and `upgrade` appear near the *Tower Information*. (Regular Task 1(ii)(e))
1. Right-click anywhere, including the buttons and on the tower itself. Verify the following:
	* The *Tower Information* and two buttons disappear.
1. Exit the mouse pointer from the tower and left-click on it again.
1. Left-click on the `destroy the tower` button. Verify the following:
	* The tower is destroyed, i.e. it disappears and no longer exists in the arena. (Regular Task 1(ii)(e)(a))
	* The *Tower Information* and two buttons disappear.
	* A portion of the tower cost is refunded to the *Resource Pool*.
1. Destroy more towers until current *Resource Pool* is greater than the upgrade cost of any one tower.
1. Left-click on a tower with upgrade cost lower than current *Resource Pool*. (Regular Task 1(ii)(e)(b))
	* The tower is upgraded, i.e. its stats are improved.
	* The upgrade cost is deducted from the *Resource Pool*.
	* The line `<tower> is being upgraded`, where `<tower>` is the name of the tower, is printed on the console.
1. Build towers until current *Resource Pool* is lower than the upgrade cost of any one tower.
1. Left-click on a tower with upgrade cost greater than current *Resource Pool*.
1. Now left-click on the `upgrade` button. Verify the following: (Regular Task 1(ii)(e)(b)(a))
	* The tower remains unupgraded, and the *Resource Pool* remains unchanged.
	* The line `not enough resource to upgrade <tower>`, where `<tower>` is the name of the tower, is printed on the console.
1. Click the `Play` button to start the game in `play` mode. Verify the following:
	* The game is run in real-time. (Game Physics Requirement)
	* Monsters begin to generate at the starting position. For every monster generated, the line `<type>:<HP> generated`, where `<type>` and `<HP>` is the type and HP of the monster respectively, is printed on the console. (Regular Task 1(iii)(a))
	* Whenever a tower attacks an enemy, the line `<tower_type>@(<tower_x>.<tower_y>) -> <monster_type>@(<monster_x>, <monster_y>)`, where `<_type>`, `<_x>` and `<_y>` represent the object's type, object center's horizontal coordinate and object center's vertical coordinate respectively, is printed on the console. (Regular Task 1(iii)(b))
	* The attack is represented in the graphical interface. (Regular Task 1(iii)(b)(a))
1. Verify the behaviour of attempting to build a tower in an invalid grid for the following conditions: (Honors Task 1(ii)(a))
	* *The Grid* is within the starting position.
	* *The Grid* is within the end-zone.
	* *The Grid* contains at least one tower.
	* *The Grid* disconnects the starting position from the end-zone, i.e. a path can no longer be found between the two.
	* *The Grid* contains at least one monster.
	* *The Grid* prevents at least one monster from reaching the end-zone by completely blocking its path.
1. Verify the behaviour of attempting to build a tower in a valid grid several times. (Honors Task 1(ii)(a))
1. Verify the behaviour of upgrading and destroying towers using the same procedures as above.
1. Close the application, and then restart it using the same `.jar` file.
1. Click the `Simulate` button to start the game in `simulate` mode.
1. Separately, attempt to enter *Tower Placing Mode*, upgrade and destroy a tower. Verify that none of these work. (Honors Task 1(ii))
1. Close the application.
### Grade Task 2
1. Start the application using the same `.jar` file as Task 1.
1. Build one of each tower, namely Basic Tower, Ice Tower, Catapult and Laser Tower.
1. Start the game in `play` mode.
1. Verify the following:
	* Each tower shoots at monsters automatically while the Euclidean distance between the center of the grid of where the tower is built and the center of the monster is within the interval defined by the tower's minimum and maximum range. (Regular Task 2(i))
	* Each tower hits the monster that is closest to top-left corner of the end-zone. (Game Physics Requirement)
	* Monsters that are hit take damage equal to the attack power of the tower. (Game Physics Requirement)
1. Verify that the Basic Tower has a minimum range of `0` pixels and a maximum range of `65` pixels. (Regular Task 2(ii))
1. Upgrade the Basic Tower. Verify that its attack power has been increased. (Regular Task 2(ii)(a))
1. Verify that the Ice Tower causes any monster it hits to become noticeably slower. (Regular Task 2(iii))
1. Upgrade the Ice Tower. Verify that the duration of the slow effect has been extended. (Regular Task 2(iii)(a))
1. Verify the following about the Catapult:
	* It has a minimum range of `50` pixels and a maximum range of `150` pixels. (Regular Task 2(iv)(a))
	* Any monster within `25` pixels of the location where the stone lands is hit. (Regular Task 2(iv)(b))
	* After the stone is thrown, the Catapult takes some time to reload. During that time, it cannot fire at any monsters. (Regular Task 2(iv)(c))
1. Upgrade the Catapult. Verify that the time required to reload it has been reduced. (Regular Task 2(iv)(c)(a))
1. Verify the following about the Laser Tower:
	* Every time it fires, the *Resource Pool* is reduced. (Task 2(v)(a))
	* When it fires, a line is drawn from the center of the tower to the monster. The line extends to the edge of the arena. (Task 2(v)(b))
	* Any monster within `3` pixels of the line is hit. (Task 2(v)(b)(a))
1. Upgrade the Laser Tower. Verify that its attack power has been increased. (Regular Task 2(v)(c))
1. Close the application.
1. Open the project using Eclipse IDE and run the Gradle task `PPP` which uses JUnit to test the Catapult attack algorithm. (Whole of Honors Task 2)
1. Close the project.
### Grade Task 3
1. Start the application using the same `.jar` file as Task 1.
1. Start the game in `play` mode.
1. Verify the following:
	* Monsters are generated every fixed period of time at the starting position. (Task 3(i))
	* The generated monsters move towards the end-zone either horizontally or vertically per frame. (Task 3(ii))
	* When any monster reaches the end-zone, the game is over and the line `Gameover` is printed on the console. (Task 3(ii)(a))
	* When the above happens, everything on the arena is frozen and a dialog box informing that the game is over. (Task 3(ii)(a)(a))
1. Save your score. Record down the value of this score.
1. This time, build some towers to defend yourself before starting the game in `play` mode.
1. Verify the following:
	* Monsters never move into a grid containing a tower. (Game Physics Requirement)
	* Each monster is represented by an image: (Task 3(iii))
		* Fox - `/src/main/resources/fox.png`
		* Unicorn - `/src/main/resources/unicorn.png`
		* Penguin -  `/src/main/resources/penguin.png`
	* Whenever you hover over a monster, its health points is temporarily shown on the graphical interface. (Task 3(iv)) The display is dismissed when the mouse pointer exits the monster. (Task 3(iv)(a))
	* Whenever a monster's health points reaches `0`, its image is replaced by `/src/main/resources/collision.png`. (Task 3(iii)(a)) After a short moment, it is removed from the arena. (Task 3(iii)(a)(a)) When this happens, the *Resource Pool* is increased. (Game Physics Requirement)
	* The Fox monster has higher speed than both Unicorn and Penguin. (Task 3(iii)(v)(a))
	* The Unicorn monster has higher maximum health than both fox and Penguin. (Task 3(iii)(v)(b))
	* The Penguin monster can regenerate health points each time it moves, up to its maximum value. (Task 3(iii)(v)(c))
	* Each generation of monsters become stronger (relative to the same monster type) as time goes by. (Task 3(i)(a))
1. Close the application.	
1. Open the project using Eclipse IDE and run the Gradle task `QQQ` which uses JUnit to test the Fox pathfinding algorithm. (Whole of Honors Task 3)
1. Close the project.

Demonstrate that the following extra tasks have been completed:
1.  Adopt the following technologies into your project. 
	1. Use Java Persistence API or Hibernate Framework to connect to a small database that stores your game data (attributes of Towers/Monsters); (2.5% Group)
	1. Use Java spring framework that allows the game be played in different machines while a server records the top 10 highest scores; (2.5% Group)

### Notes
 * All file paths are relative to the root directory of the project.

## Credits
Skeleton code created by: Dr. Kevin Wang (kevinw@cse.ust.hk)

GitHub Repository: https://github.com/khwang0/2019F-COMP3111/
