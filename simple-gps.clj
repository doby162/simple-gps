(defn name->task
  [task-data name]
  (first (filter #(= name (:task %)) task-data)))

(defn member?
  [element collection]
  (not= -1 (.indexOf collection element)))

(defn find-steps
  [task-data goals solution]
  (let [remaining-goals (filter #(not (member? (:task %) solution)) goals)
        current-goal (first remaining-goals)]
  (cond
    (nil? current-goal)
    ; check if we are out of goals, terminating this branch
    solution
    (or (every? #(member? % solution) (:depends current-goal)) (empty? (:depends current-goal)))
    ; check if we can do the current goal action
    (recur task-data goals (conj solution (:task current-goal)))
    :else ; recurse deeper looking for a goal we can accomplish
    (recur task-data (apply conj goals (map #(name->task task-data %) (:depends current-goal))) solution))))

(defn determine-order [task-data goals]
  (find-steps task-data (map #(name->task task-data %) goals) []))

;included test cases
(def example-tasks
  [{:task "make a sandwich" :depends ["buy groceries"]}
   {:task "buy groceries" :depends ["go to the store"]}
   {:task "go to the store" :depends []}])

(assert
  (=
   ["go to the store" "buy groceries" "make a sandwich"]
   (determine-order example-tasks ["make a sandwich"])))
(assert
  (=
   ["go to the store" "buy groceries" "make a sandwich"]
   (determine-order example-tasks ["buy groceries" "make a sandwich"])))
;testing a shorter sequence
(assert
  (=
   ["go to the store" "buy groceries"]
   (determine-order example-tasks ["buy groceries"])))

(def example-tasks2
  [{:task "make a sandwich" :depends ["wash hands" "buy groceries"]}
   {:task "buy groceries" :depends ["go to the store"]}
   {:task "go to the store" :depends []}
   {:task "wash hands" :depends ["turn on sink" "get soap"]}
   {:task "turn on sink" :depends []}
   {:task "get soap" :depends ["go to the store"]}])

;test multiple depends on nodes
(assert
  (=
   ["go to the store" "buy groceries" "get soap" "turn on sink" "wash hands" "make a sandwich"]
   (determine-order example-tasks2 ["make a sandwich"])))

;less interesting tests below

(def example-tasks3
  [{:task "l" :depends ["m"]}
   {:task "m" :depends ["n"]}
   {:task "n" :depends ["o"]}
   {:task "o" :depends ["p"]}
   {:task "p" :depends ["q"]}
   {:task "q" :depends ["r"]}
   {:task "r" :depends ["x"]}
   {:task "x" :depends ["t"]}
   {:task "t" :depends ["u"]}
   {:task "u" :depends ["v"]}
   {:task "v" :depends []}])
;test a longer simple chain
(assert
  (=
   ["v" "u" "t" "x" "r" "q" "p" "o" "n" "m" "l"]
   (determine-order example-tasks3 ["l"])))
(assert
  (=
   ["v"]
   (determine-order example-tasks3 ["v"])))
(assert
  (=
   ["v" "u" "t" "x"]
   (determine-order example-tasks3 ["x"])))

(def example-tasks4
  [{:task "impossible task" :depends "can't be done"}])
(assert
  (=
   []
   (determine-order example-tasks4 ["impossible task"])))

(def circular-tasks
  [{:task "task a" :depends "task b"}
   {:task "task b" :depends "task a"}])
(assert
  (=
   []
   (determine-order circular-tasks ["task a"])))
(println "All tests passed")
