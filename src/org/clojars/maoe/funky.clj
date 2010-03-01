(ns
  #^{:author "Mitsutoshi Aoe"
     :doc "Macros for keyword parameters"}
  org.clojars.maoe.funky)

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

(defn split-with-nth
  [n pred coll]
  [(take-while-nth n pred coll)
   (drop-while-nth n pred coll)])

(defmacro fnk
  "Same as clojure.core/fn with keyword params."
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
             ~@(when extras `(~extras extras#))]
         ~@body))))

(defmacro defnk
  "Same as clojure.contrib.def/defnk with optional params."
  [fn-name & fn-tail]
  `(def ~fn-name (fnk ~@fn-tail)))

(defmacro defnk-
  "Same as defnk but yielding private def."
  [name & decls]
  (list* `defnk (with-meta name (assoc (meta name) :private true)) decls))

(defmacro letfnk
  "Same as letfnk with keyword params."
  [fnspecs & body]
  (let [args (mapcat (fn [[fname fargs & fbody]]
                       `(~fname (fnk ~fargs ~@fbody)))
                     fnspecs)]
    `(let [~@args] ~@body)))

(defmacro defmethodk
  "Same as defmethod with keyword params."
  [multifn dispatch-val & fn-tail]
  `(. ~(with-meta multifn {:tag 'clojure.lang.MultiFn}) addMethod ~dispatch-val (fnk ~@fn-tail)))

