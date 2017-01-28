(ns snake.ui
  (:require [clojure.java.io :as io]
            [quil
             [core :as q]
             [middleware :as m]]
            [snake.core :refer :all]))

(defn load-clip [resource]
  (let [stream (javax.sound.sampled.AudioSystem/getAudioInputStream (io/resource resource))
        info (javax.sound.sampled.DataLine$Info. javax.sound.sampled.Clip (.getFormat stream))]
    (doto (javax.sound.sampled.AudioSystem/getLine info)
      (.open stream))))

(defn play-clip [clip]
  (doto clip
    (.setFramePosition 0)
    (.start)))

(defn make-sounds [] {:nom [(load-clip "NomNom.wav")
                            (load-clip "NomNom2.wav")
                            (load-clip "NomNom3.wav")]
                      :game-over [(load-clip "GameOver.wav")
                                  (load-clip "GameOver2.wav")
                                  (load-clip "GameOver3.wav")]})

(defn play-sound [{sounds :sounds :as state} sound]
  (let [candidates (sound sounds)
        picked (get candidates (rand-int (count candidates)))]
    (play-clip picked))
  state)

(def ^:dynamic *cell-size* 10)

(defn initialize []
  (q/frame-rate 15)
  (->
   (make-state (/ (q/width) *cell-size*)
               (/ (q/height) *cell-size*))
   (assoc :running? false)
   (assoc :sounds (make-sounds))))

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
    (do
      (q/background 200 100 100)
      (q/fill 0 0 0)
      (q/text-align :center :baseline)
      (q/text "YOU DIED! Press 'R' to restart."
              (quot (q/width) 2)
              (quot (q/height) 2)))
    (q/background 255 255 255))
  (let [[head & tail] (:body state)
        apples (:apples state)]
    (q/fill 0 0 0)
    (q/text-align :left :top)
    (q/text (format "Score: %d" (- (count (:body state)) 2)) 20 20)
    (q/fill 0 0 0)
    (apply draw-at head)
    (q/fill 255 127 127)
    (doseq [cell tail] (apply draw-at cell))
    (q/fill 100 200 100)
    (doseq [pos apples] (apply draw-circle-at pos))))

(defn next-state [state]
  (if (and (:running? state) (not (:dead? state)))
    (let [{[head & _] :body
           direction  :direction} state
          next-head               (next-head head direction)
          eat-apple?              (apple-at? state next-head)]
      (let [new-state (move state eat-apple?)]
        (if (:dead? new-state)

          (play-sound new-state :game-over)

          (if eat-apple?
           (do
             (-> new-state
                 (play-sound :nom)
                 (add-random-apple)
                 (update :apples disj next-head)))

           new-state))))
    state))

(defn on-key [state {:keys [key raw-key] :as evt}]
  (let [dorun? (if (= raw-key \space)
                 (not (:running? state))
                 (:running? state))]
    (if (= :r key)
      (initialize)
      (-> state
          (assoc :event evt)
          (assoc :running? dorun?)
          (turn (:key evt))))))

(defn start-sketch []
  (q/sketch
   :features [:keep-on-top :no-safe-fns]
   :setup #'initialize
   :draw #'draw
   :key-pressed #'on-key
   :update #'next-state
   :size [500 500]
   :middleware [m/fun-mode]))

