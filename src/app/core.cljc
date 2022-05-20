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


(p/defn MapEntryView [k v]
  (dom/ul
   (dom/li (dom/text (str k)))
   (dom/li (dom/text (str v)))))


(p/defn MapView [data]
  (dom/ul
   (dom/for [[k v] data]
     (dom/li (MapEntryView. k v)))))

(p/defn App []
  (dom/div
   (dom/h1 (dom/text "Tree view"))
   (MapView. {:foo "bar"})))

(def app #?(:cljs (p/client (p/main
                             (binding [dom/parent (dom/by-id "root")]
                               (try
                                 (App.)
                                 (catch Pending _)))))))
