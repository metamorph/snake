(ns snake.ui
  (:require [quil
             [core :as q]
             [middleware :as m]]))

(defn initialize [])
(defn draw [state])
(defn next-state [state])

(q/defsketch snake-game
  :setup #'initialize
  :draw #'draw
  :update #'next-state
  :size [100 100]
  :middleware [m/fun-mode])
