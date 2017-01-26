(ns snake.ui
  (:require [quil
             [core :as q]
             [middleware :as m]]
            [snake.core :refer :all]))

(def ^:dynamic *cell-size* 10)

(defn initialize []
  (q/frame-rate 30)
  (assoc
   (make-state (/ (q/width) *cell-size*)
               (/ (q/height) *cell-size*))
   :running? false))

(defn draw-at [x y]
  (q/rect (* x *cell-size*)
          (* y *cell-size*)
          *cell-size* *cell-size*))

(defn draw [state]
  (q/clear)
  (if (:dead? state)
    (q/background 127 10 10)
    (q/background 255 255 255))
  (let [[head & tail] (:body state)]
    (q/fill 200 127 127)
    (apply draw-at head)
    (q/fill 255 127 127)
    (doseq [cell tail] (apply draw-at cell))))

(defn next-state [state]
  (if (:running? state)
    ;; Check if an apple is in our path.
    ;; In that case - remove the apple and tell the snake to grow.
    (move state
          (= (mod (q/frame-count) 60) 0) ;; Make the snake grow every 60th frame.
          )
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
