(ns snake.ui
  (:require [quil
             [core :as q]
             [middleware :as m]]
            [snake.core :refer :all]))

(def ^:dynamic *cell-size* 10)

(defn initialize []
  (q/frame-rate 20)
  (assoc
   (make-state (/ (q/width) *cell-size*)
               (/ (q/height) *cell-size*))
   :running? false))

(defn draw-at [x y]
  (q/rect (* x *cell-size*)
          (* y *cell-size*)
          *cell-size* *cell-size*))
(defn draw-circle-at [x y]
  (q/ellipse-mode :corner)
  (q/ellipse (* x *cell-size*)
             (* y *cell-size*)
             *cell-size* *cell-size*))

(defn draw [state]
  (q/clear)
  (if (:dead? state)
    (q/background 127 10 10)
    (q/background 255 255 255))
  (let [[head & tail] (:body state)
        apples (:apples state)]
    (q/fill 127 127 200)
    (apply draw-at head)
    (q/fill 255 127 127)
    (doseq [cell tail] (apply draw-at cell))
    (q/fill 255 0 0)
    (doseq [pos apples] (apply draw-circle-at pos))))

(defn next-state [state]
  (if (and (:running? state) (not (:dead? state)))
    (let [{[head & _] :body
           direction  :direction} state
          next-head               (next-head head direction)
          eat-apple?              (apple-at? state next-head)]
      (if eat-apple?
        (-> (move state true)
            (add-random-apple)
            (update :apples disj next-head))
        (move state false)))
    state))

(defn on-key [state {:keys [key raw-key] :as evt}]
  (let [dorun? (if (= raw-key \space)
                 (not (:running? state))
                 (:running? state))]
    (-> state
        (assoc :event evt)
        (assoc :running? dorun?)
        (turn (:key evt)))))


(defn start-sketch []
  (q/sketch
   :features [:keep-on-top :no-safe-fns]
   :setup #'initialize
   :draw #'draw
   :key-pressed #'on-key
   :update #'next-state
   :size [500 500]
   :middleware [m/fun-mode]))
