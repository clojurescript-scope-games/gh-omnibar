(ns github-omnibar.core
    (:require [reagent.core :as r :refer [atom cursor]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

;; (defonce actions
    
  ;; (.toArray (.map (js/$ "[data-hotkey]") #({:label (.getAttribute %2 "aria-label") :hotkey (.getAttribute %2 "data-hotkey")}))))
  ;; )
(defonce omni-data
  (r/atom {:actions [{:label "Homepage"
                    :hotkey "g h"}
                   {:label "Search"
                    :hotkey "s"}]
         :search ""
         }))
;; -------------------------
;; Views

(defn home-page []
  (let [actions (cursor omni-data [:actions])]
    [:div.github-omnibar
     [:div.filter-repos.filter-bar
      [:input.filter-input {:type "text"
                            :placeholder "Find ..."
                            :autofocus "true"}]]

     [:ul.mini-repo-list
      (for [action @actions]
        [:li.source>a.mini-repo-list-item.css-truncate
         [:span.repo-and-owner>span.repo (:label action)]
         [:span.stars>kbd (:hotkey action)]])]])
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
