(ns circleseq.core
  (:require [clojure.browser.repl]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs-http.client :as http :refer [get]]
            [om-tools.core :refer-macros [defcomponent]]
            [sablono.core :as html :refer-macros [html]]
            [figwheel.client :as fw :include-macros true]))

(enable-console-print!)

(defn gen-cells [y]
  (vec (take 32 (map (fn [x] {:y y :x x :io (rand-nth [0 1])}) (vec (range 32))))))

(def app-state (atom {:rows 8
                      :steps 32
                      :matrix [{:step 0 :sequ (gen-cells 0) :length 32 :name "BD" :midi-note 38}
                               {:step 0 :sequ (gen-cells 1) :length 32 :name "SD" :midi-note 37}
                               {:step 0 :sequ (gen-cells 2) :length 32 :name "MT" :midi-note 40}
                               ]}))

(defn advance-step [grid]
  (vec (for [row grid]
    (if (< (:step row) (- (:length row) 1))
      (update-in row [:step] inc)
      (assoc-in row [:step] 0)))))

(defn trigger [grid]
  (doseq [x (range (count grid))]
    (let [row (-> grid (nth x))
          cell (-> row :sequ (nth (:step row)))]
         (if (> (:io cell) 0)
           (get (str "http://localhost:8081/noteon/" (:y cell)))))))

(defcomponent checkbox [{:keys [x y io] :as app} owner opts]
  (render-state [_ state]
    (let [panel-size 60
          transform  (str "translateY(" (* 140 y) "px) "
                          "rotateY(" (* (/ 360 (:length opts)) x) "deg) translateZ(200px)")
          style {:transform transform}]
      (html [:input {:className (str "" (if (:step app) "step")) :type "checkbox" :style style :checked (not (zero? io)) :on-click (fn [] (om/transact! app #(assoc % :io 1)))}]))))

(defcomponent row [{:keys [step sequ length]} owner]
  (render-state [_ state]
    (let [transform (str "rotateY(" (* (/ 360 length) step) "deg) "
                         "translateZ(-200px)")]
      (html [:div.carousel {:style {:transform transform}}
            (om/build-all checkbox (map #(if (= step (:x %)) (assoc % :step true) %) sequ) {:opts {:length length :step step}})]))))

(defcomponent main [app owner]
  (did-mount [_]
    (js/setInterval (fn []
      (trigger (:matrix @app))
      (om/transact! app #(update-in % [:matrix ] advance-step))) 400))
  (render [_]
    (html [:div.container
      (om/build-all row (:matrix app) nil)])))

(om/root
  main
  app-state
  {:target (. js/document (getElementById "app"))})

(fw/watch-and-reload
  :websocket-url   "ws://localhost:3449/figwheel-ws"
  :jsload-callback (fn [] (print "reloaded")))
