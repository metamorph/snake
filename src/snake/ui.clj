(ns snake.ui
  (:require [quil
             [core :as q]
             [middleware :as m]]
            [snake.core :refer :all]))

(def ^:dynamic *cell-size* 10)

(defn initialize []
  (make-state (/ (q/width) *cell-size*)
              (/ (q/height) *cell-size*)))

(defn draw [state]
  ;; Set the scale to use.
  (q/scale  0.2)
  (q/clear)
  (q/background 255 255 255)
  (let [[x y] (:position state)]
    (q/ellipse ))
  )
(defn next-state [state] state)


(defn start-sketch []
  (q/sketch
   :features [:keep-on-top]
   :setup #'initialize
   :draw #'draw
   :update #'next-state
   :size [500 500]
   :middleware [m/fun-mode]))
