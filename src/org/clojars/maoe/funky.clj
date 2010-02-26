(ns org.clojars.maoe.funky)

(defn- take-while-nth
  [n pred coll]
  (lazy-seq
   (when-let [s (seq coll)]
     (when (pred (first s))
       (concat (take n s)
               (take-while-nth n pred (drop n s)))))))

(defn- drop-while-nth
  [n pred coll]
  (lazy-seq
   (loop [p pred c coll]
     (when-let [s (seq c)]
       (if (p (first s))
         (recur p (drop n s))
         s)))))

(defn- split-with-nth
  [n pred coll]
  [(take-while-nth n pred coll)
   (drop-while-nth n pred coll)])

(defmacro fnk
  [& fn-tail]
  (let [[args & body]          fn-tail
        [req-args kv-and-rest] (split-with symbol? args)
        [key-vals [_ extras]]  (split-with-nth 2 keyword? kv-and-rest)
        syms                   (map #(symbol (name %))
                                    (take-nth 2 key-vals))
        vals                   (take-nth 2 (rest key-vals))
        sym-vals               (apply hash-map (interleave syms vals))
        de-map                 {:keys (vec syms) :or sym-vals}]
    `(fn [~@req-args & options#]
       (let [[key-vals# extras#] (split-with-nth 2 keyword? options#)
             ~de-map (apply hash-map key-vals#)
             ~extras extras#]
         ~@body))))

(defmacro defnk
  [fn-name & fn-tail]
  `(def ~fn-name (fnk ~@fn-tail)))

(defmacro defnk-
  "same as defnk, yielding non-public def"
  [name & decls]
  (list* `defnk (with-meta name (assoc (meta name) :private true)) decls))

(defmacro letfnk
  [fnspecs & body]
  (let [args (mapcat (fn [[fname fargs & fbody]]
                       `(~fname (fnk ~fargs ~@fbody)))
                     fnspecs)]
    `(let [~@args] ~@body)))
