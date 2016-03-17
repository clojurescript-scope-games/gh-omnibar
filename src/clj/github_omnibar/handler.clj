(ns github-omnibar.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [github-omnibar.middleware :refer [wrap-middleware]]
            [environ.core :refer [env]]))

(def mount-target
  [:div.demo
     [:div {:role "banner", :class "header header-logged-in true"}
      [:div {:class "container clearfix"}
       [:a {:data-ga-click "Header, go to dashboard, icon:logo", :aria-label "Homepage", :data-hotkey "g d", :href "https://github.com/", :class "header-logo-invertocat"}
        [:svg {:width "28", :viewbox "0 0 16 16", :version "1.1", :role "img", :height "28", :class "octicon octicon-mark-github", :aria-hidden "true"}
         [:path {:d "M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59 0.4 0.07 0.55-0.17 0.55-0.38 0-0.19-0.01-0.82-0.01-1.49-2.01 0.37-2.53-0.49-2.69-0.94-0.09-0.23-0.48-0.94-0.82-1.13-0.28-0.15-0.68-0.52-0.01-0.53 0.63-0.01 1.08 0.58 1.23 0.82 0.72 1.21 1.87 0.87 2.33 0.66 0.07-0.52 0.28-0.87 0.51-1.07-1.78-0.2-3.64-0.89-3.64-3.95 0-0.87 0.31-1.59 0.82-2.15-0.08-0.2-0.36-1.02 0.08-2.12 0 0 0.67-0.21 2.2 0.82 0.64-0.18 1.32-0.27 2-0.27 0.68 0 1.36 0.09 2 0.27 1.53-1.04 2.2-0.82 2.2-0.82 0.44 1.1 0.16 1.92 0.08 2.12 0.51 0.56 0.82 1.27 0.82 2.15 0 3.07-1.87 3.75-3.65 3.95 0.29 0.25 0.54 0.73 0.54 1.48 0 1.07-0.01 1.93-0.01 2.2 0 0.21 0.15 0.46 0.55 0.38C13.71 14.53 16 11.53 16 8 16 3.58 12.42 0 8 0z"}]]]
       [:div {:role "search", :class "site-search repo-scope js-site-search"}
        [:form {:method "get", :data-repo-search-url "/reagent-project/reagent/search", :data-global-search-url "/search", :class "js-site-search-form", :action "/reagent-project/reagent/search", :accept-charset "UTF-8", :_lpchecked "1"}
         [:div {:style "margin:0;padding:0;display:inline"}
          [:input {:type "hidden", :value "✓", :name "utf8"}]]
         [:label {:class "js-chromeless-input-container form-control awesome-autocomplete"}
          [:div {:class "scope-badge"} "This repository"]
          [:span {:class "twitter-typeahead", :style "position: relative; display: inline-block; direction: ltr;"}
           [:input {:dir "auto", :placeholder "Search", :tabindex "1", :data-repo-scope-placeholder "Search", :autocapitalize "off", :name "q", :type "text", :data-global-scope-placeholder "Search GitHub", :style "position: relative; vertical-align: top;", :spellcheck "false", :class "js-site-search-focus js-site-search-field is-clearable chromeless-input tt-input", :autocomplete "off", :aria-label "Search this repository", :data-hotkey "s"}]]]]]
       [:ul {:role "navigation", :class "header-nav left"}
        [:li {:class "header-nav-item"}
         [:a {:data-selected-links "/pulls /pulls/assigned /pulls/mentioned /pulls", :data-hotkey "g p", :data-ga-click "Header, click, Nav menu - item:pulls context:user", :class "js-selected-navigation-item header-nav-link", :href "/pulls"} "      Pull requests\n"]]
        [:li {:class "header-nav-item"}
         [:a {:data-selected-links "/issues /issues/assigned /issues/mentioned /issues", :data-hotkey "g i", :data-ga-click "Header, click, Nav menu - item:issues context:user", :class "js-selected-navigation-item header-nav-link", :href "/issues"} "      Issues\n"]]
        [:li {:class "header-nav-item"}
         [:a {:data-ga-click "Header, go to gist, text:gist", :href "https://gist.github.com/", :class "header-nav-link"} "Gist"]]]]]])

(def loading-page
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1"}]
    (include-css "/css/github.css")
    (include-css "/css/frameworks.css")
    (include-js "/js/jquery.min.js")
    (include-js "/js/string_score.js")
    (include-css "/css/site.css")
    ]
   [:body
    mount-target
     (include-js "/js/app.js")]))


  (defroutes routes
    (GET "/" [] loading-page)
    (GET "/about" [] loading-page)
    
    (resources "/")
    (not-found "Not Found"))

  (def app (wrap-middleware #'routes))
