(ns user
  (:require [hyperfiddle.photon :as p]
            app.core))

(defonce reactor nil)

(defn ^:dev/after-load start! []
  (set! reactor (((ns-publics 'app.core) 'app)         ; re-resolve recompiled Photon main
                 #(js/console.log "Reactor success:" %)
                 #(js/console.error "Reactor failure:" %))))

(defn ^:dev/before-load stop! []
  (when reactor (reactor))
  (set! reactor nil))

