(ns textdb.manip-test
  (:require [clojure.test :refer :all]
            [textdb.manip :refer :all]
            [clojure.string :refer [ends-with? split trimr join]]
  )
)  

            
(def curr-txts-path-prefix
"/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/")
    
(def curr-txts-path3 (str curr-txts-path-prefix "TESTSUITE" "/"))

(def fnames3 (txtfile-strs-only-s curr-txts-path3))

(println "filenames, from txtfile-strs-only-s: " fnames3)

(def set-expected-fnames3 #{"202003061142 'knowing what nobody knows' is an advantage.md" "202003141104 1 line w-CR.md" "202003141105 3 lines.md" "202003141107 2 lines.md" "202003141109 1 line.md" "202003141108 no lines.md" ".md"})

(deftest fname-tests3
  (is (= (set (id-s fnames3)) #{"202003061142" "202003141104" "202003141105" "202003141107" "202003141108" "202003141109" ""}) "id from fname")
  (is (= (set (txtfile-strs-only-s curr-txts-path3)) (set set-expected-fnames3)) "getting fnames from dir")
)

; -------------------------------------------



