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
  (q/fill 255 255 255)
  (doseq [cell (:body state)] (apply draw-at cell))
  (q/fill 0 0 0)
  (q/text (str state) 50 50)
  (q/text (str (:event state)) 80 80))

(defn next-state [state]
  (if (:running? state)
    (move state)
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
