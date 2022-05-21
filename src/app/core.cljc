(ns app.core
  (:require [hyperfiddle.photon :as p]
            [hyperfiddle.photon-dom :as dom]
            [missionary.core :as m])
  (:import (hyperfiddle.photon Pending))
  #?(:cljs (:require-macros app.core)))                     ; forces shadow hot reload to also reload JVM at the same time

(def tree '{:deps     true
            :builds   {:app {:target     :browser
                             :asset-path "/js"
                             :output-dir "resources/public/js"
                             :modules    {:main {:init-fn user/start!
                                                 :entries [user]}}
                             :devtools   {:watch-dir       "resources/public"
                                          :hud             #{:errors :progress}
                                          :ignore-warnings true}}}
            :dev-http {8080 "resources/public"}
            :nrepl    {:port 9001}
            :npm-deps {:install false}})


(p/def View)
(p/def CollView)
(p/def MapEntryView)
(p/def MapView)


(p/defn App
  []
  ;; use dynamic bindings because the compiler doesn't support mutual recursion
  ;; in `p/defn` yet
  (binding [View (p/fn [data]
                   (cond
                     (map? data) (MapView. data)
                     (coll? data) (CollView. data)
                     :else (dom/text (str data))))
            CollView (p/fn [data]
                       (let [[begin end] (cond
                                           (vector? data) "[]"
                                           (set? data) ["#{" "}"]
                                           :else "()")]
                         (dom/ul
                          (dom/attribute "class" "view coll-view")
                          (dom/text begin)
                          (dom/for [x data]
                            (dom/li
                             (dom/attribute "role" "treeitem")
                             (View. x))))
                         (dom/text end)))
            MapEntryView (p/fn [k v]
                           (dom/ul
                            (dom/attribute "class" "view map-entry-view")
                            (dom/li
                             (dom/attribute "role" "treeitem")
                             (View. k))
                            (dom/li
                             (dom/attribute "role" "treeitem")
                             (View. v))))
            MapView (p/fn [data]
                      (dom/ul
                       (dom/attribute "class" "view map-view")
                       (dom/attribute "role" "group")
                       (dom/text "{")
                       (dom/for [e data]
                         (dom/li
                          (dom/attribute "role" "treeitem")
                          (MapEntryView. (key e) (val e))))
                       (dom/text "}")))]
    (dom/div
     (dom/h1 (dom/text "Tree view"))
     (View. {:foo {:bar #{"baz"}}}))))


(def app #?(:cljs (p/client (p/main
                             (binding [dom/parent (dom/by-id "root")]
                               (try
                                 (App.)
                                 (catch Pending _)))))))
