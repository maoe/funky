(ns test
  (:use org.clojars.maoe.funky)
  (:use clojure.test))

(deftest test-fnk
  (let [f (fnk [a :b "b" & c]
            (list a b c))]
    (are [expr answer] (= expr answer)
         (f 0)        '(0 "b" ())
         (f 0 1)      '(0 "b" (1))
         (f 0 1 2)    '(0 "b" (1 2))
         (f 0 :b 0)   '(0 0 ())
         (f 0 :b 0 0) '(0 0 (0)))))

(deftest test-letfnk
  (letfnk [(f [a :b "b" & c]
             (list a b c))]
    (are [expr answer] (= expr answer)
         (f 0)        '(0 "b" ())
         (f 0 1)      '(0 "b" (1))
         (f 0 1 2)    '(0 "b" (1 2))
         (f 0 :b 0)   '(0 0 ())
         (f 0 :b 0 0) '(0 0 (0)))))

(defnk f-test [a :b "b" & c]
  (list a b c))

(deftest test-defnk
  (are [expr answer] (= expr answer)
       (f-test 0)        '(0 "b" ())
       (f-test 0 1)      '(0 "b" (1))
       (f-test 0 1 2)    '(0 "b" (1 2))
       (f-test 0 :b 0)   '(0 0 ())
       (f-test 0 :b 0 0) '(0 0 (0))))

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
