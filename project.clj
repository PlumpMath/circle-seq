(defproject circleseq "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj" "src/cljs"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2311"]
                 [org.clojure/core.async "0.1.338.0-5c5012-alpha"]
                 [ring "1.2.2"]
                 [compojure "1.1.8"]
                 [enlive "1.1.5"]
                 [om "0.7.1"]
                 [prismatic/om-tools "0.3.2"]
                 [sablono "0.2.22"]
                 [cljs-http "0.1.16"]
                 [figwheel "0.1.3-SNAPSHOT"]]

  :profiles {:dev
             {:repl-options {:init-ns circleseq.server}
              :plugins [[com.cemerick/austin "0.1.5-SNAPSHOT"]
                        [lein-cljsbuild "1.0.3"]
                        [lein-figwheel "0.1.2-SNAPSHOT"]]
              :cljsbuild {:builds
                          [{:source-paths ["src/cljs"]
                            :compiler {:output-to     "resources/public/app.js"
                                       :output-dir    "resources/public/out"
                                       :preamble      ["react/react.js"]
                                       ;;:optimizations :simple
                                       :optimizations :none
                                       :pretty-print  true
                                       :source-map    true}}]}
              :figwheel {:http-server-root "public" ;; resources/public
                         :port 3449 }}})
