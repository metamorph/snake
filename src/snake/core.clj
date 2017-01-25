(ns snake.core
  (:gen-class))

(def direction->coord {:up [0 -1]
                       :down [0 1]
                       :right [1 0]
                       :left [-1 0]})
(defn turn->direction
  "This is pretty ugly - should be an easier way to do it?"
  [direction turn]
  (case turn
    :left (case direction
            :up :left
            :right :up
            :down :right
            :left :down)
    :right (case direction
             :up :right
             :right :down
             :down :left
             :left :up)))

(defn within-bounds?
  "Check if a coordinate is within bounds."
  [width height [x y :as position]]
  (and (>= x 0)
       (>= y 0)
       (< x width)
       (< y height)))

(defn move
  "Move the snake. If it hits the bounds - declare the state as 'dead'."
  ([state] (move state false))
  ([{:keys [direction body bounds] :as state} grow?]
   (if (:dead? state)
     state
     (let [head           (first body)
          new-head       (mapv + (direction direction->coord) head)
          new-body       (conj body new-head)
          [width height] bounds]
      (if (within-bounds? width height new-head)
        (assoc state :body (drop-last (if grow? 0 1) new-body))
        (assoc state :dead? true))))))

(defn turn
  "Make a turn - set the new direction."
  [{direction :direction :as state} turn]
  (if (#{:left :right} turn)
    (assoc state :direction (turn->direction direction turn))
    state))

(defn make-body
  "Create a snake body with a given size and a facing direction"
  [position size direction]
  (let [tail-direction (map (partial * -1) (direction->coord direction))]
    (take size (iterate (partial mapv + tail-direction) position))))

(defn make-state
  "Create the initial state."
  ([width height position direction size] {:bounds [width height]
                                           :direction direction
                                           :body (make-body position size direction)})
  ([width height] (make-state width height
                              [(quot width 2) (quot height 2)]
                              :up
                              2)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
