This project simulated an elevator which coordinated multiple threads (people) with semaphores.

Person:
1)	49 people are in line at the elevator at the beginning of the simulation (1 thread per person).
2)	Each person begins at floor 1.
3)	Each person randomly picks a floor from 2 to 10.
4)	A person will wait for an elevator to arrive at floor 1.
5)	A person will board the elevator only if there is room.
6)	Once at the destination floor, the person exits the elevator.


Elevator:
1)	There is 1 elevator (1 thread for the elevator).
2)	The elevator can only hold 7 people.
3)	The elevator begins on floor 1.
4)	The elevator leaves after the 7th person enters.

There is only one file needed to run the project, the Project2.java file.
To compile, type "javac Project2.java" into the terminal
Then run by typing "java Project2"

The project should output correctly and meet all requirements specified in the instructions.
