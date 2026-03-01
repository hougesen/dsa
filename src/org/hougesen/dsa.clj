(ns org.hougesen.dsa (:use [clojure.java.shell :only [sh]]))

(defn not-empty? [line] (if (empty? line) false true))

(defn extract-pids
  [lines]
  (filter not-empty?
    (map clojure.string/trim (clojure.string/split-lines lines))))

(defn docker-ps [] (sh "docker" "ps" "--format" "{{.ID}}"))

(defn print-stopping [pid] (println (format "%s: stopping" pid)))

(defn print-docker-stop-result
  [pid result]
  (println (if (= 0 (result :exit))
             (format "%s: stopped\n" pid)
             (format "%s: error stopping\n" pid))))

(defn docker-stop
  [pid]
  (print-stopping pid)
  (let [result (sh "docker" "stop" pid)] (print-docker-stop-result pid result)))

(defn main
  []
  (let [ids (extract-pids ((docker-ps) :out))] (run! docker-stop ids))
  (shutdown-agents))

(main)
