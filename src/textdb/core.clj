(ns textdb.core
  (:require [textdb.manip :as manip]
;            [clj-commons-exec :as exec]
            [clojure.pprint :as p]
  )
  (:gen-class)
)
  
  
(defn -main []

  (println "hello")
  
  (def currtexts-prefix
    "/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/")
    
  (def currtexts (str currtexts-prefix "TESTSUITE" "/"))
  
  (def slips-s (manip/txtfile-fname-s currtexts))
  ; this is a seq of textfile name strings
  
  (def slips-db1 (manip/slips-db currtexts slips-s))

  
  (println "\n---testing highest functions")
  (println (manip/slip-map currtexts "202003061142 'knowing what nobody knows' is an advantage.md"))
  (println "\n\n")
  
  (comment
  ;(println "--doing a Unix 'ls'")
  ;(println (exec/sh "pwd"))
  ;(println (exec/sh "ls"))
  )

  (println "found using find-by-id")
  (println (manip/find-by-id slips-db1 "202003141105"))
)

