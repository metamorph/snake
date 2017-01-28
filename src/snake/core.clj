(ns snake.core
  (:gen-class))

(def direction->coord {:up [0 -1]
                       :down [0 1]
                       :right [1 0]
                       :left [-1 0]})
(defn turn->direction
  "This is pretty ugly - should be an easier way to do it?"
  [direction turn]
  (case [direction turn]
    [:up :left] :left
    [:up :right] :right
    [:down :left] :left
    [:down :right] :right
    [:left :up] :up
    [:left :down] :down
    [:right :down] :down
    [:right :up] :up
    direction))

(defn within-bounds?
  "Check if a coordinate is within bounds."
  [width height [x y :as position]]
  (and (>= x 0)
       (>= y 0)
       (< x width)
       (< y height)))

(defn head-overlaps-body? [[head & body]]
  (not (empty? (filter #(= head %) body))))

(defn alive? [body [width height]]
  (and (within-bounds? width height (first body))
       (not (head-overlaps-body? body))))

(defn next-head [position direction]
  (mapv + (direction direction->coord) position))

(defn move
  "Move the snake. If it hits the bounds - declare the state as 'dead'."
  ([state] (move state false))
  ([{:keys [direction body bounds] :as state} grow?]
   (if (:dead? state)
     state
     (let [head           (first body)
           new-head       (next-head head direction)
           new-body       (conj body new-head)]
       (if (alive? new-body bounds)
         (assoc state :body (drop-last (if grow? 0 1) new-body))
         (assoc state :dead? true))))))

;; -- TODO: Add apples that will disappear after a time. Add a ttl entry to an apple - draw them in changing colors.

(defn add-apple-at [state pos]
  (update state :apples conj pos))

(defn apple-at? [{apples :apples :as state} pos] (some? ((set apples) pos)))
(defn body-at? [{body :body} pos] (some? ((set body) pos)))

(defn add-random-apple [{[width height] :bounds
                         apples          :apples
                         body            :body
                         :as             state}]
  ;; This could be a bug if the entire field is filled with snake or apples.
  (add-apple-at state
                (first (drop-while
                        (fn [pos] (or (apple-at? state pos)
                                     (body-at? state pos)))
                        (repeatedly (fn []
                                      [(rand-int width)
                                       (rand-int height)]))))))

(defn turn
  "Make a turn - set the new direction."
  [{direction :direction :as state} turn]
  (assoc state :direction (turn->direction direction turn)))

(defn make-body
  "Create a snake body with a given size and a facing direction"
  [position size direction]
  (let [tail-direction (map (partial * -1) (direction->coord direction))]
    (take size (iterate (partial mapv + tail-direction) position))))

(defn make-state
  "Create the initial state."
  ([width height position direction size] (add-random-apple
                                           {:bounds    [width height]
                                            :direction direction
                                            :apples #{}
                                            :body      (make-body position size direction)}))
  ([width height] (make-state width height
                              [(quot width 2) (quot height 2)]
                              :up
                              2)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
