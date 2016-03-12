(ns github-omnibar.core
    (:require [reagent.core :as r :refer [atom cursor]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

;; (defonce actions
    
  ;; (.toArray (.map (js/$ "[data-hotkey]") #({:label (.getAttribute %2 "aria-label") :hotkey (.getAttribute %2 "data-hotkey")}))))
  ;; )
(defonce omni-data
  (r/atom {:actions [{:type :hot-key
                      :label "Homepage"
                      :hotkey "g h"}
                     {:type :hot-key
                      :label "Search"
                      :hotkey "s"}]
           :search ""
           :highlighted 0
           }))
(defn- mod-actions [idx] (mod idx (count (:actions @omni-data))))
(defn godown []
  (swap! omni-data update-in [:highlighted] (comp mod-actions inc)))

(defn goup []
  (swap! omni-data update-in [:highlighted] (comp mod-actions dec)))

(defn reset []
  (swap! omni-data update-in [:highlighted] #(identity 0)))

(defmulti action
  (fn [current-action]
    (:type current-action)))

(defmethod action :hot-key [current]
  (let [el (js/$ (str "[data-hotkey='" (:hotkey current) "']"))]
    (if (.is el "input, textarea")
      (.focus el)
      (.click el))))

(defmethod action :repo [current]
  (println "search"))

;; -------------------------
;; Views

(defn home-page []
  (let [actions (cursor omni-data [:actions])
        highlighted (:highlighted @omni-data)
        current-action (nth (:actions @omni-data) (:highlighted @omni-data))]
    [:div.github-omnibar
     [:div.filter-repos.filter-bar
      [:input.filter-input {:type "text"
                            :placeholder "Find ..."
                            :auto-focus "true"
                            :on-key-down #(case (.-which %)
                                            40 (godown)
                                            38 (goup)
                                            13 (action current-action)
                                            (reset))
                                      }]]
     [:ul.mini-repo-list
      (map-indexed (fn [idx action]
                     [:li.source {:key idx :class (if (= idx highlighted) "highlighted")}
                      [:a.mini-repo-list-item.css-truncate
                       [:span.repo-and-owner>span.repo (:label action)]
                       [:span.stars>kbd (:hotkey action)]]])
           @actions)]])
   )

(defn about-page []
  [:div [:h2 "About github-omnibar"]
   [:div>a {:href "/"} "go to the home page"]])

(defn current-page []
  [:div [(session/get :current-page)]])


;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
