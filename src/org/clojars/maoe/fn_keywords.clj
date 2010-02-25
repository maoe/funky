(ns org.clojars.maoe.fn-keywords
  (:gen-class))

(defn- take-while-nth [n pred coll]
  (lazy-seq
   (when-let [s (seq coll)]
     (when (pred (first s))
       (concat (take n s)
               (take-while-nth n pred (drop n s)))))))

(defn- drop-while-nth [n pred coll]
  (lazy-seq
   (loop [p pred c coll]
     (when-let [s (seq c)]
       (if (p (first s))
         (recur p (drop n s))
         s)))))

(defn- split-with-nth [n pred coll]
  [(take-while-nth n pred coll)
   (drop-while-nth n pred coll)])

(defmacro defnk [fn-name & fn-tail]
  (let [[args & body]          fn-tail
        [req-args kv-and-rest] (split-with symbol? args)
        [key-vals extras]      (split-with-nth 2 keyword? kv-and-rest)
        syms                   (map #(symbol (name %))
                                    (take-nth 2 key-vals))
        vals                   (take-nth 2 (rest key-vals))
        sym-vals               (apply hash-map (interleave syms vals))
        de-map                 {:keys (vec syms) :or sym-vals}
        extras                 (nth extras 1)]
    `(defn ~fn-name
       [~@req-args & options#]
       (let [[key-vals# extras#] (split-with-nth 2 keyword? options#)
             ~de-map (apply hash-map key-vals#)
             ~extras extras#]
         ~@body))))
