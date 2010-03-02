(ns test
  (:use org.clojars.maoe.funky)
  (:use clojure.test))

(deftest test-fnk
  (let [f (fnk [a :b "b" & c]
            (list a b c))]
    (is (f 0)        '(0 "b"))
    (is (f 0 1)      '(0 "b" (1)))
    (is (f 0 1 2)    '(0 "b" (1 2)))
    (is (f 0 :b 0)   '(0 0))
    (is (f 0 :b 0 0) '(0 0 (0)))))
