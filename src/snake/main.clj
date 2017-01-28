(ns snake.main
  (:gen-class)
  (:require [snake.ui :as ui]))

(defn -main
  [& args]
  (ui/start-sketch))
