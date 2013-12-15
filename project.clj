(defproject org.clojars.etosch/tryptophan "1.0.0-SNAPSHOT"
  :description "Mechanical Turk API convenience classes and request mangement"
  :dependencies [[org.clojure/clojure "1.5.1"]
  	         [clj-http "0.7.7"]
                 [clj-time "0.6.0"]
                 [commons-codec/commons-codec "1.4"]
                 [org.clojure/data.xml "0.0.7"]
                 ]
  :url "https://github.com/etosch/tryptophan"
  :aot [mturk.core]
  :main mturk.core
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  )
