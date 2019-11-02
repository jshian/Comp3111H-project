# COMP3111 Project - Tower Defense 
## Team Information
ID: #29 (DarkLight)
 
Course Stream: Honors

Members:
 * Task 1: Ho, Chung Shun (cshoae@connect.ust.hk)
 * Task 2: SHI Jianhua (jshian@connect.ust.hk)
 * Task 3: LEUNG, Tin Long (tlleungac@connect.ust.hk) 

## Grading Process
1. Start the database server.
	1. ...
1. Clone this repository to two different lab machines. They shall be referred to as *Machine A* and *Machine B* respectively.
1. Do the following on Machine A:
	1. Run the file `towerDefence-release.jar` to start the application. Verify the following:
		1. The arena is displayed as a square with side length 480px. (Game Physics Requirement)
		1. The starting position on the arena is represented by the image *???*.
		1. The end zone on the arena is represented by the image *???*. (Regular Task 1(i))
	1. Left-click and hold on any of the buttons that end in `Tower`. Verify the following:
		1. The attack power, building cost and shooting range is displayed. (Game Physics Requirement)
	1. Continue to hold left-click and move the mouse pointer around the arena. Verify the following:
		1. A sihouette is displayed. The sihouette is represented by an image according to the tower type:
		 * Basic Tower - `/src/main/resources/basicTower.png`
		 * Ice Tower - `/src/main/resources/iceTower.png`
		 * Laser Tower - `/src/main/resources/LaserTower.png`
		1. The sihouette is located within, but does not exceed, the grid in which the mouse pointer is located (referred to as *The Grid*). (Game Physics Requirement)
		1. If *The Grid* is inside the starting position or end-zone, it glows red.
		1. Otherwise, *The Grid* glows green.

Demonstrate that the game physics fits the following requirements:
 * A monster is modeled as a point which should be roughly the center of its icon/grid.
 * A monster is always moving from a grid to an adjacent grid either horizontally or vertically.
 * The game must not allow a player to build a tower if the player does not have enough resource to build it.
 * When a monster is killed it must be removed from the arena, a certain amount of resource will be given to the player.
 * Each grid can contain at most one tower. When it contains a tower, it cannot contain any monster.
 * No tower shall be able to move. 
 * All distance used in the game should be referred as Euclidean distance, in pixels (px). 
 * All operations by the player must be done by mouse.
 * A tower cannot attack a monster that is outside its shooting range. When we judge a monster is inside a tower's shooting range, the following distance will be used: the Euclidean distance between the center of the grid of where the tower is built and the center of the monster.
 * The health point (HP) of a monster will be deducted by the attack power of the tower attacking it. When HP is less than or equal to zero a monster is killed. 
 * When there are multiple possible monster a tower can shoot (in range), it should always choose a monster that is nearest to up-left corner of the end-zone ((440, 0) in our demo).
 * It is allowed that multiple towers shoot at a monster at the same time even through only one tower is needed to kill it. This is likely to happen.
 * A monster will never move toward a grid with a tower. If a monster is already on its way to a new grid and a part of the monster body is already insider the grid, no tower will be allowed to be built on this new grid. 
 * After building a tower, all monsters on the map should have at least one valid path move toward the end-zone. Thus, the game must not allow a player to build a tower to trap a monster. 
 * Each grid can contain any number of monsters as long as it does not contain a tower.
 * The game is a time-based game. The button `Next Frame` would NOT be tested in grading. It will be served as a debug button for your own interest. There are two methods to start the game: by clicking `Simulate` or `Play`. In either mode monsters will be automatically generated and the monsters will move towards the end-zone, towers will automatically fire if any monsters are in its shooting distance. In `simulate` mode, player is only allowed to build tower before the simulate button is clicked. Once the button is clicked, the player will no be clicking any button until the game is over. In `play` mode, the player is allowed to build or to upgrade tower when the game is running.
 
 
Demonstrate that the following tasks have been completed:
1. Arena Building (15)
    1. Indicate the grid that a monster show up and the grid representing end-zone with png images. 
    The image must shall be shown at the back when there is a monster on the grid. Create/choose your own image. (1)
    1. Allow all types of towers to be built on all green grids using drag and drop gesture. (1)
        1. When a tower is built, an image of the tower will be placed on the grid. Use the png files 
        provided under `src\main\resources`. (1)
        1. A fixed amount of resource (e.g. money, or you create your own set of resources.) is deducted 
        and correctly display the remaining amount of resource on GUI screen after building
        the tower. (1)
        1. When the mouse pointer is moved over a built tower, all information related to the 
        tower must be displayed on the graphic interface (`System.out.println` is not acceptable)) (1)
            1. Furthermore, the pixels of the area that are inside the tower's range are shaded (you might use a circle UI). (1)
                1. Furthermore, when the mouse is moved away from the tower, the shaded area will be restored (you are not allowed to dismiss the information/shaded pixel by clicking button, e.g. click a message box). (1)
        1. When there is not enough resource to build a tower, the program should prompt a dialog box to warn the player (1)
        1. When a tower is clicked, the player will be provided with two options: `destroy the tower` and `upgrade`. (1)
            1. Furthermore, when `destroy the tower` is selected, the tower will be destroyed. (1)
            1. Furthermore, when `upgrade` is selected, and in case there is enough resource (you can determine the resources needed) to upgrade, it will invoke the function that corresponding to 
            upgrading the tower and a line `XXX tower is being upgraded` is printed on the console. (1)
                1. Furthermore, in case there isn't enough resource to upgrade the tower, the upgrade will be aborted and a line `not enough resource to upgrade XXX tower` is printed on the console. (1)
    1.  Logging the following information using `System.out.println`:
        1. When a monster is generated. Log its type and HP in the format `<type>:<HP> generated` (1)
        1. When a monster is attacked. Log the type and position tower attacks it and the position of the monster in the format `<tower_type>@(<x>.<y>) -> <monster_type>@(<x>, <y>)` (1)
            1. Furthermore, represent the attack in the GUI so that the monster and the tower involved can be visually identified without reading the log. (1)
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
