(ns test
  (:use org.clojars.maoe.funky)
  (:use clojure.test))

(deftest test-fnk
  (let [f (fnk [a] (list a))
        g (fnk [:a 0] (list a))
        h (fnk [a :b "b"] (list a b))
        i (fnk [a :b "b" & c] (list a b c))]
    (are [expr answer] (= expr answer)
         (f 0)        '(0)
         (g)          '(0)
         (g 1)        '(0)
         (g :a 1)     '(1)
         (h 0)        '(0 "b")
         (h 0 :b 1)   '(0 1)
         (i 0)        '(0 "b" ())
         (i 0 1)      '(0 "b" (1))
         (i 0 1 2)    '(0 "b" (1 2))
         (i 0 :b 0)   '(0 0 ())
         (i 0 :b 0 0) '(0 0 (0))
    )))

(deftest test-letfnk
  (letfnk [(f [a] (list a))
           (g [:a 0] (list a))
           (h [a :b "b"] (list a b))
           (i [a :b "b" & c] (list a b c))]
    (are [expr answer] (= expr answer)
         (f 0)        '(0)
         (g)          '(0)
         (g 1)        '(0)
         (g :a 1)     '(1)
         (h 0)        '(0 "b")
         (h 0 :b 1)   '(0 1)
         (i 0)        '(0 "b" ())
         (i 0 1)      '(0 "b" (1))
         (i 0 1 2)    '(0 "b" (1 2))
         (i 0 :b 0)   '(0 0 ())
         (i 0 :b 0 0) '(0 0 (0))
    )))

(defnk f-test [a] (list a))
(defnk g-test [:a 0] (list a))
(defnk h-test [a :b "b"] (list a b))
(defnk i-test [a :b "b" & c] (list a b c))

(deftest test-defnk
  (are [expr answer] (= expr answer)
       (f-test 0)        '(0)
       (g-test)          '(0)
       (g-test 1)        '(0)
       (g-test :a 1)     '(1)
       (h-test 0)        '(0 "b")
       (h-test 0 :b 1)   '(0 1)
       (i-test 0)        '(0 "b" ())
       (i-test 0 1)      '(0 "b" (1))
       (i-test 0 1 2)    '(0 "b" (1 2))
       (i-test 0 :b 0)   '(0 0 ())
       (i-test 0 :b 0 0) '(0 0 (0))))

(defmulti fmulti-test
  (fn [t & _] (class t)))

(defmethodk fmulti-test String
  [_ a :b "b" & c]
  (list String a b c))

(defmethodk fmulti-test Integer
  [_ a :b 1 & c]
  (list Integer a b c))

(deftest test-defmethodk
  (are [expr answer] (= expr answer)
       (fmulti-test "s" 0)        (list String 0 "b" '())
       (fmulti-test "s" 0 1)      (list String 0 "b" '(1))
       (fmulti-test "s" 0 1 2)    (list String 0 "b" '(1 2))
       (fmulti-test "s" 0 :b 0)   (list String 0 0 '())
       (fmulti-test "s" 0 :b 0 0) (list String 0 0 '(0))
       (fmulti-test 0 0)          (list Integer 0 1 '())
       (fmulti-test 0 0 1)        (list Integer 0 1 '(1))
       (fmulti-test 0 0 1 2)      (list Integer 0 1 '(1 2))
       (fmulti-test 0 0 :b 0)     (list Integer 0 0 '())
       (fmulti-test 0 0 :b 0 0)   (list Integer 0 0 '(0))))
