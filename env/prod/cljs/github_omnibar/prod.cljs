(ns github-omnibar.prod
  (:require [github-omnibar.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
