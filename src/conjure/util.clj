(ns conjure.util
  (:require [clojure.edn :as edn]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [expound.alpha :as expound]
            [taoensso.timbre :as log]
            [camel-snake-kebab.core :as csk]))

(defn sentence [parts]
  (str/join " " parts))

(defn error [& parts]
  (let [msg (sentence parts)]
    (doseq [line (str/split msg #"\n")]
      (log/error line))
    (binding [*out* *err*]
      (println msg))))

(defn parse-user-edn [spec src]
  (let [value (edn/read-string src)]
    (if (s/valid? spec value)
      value
      (error (expound/expound-str spec value)))))

(defn env
  ([spec k] (some->> (env k) (parse-user-edn spec)))
  ([k] (System/getenv (csk/->SCREAMING_SNAKE_CASE (str "conjure-" (name k))))))
