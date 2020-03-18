(ns textdb.core
  (:require [textdb.manip :as manip]
;            [clj-commons-exec :as exec]
            [clojure.pprint :as p]
  )
  (:gen-class)
)
  
  
(defn -main []

  (println "hell")
  
  (def currtexts-prefix
    "/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/")
    
  (def currtexts (str currtexts-prefix "TEST3" "/"))
  
  (def slips-s (manip/txtfile-fnames-s currtexts))
  ; this is a seq of textfile name strings
  
  (def slips-db1 (manip/slips-db currtexts slips-s))

  
  (println "\n---testing highest functions")
  (println (manip/slip-map currtexts "201910211240 your brain connects millions of ideas, becomes your worldview.txt"))
  (println "\n\n")
  
  (comment
  (println "--doing a Unix 'ls'")
  (println (exec/sh "pwd"))
  (println (exec/sh "ls"))
  )

  (println "found using find-by-id")
  (println (manip/find-by-id slips-db1 "201909101111"))
)

