(ns github-omnibar.core
  (:use [clj-fuzzy.metrics :only [tversky]])
  (:require [reagent.core :as r :refer [atom cursor]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))
    


(defn fetch-hotkeys []
  (.toArray (.map (js/$ "[data-hotkey]")
                                    (fn [idx el]
                                      {:type :hot-key
                                       :hotkey (.getAttribute el "data-hotkey")
                                       :label (or (.getAttribute el "aria-label") (.-textContent el))}))))
(defonce omni-data
  (r/atom {:actions (fetch-hotkeys)
           :search ""
           :highlighted 0
           :display false
           }))

(defn fetch-repos! []
  (.get js/$ "/dashboard/ajax_your_repos"
        (fn [el]
          (swap! omni-data
                 update-in [:actions]
                 #(concat % (-> (js/$ el)
                               (.filter (fn [_ node] (= "LI" (.-nodeName node))))
                               (.map (fn [_ li]
                                       (let [a (.find (js/$ li) "a.mini-repo-list-item")
                                             span (.find a "span.repo")
                                             svg (.find a "svg")]
                                         {:type (keyword (.replace (.-className li) " " "-"))
                                          :href (.attr a "href")
                                          :label (.text span)
                                          :svg (.-outerHTML (.get svg 0))})))
                               .toArray))))))
(fetch-repos!)

(defonce item-height 32.6)
;; ---------------------
;; Actions
(defn- mod-actions [idx]
  (mod idx (count (:actions @omni-data))))

(def container
  (memoize #(js/$ "#omnibar .mini-repo-list")))

(defn scroll-to [where]
  (.scrollTop (container) where))

(defn fuzzy-match [data keyword]
  (swap! data update-in [:actions]
         (fn [actions]
           (->> actions
                (map #(assoc % :score (if (empty? keyword) nil (tversky (:label %) keyword))))
                (sort-by :score)
                reverse))))

(defn godown! []
  (swap! omni-data update-in [:highlighted] (comp mod-actions inc))
  (let [idx (:highlighted @omni-data)]
    (scroll-to (- (* item-height idx) (* item-height 8)))))

(defn goup! []
  (swap! omni-data update-in [:highlighted] (comp mod-actions dec))
  (let [idx (:highlighted @omni-data)
        el (js/$ "#omnibar .mini-repo-list")]
    (scroll-to (* item-height idx))))

(defn reset-highlight! []
  (swap! omni-data assoc :highlighted 0)
  (scroll-to 0))

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
                              ;; :on-blur #(exit!)
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
                       [:li.source {:key idx
                                    :class (if (= idx highlighted) "highlighted")}
                        [:a.mini-repo-list-item.css-truncate
                         {:on-click #(action! act)}
                         [:span {:dangerouslySetInnerHTML {:__html (:svg act)}}]
                         [:span.repo-and-owner>span.repo (:label act)]
                         (if-let [hotkey (:hotkey act)] [:span.stars>kbd hotkey])]])
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
