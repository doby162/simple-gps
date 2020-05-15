# Simple GPS in two lisps

## A stripped down implementation of the General Problem Solver AI 
This project uses a language called Sibilant: https://sibilant.org/
To install Sibilant, run: `npm install -g sibilant`

To run this code, use: `sibilant -x -f simple-gps.sibilant`

In this code puzzle, we process a list of tasks, each of which has some number of preconditions. 

Tasks are arranged such that no action is taken before it's preconditions are met, and all stated conditions are eventually fulfilled, assuming such a combination exists. 

Example:

The following should print:
[ 'go to the store', 'buy groceries', 'make a sandwich' ]

```
(var example-tasks
     [{task "make a sandwich", depends ["buy groceries"]}
      {task "buy groceries", depends ["go to the store"]}
      {task "go to the store", depends []}]) 
      
(console.log (determine-order example-tasks ["make a sandwich"]))
```

Although my implementation doesn't have actions with multiple post-conditions or actions that remove the post conditions of other actions, it does handle actions with an arbitrary number of preconditions.

 The following should also print:
  [ 'go to the store',
    'buy groceries',
    'get soap',
    'turn on sink',
    'wash hands',
    'make a sandwich' ]

```
(var example-tasks2
  [{task "make a sandwich" depends ["wash hands" "buy groceries"]}
   {task "buy groceries" depends ["go to the store"]}
   {task "go to the store" depends []}
   {task "wash hands" depends ["turn on sink" "get soap"]}
   {task "turn on sink" depends []}
   {task "get soap" depends ["go to the store"]}])
   
   (console.log (determine-order example-tasks2 ["make a sandwich"]))
```



##  Explanation of the algorithm

My implementation of simple-gps comes down to a recursive function with three end points.

On the first cycle it reads the precondition  of the goal, finds the action that would satisfy that precondition, and then adds that action to the collection of goals. On the next cycle, it will find the precondition of the new goal, etc. If an action has multiple preconditions, they are solved in a depth first manor, such that one is totally resolved before the other is considered. Recursing deeper is the third endpoint in the find-steps function.

The cycle terminates when an action is reached that has no precondition. When such an action is found, the action is added to the current state and we begin evaluating the next goal. In an example without multiple preconditions, this means moving up one node at a time until we reach the original goal. Terminating a branch is the second endpoint in find-steps.

Finally, the goal is considered resolved when all of it's preconditions are met, and the goal action itself is present in the output. At this point there are no more goals, and the algorithm terminates, using the first endpoint of find-steps. 

