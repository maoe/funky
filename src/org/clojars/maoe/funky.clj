(ns
  #^{:author "Mitsutoshi Aoe"
     :doc "Macros for keyword parameters"}
  org.clojars.maoe.funky)

(defn- take-while-nth
  [n pred coll]
  {:pre [(pos? n)]}
  (lazy-seq
   (when-let [s (seq coll)]
     (when (pred (first s))
       (concat (take n s)
               (take-while-nth n pred (drop n s)))))))

(defn- drop-while-nth
  [n pred coll]
  {:pre [(pos? n)]}
  (lazy-seq
   (loop [p pred c coll]
     (when-let [s (seq c)]
       (if (p (first s))
         (recur p (drop n s))
         s)))))

(defn split-with-nth
  [n pred coll]
  {:pre [(pos? n)]}
  [(take-while-nth n pred coll)
   (drop-while-nth n pred coll)])

(defmacro fnk
  "Same as clojure.core/fn with keyword params."
  [& fn-tail]
  (let [[args & body]            fn-tail
        [req-args rest-args]     (split-with symbol? args)
        [kv-args [_ extra-args]] (split-with-nth 2 keyword? rest-args)
        syms                     (map #(symbol (name %))
                                      (take-nth 2 kv-args))
        vals                     (take-nth 2 (rest kv-args))
        sym-vals                 (apply hash-map (interleave syms vals))
        de-map                   {:keys (vec syms) :or sym-vals}
        extras                   (gensym "extras")]
    `(fn [~@req-args & params#]
       (let [[kv-args# ~extras] (split-with-nth 2 keyword? params#)
             ~de-map (apply hash-map kv-args#)
             ~@(when extra-args
                 `(~extra-args ~extras))]
         ~@body))))

(defmacro defnk
  "Same as clojure.contrib.def/defnk with optional params."
  [fn-name & fn-tail]
  `(def ~fn-name (fnk ~@fn-tail)))

(defmacro defnk-
  "Same as defnk but yielding private def."
  [fn-name & fn-tail]
  (list* `defnk (with-meta fn-name
                  (assoc (meta fn-name) :private true))
         fn-tail))

(defmacro letfnk
  "Same as letfnk with keyword params."
  [fn-specs & body]
  (let [args (mapcat (fn [[fname fargs & fbody]]
                       `(~fname (fnk ~fargs ~@fbody)))
                     fn-specs)]
    `(let [~@args] ~@body)))

(defmacro defmethodk
  "Same as defmethod with keyword params."
  [multifn dispatch-val & fn-tail]
  `(. ~(with-meta multifn
         {:tag 'clojure.lang.MultiFn})
      addMethod ~dispatch-val (fnk ~@fn-tail)))
