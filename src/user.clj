(ns user
  (:require [hyperfiddle.photon :as p]
            [shadow.cljs.devtools.api :as shadow]
            [shadow.cljs.devtools.server :as shadow-server]
            app.core))

(comment
  "startup"
  (main))

(defn main [& args]
  (shadow-server/start!)                                 ; shadow serves nrepl and browser assets including index.html
  (shadow/watch :app)
  (p/start-websocket-server! {:host "localhost" :port 8081}))

