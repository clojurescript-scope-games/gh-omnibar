(ns github-omnibar.core
  (:use [clj-fuzzy.metrics :only [tversky]])
  (:require [reagent.core :as r :refer [atom cursor]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))
    

(defonce omni-data
  (r/atom {:actions (.toArray (.map (js/$ "[data-hotkey]")
                                    (fn [idx el]
                                      {:type :hot-key
                                       :hotkey (.getAttribute el "data-hotkey")
                                       :label (or (.getAttribute el "aria-label") (.-textContent el))})))
           :search ""
           :highlighted 0
           :display false
           }))

;; (defn fuzzy-match [data keyword]
;;   (filter #(re-find ))
;;   (tokenize keyword)
;;   )(fz/dice (:label action) keyword)
;; ---------------------
;; Actions
(defn- mod-actions [idx] (mod idx (count (:actions @omni-data))))

(defn fuzzy-match [data keyword]
  (swap! data update-in [:actions]
         (fn [actions]
           (->> actions
                (map #(assoc % :score (if (empty? keyword) nil (tversky (:label %) keyword))))
                (sort-by :score)
                reverse))))

(defn godown! []
  (swap! omni-data update-in [:highlighted] (comp mod-actions inc)))

(defn goup! []
  (swap! omni-data update-in [:highlighted] (comp mod-actions dec)))

(defn reset-highlight! []
  (swap! omni-data assoc :highlighted 0))

(defn exit! []
  (swap! omni-data assoc :display false))

(defmulti action!
  (fn [current-action]
    (or (:type current-action) :exit)))

(defmethod action! :hot-key [current]
  (let [el (js/$ (str "[data-hotkey='" (:hotkey current) "']"))]
    (if (.is el "input, textarea")
      (.focus el)
      (.each el #((.click %2)))))
  (exit!))

(defmethod action! :repo [current]
  (println "search"))

(defmethod action! :exit [_]
  (exit!))

;; -------------------------
;; Views
(defn omnibar []
  (if (:display @omni-data)
    (let [actions (cursor omni-data [:actions])
          highlighted (:highlighted @omni-data)
          current-action (nth (:actions @omni-data) (:highlighted @omni-data))]
      [:div.github-omnibar
       [:div.filter-repos.filter-bar
        [:input.filter-input {:type "text"
                              :placeholder "Find ..."
                              :auto-focus "true"
                              :on-blur #(exit!)
                              :on-change (fn [e]
                                           (fuzzy-match omni-data (-> e .-target .-value)))
                              :on-key-down #(let [key (.-which %)
                                                  ctrl (.-ctrlKey %)]
                                              (cond
                                                (= key 40) (godown!)
                                                (and (= key 78) ctrl) (godown!)
                                                (= key 38) (goup!)
                                                (and (= key 80) ctrl) (goup!)
                                                (= key 13) (do (.preventDefault %)
                                                               (action! current-action))
                                                (= key 27) (exit!)
                                                (and (= key 71) ctrl) (exit!)
                                                (and (< key 90) (> key 48)) (reset-highlight!))
                                              )}]]
       [:ul.mini-repo-list
        (map-indexed (fn [idx act]
                       [:li.source {:key idx :class (if (= idx highlighted) "highlighted")}
                        [:a.mini-repo-list-item.css-truncate
                         {:on-click #(action! act)}
                         [:span.repo-and-owner>span.repo (:label act)]
                         [:span.stars>kbd (:hotkey act)]]])
                     (filter #(not= (get % :score 1) 0) @actions))]]))
   )

;; -------------------------
;; Initialize omnibar
(defn mount-root []
  (r/render [omnibar] (.getElementById js/document "omnibar")))


  (.append (js/$ "body") (js/$ "<div id=omnibar></div>"))
  (.keydown (js/$ js/document) (fn [e]
                                 (if (and (= 80 (.-which e)) (= "BODY" (.-nodeName (.-target e))))
                                   (swap! omni-data assoc :display true))))
  (mount-root)
