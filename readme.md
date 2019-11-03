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
### Grade Regular Task 1
1. Do the following on Machine A:
	1. Run the file `towerDefence-release.jar` to start the application. Verify the following:
		* The arena is displayed as a square with side length 480px. (Game Physics Requirement)
		* The starting position on the arena is represented by the image `XXX.png`. (Regular Task 1(i))
		* The end zone on the arena is represented by the image `YYY.png`. (Regular Task 1(i))
		* A resource count of *???* (referred to as *Resource Pool*) is displayed.
	1. Left-click and hold on either the `Basic Tower`, `Ice Tower` or `Laser Tower` button.
	1. Continue to hold left-click and move the mouse pointer around the arena. From here onwards, while this action is being done, thet game is asid to be in *Tower Placing Mode*. Verify the behaviour of *Tower Placing Mode*, which are as follows:
		* A sihouette (referred to as *Tower Sihouette*) is displayed. The *Tower Sihouette* is represented by an image according to the tower type: (Regular Task 1(ii)(a))
		 	* Basic Tower - `/src/main/resources/basicTower.png`
		 	* Ice Tower - `/src/main/resources/iceTower.png`
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
	1. Click the `Play` button. Verify the following:
		* Monsters begin to generate at the starting position.
		* For every monster generated, the line `<type>:<HP> generated`, where `<type>` and `<HP>` is the type and HP of the monster respectively, is printed on the console. (Regular Task 1(iii)(a))
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
	1. Force close the application, and then restart it.
	1. Click the `Simulate` button.
	1. Attempt to enter *Tower Placing Mode*, upgrade and destroy a tower separately. Verify that this does not work. (Honors Task 1(ii))

Demonstrate that the game physics fits the following requirements:
 * A monster is always moving from a grid to an adjacent grid either horizontally or vertically.
 * When a monster is killed it must be removed from the arena, a certain amount of resource will be given to the player.
 * All distance used in the game should be referred as Euclidean distance, in pixels (px). 
 * A tower cannot attack a monster that is outside its shooting range. When we judge a monster is inside a tower's shooting range, the following distance will be used: the Euclidean distance between the center of the grid of where the tower is built and the center of the monster.
 * The health point (HP) of a monster will be deducted by the attack power of the tower attacking it. When HP is less than or equal to zero a monster is killed. 
 * When there are multiple possible monster a tower can shoot (in range), it should always choose a monster that is nearest to up-left corner of the end-zone ((440, 0) in our demo).
 * It is allowed that multiple towers shoot at a monster at the same time even through only one tower is needed to kill it. This is likely to happen.
 * A monster will never move toward a grid with a tower. If a monster is already on its way to a new grid and a part of the monster body is already insider the grid, no tower will be allowed to be built on this new grid. 
 * Each grid can contain any number of monsters as long as it does not contain a tower.
 * The game is a time-based game. The button `Next Frame` would NOT be tested in grading. It will be served as a debug button for your own interest. There are two methods to start the game: by clicking `Simulate` or `Play`. In either mode monsters will be automatically generated and the monsters will move towards the end-zone, towers will automatically fire if any monsters are in its shooting distance. In `simulate` mode, player is only allowed to build tower before the simulate button is clicked. Once the button is clicked, the player will no be clicking any button until the game is over. In `play` mode, the player is allowed to build or to upgrade tower when the game is running.
 
 
Demonstrate that the following tasks have been completed:
1.  Towers (15)
    1.  All towers built in the arena will shoot a monster automatically which is inside its range (unless all towers are impossible to attack, e.g. out of range, in cool down etc). (1)      
    1.  Implement Basic Tower that has a shooting range [0,65] pixels. You can decide the attack power and other parameters of the tower. (1)
        1. Implement the upgrade function of Basic Tower that increase the attack power of the basic Tower. (1)
    1.  Implement Ice Tower that will make monster move slower for a period of time (Take a human noticeable longer time to move without making other monster move slower). You can determine other attributes of Ice Tower. (1)
        1. Implement the upgrade function of Ice Tower that increase the duration of the monster being slowed. (1)
    1.  Implement Catapult that:
        1. it throws a stone (attacks) to a coordinate less than 150 px but more than 50 px away from the center of the Catapult. (1)
        1. All monsters placed at the radius of 25px of where the stone drop receive damage. (2)
        1. After a stone is thrown, the Catapult take some times to reload the stone (cold down). During that period of time the Catapult will not be able to throw a stone again. (1)
            1. Furthermore, implement the upgrade function of Catapult that the reload time/cold down time is shorten. (1)
    1.  Implement Laser Tower that:
        1. it consumes some resources to attack a monster. (1)
        1. draw a line from the center to the tower to the monster and extend beyond until it reach the edge of the Arena. (2)
            1. Furthermore, all monsters on the line or within 3px away from the line will receive damage. (1)
        1. Implement the upgrade function of Laser Tower that increase attack power of the tower. (1)   
    1. *noted: you are allowed to determine the parameters and cost of your towers when it is not specified. For instance, we did not say if an Ice tower will give damage or not and you can decide that.*
