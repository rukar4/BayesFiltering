# Bayes Filtering Programming Assignment

A localizing agent that uses Bayes Filtering to solve a maze. It employs a prediction and sensor model to determine the 
probability of being in a given cell in the world, and uses that information to determine its next best action.

## Context
This is a project that I completed while taking an AI class at Brigham Young University. We were given the setup for the
robot and the world, including visualization about the robot's beliefs and the display of the world. The goal of the 
project was to create an agent that could solve the maze autonomously. We started by implementing a localization 
algorithm that allowed us to determine the robot's location when controlling it manually. Then, we implemented the 
agent's ability to choose actions for itself and solve the maze.

To be specific, these are the methods I implemented in [theRobot.java](/Robot/theRobot.java):

1. updateProbabilities
2. predictionModel
3. getStayProbability
4. sensorModel
5. valueIteration
6. computeExpectedUtility
7. getRewardValue
8. getEV
9. autoExploreAction
10. printMundoArray

I also edited many of the other functions, but these ten comprise the bulk of my efforts.

## How to Run

Run `javac *.java` in the `Robot` and `Server` directories.

After compiling, start up the world by running the following command in the terminal from the `Server` directory:
```
java BayesWorld [world] [motor_probability] [sensor_probability] [known/unknown]
```

Then, start the robot by running the following from the `Robot` directory:
```
java theRobot [manual/automatic] [decisionDelay]
```

For more information on running these commands, review the [BayesFiltering handout](/PA_BayesFilter_Handout.pdf).

The robot may not be tuned to solve every maze with its current reward system. Change the values of the rewards in 
getRewardValue to see how it affects the robot's behavior with different movement and sensor probabilities! (Remember to
recompile the `Robot` directory after updating the code).
