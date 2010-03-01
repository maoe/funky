funky
==============

Miscellaneous macros for keyword (and optional) parameter handling in Clojure

Installation
-------------

    (defproject your-project "0.0.1-SNAPSHOT"
      :description "descriptions for your project"
      :dependencies [[org.clojars.maoe/funky "0.0.3"]
                     ...]
      ...)

Usage
-------------

    (ns your-project
      (:use org.clojars.maoe.funky))
     
    ;; defn with keyword params
    (defnk f [x y :z 0 & extras]
      (println x y z extras))
     
    ;; defn- with keyword params
    (defnk- f- [x y :z 0 & extras]
      (println x y z extras))
     
    ;; anonymous function with keyword params
    (def g
      (fnk [x y :z 0 & extras]
        (println x y z extras)))
     
    ;; letfn with keyword params
    (letfn [(h [x y :z 0 & extras]
              (println x y z extras))
            (i [x y :z 0]
              (println x y z extras))]
      (h 1 2 3)
      (i 1 2 :z 3))

    ;; multimethods with keyword params
    (defmulti j (fn [x & _] (class x)))
    
    (defmethodk j String [s :a "zero" :b "one" & extras]
      (println s a b extras))
    
    (defmethodk j Integer [s :a 0 :b 1 & extras]
      (println s a b extras))