1. Monsters (15)
    1.  In every fixed period of time, one or more monsters will be generated in the arena from a fixed grid that the monster shows up. (2)
        1. Furthermore, along the time elapsed the *stronger* the monster will be generated so that the difficulty of the game increase. *Stronger* may refer to more HP, moving faster, or any factors that make the game difficult to play. You can make your own definition of *stronger*. (1)
    1.  Monsters on the arena will move towards the end-zone automatically. (1)
        1. Furthermore, if no tower is built, the monster will be successfully reach the end-zone and cause the game over (player lose). A single line `Gameover` will be printed to console. (1)
            1. Furthermore, if the game is over, both monster generation and tower shooting should stop and a dialog box will pop up to notify the game is over (1)
    1.  Represent a monster with png images (`fox.png`, `unicorn.png`, `penguin.png`) provided under `src\main\resources`. (1)
        1. Furthermore, when a monster is killed (HP reach 0), its image will be replaced by `collision.png`. (1)
            1. Furthermore, the dead monster will be removed from the arena automatically. (1)
    1.  When a mouse pointer moves over a monster, the HP of the monster will be shown on the graphical interface. (1)
        1. Furthermore, when the mouse pointer moves away from the monster, the HP information will be dismissed. (1)
    1.  Implements three types of monsters: Fox, Unicorn, Penguin such that:
        1. Fox move fastest. (1)
        1. Unicorn has more HP than other monsters. (1)
        1. Penguin has can replenish some HP (but not more than its initial value) each time it moves. (2)

Demonstrate that the following extra tasks have been completed:
1.  Arena
    1. Allow player to build towers in all grids unless it violates the rules stipulated in the Game Physics - Rules for H task. (1)
    1. Implement the button `Simulate` according to the game physics. (2)
        1. Furthermore, implement the button `Play` according to the game physics. (2)
1.  Tower
    1. Catapult attacks algorithm. Instead of throwing a stone to a particular monster, the Catapult will throw a stone
    to a coordinate such that 
        1. among all monsters falls into the attack range plus 25px (the stone radius), the one monster which
    is nearest to the end-zone will be attacked; and (1)
        1. the stone should thrown to the coordinate that hits most monsters; (2)
        1. Rule for tie-breaking: If there are two monsters that are both considered nearest 
        to the end-zone, the stone will be thrown towards the one that hits more monsters. If the same number of monsters
        are hit by the stone, choose any monster you wish. (1)
            1. Create a test case in JUnit to show your algorithm. (1)
        1. *Note: a stone can be thrown to a grid that contains a tower if it make sense. The tower will not be destroy because of that.*
1.  Monster
    1. All monster are able to walk towards the end-zone with a shortest path (choose any path if there are two shortest paths). (1)
        1. Furthermore, Fox is a very wise monster that will not simply walk a shortest path. Fox will try to 
        follow a path that receives a minimum number of attacks from towers by assuming that there is no other monster in Arena. (2)
	1. Create a test case in JUnit to show your algorithm. (1)
1.  Adopt the following technologies into your project. 
	1. Use Java Persistence API or Hibernate Framework to connect to a small database that stores your game data (attributes of Towers/Monsters); (2.5% Group)
	1. Use Java spring framework that allows the game be played in different machines while a server records the top 10 highest scores; (2.5% Group)

### Notes
 * All file paths are relative to the root directory of the project.

## Credits
Skeleton code created by: Dr. Kevin Wang (kevinw@cse.ust.hk)

GitHub Repository: https://github.com/khwang0/2019F-COMP3111/
