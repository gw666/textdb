(ns textdb.manip-test
  (:require [clojure.test :refer :all]
            [textdb.manip :refer :all]))
            
(def curr-txts-path-prefix
"/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/")
    
(def curr-txts-path3 (str curr-txts-path-prefix "TEST3" "/"))

(def fnames3 (text-fnames-s curr-txts-path3))

(def set-expected-fnames3 #{"201909101111 learning and curiosity are both virtuous positive feedback loops.txt" "201910211245 QUO on noticing & how to start.txt" "201910211240 your brain connects millions of ideas, becomes your worldview.txt"})

(deftest fname-tests3
  (is (= (set (id-s fnames3)) #{"201909101111" "201910211245" "201910211240"}) "id from fname")
  (is (= (set (text-fnames-s curr-txts-path3)) (set set-expected-fnames3)) "getting fnames from dir")
)

; -------------------------------------------

(def curr-txts-path1 (str curr-txts-path-prefix "TEST1" "/"))
(def fnames1 (text-fnames-s curr-txts-path1))
(def set-expected-fnames1 #{"201910211245 QUO on noticing & how to start.txt"})

(deftest fname-tests1
  (is (= (set (id-s fnames1)) #{"201910211245"}) "id from fname")
  (is (= (set (text-fnames-s curr-txts-path1)) (set set-expected-fnames1)) "getting fnames from dir")
)

; -------------------------------------------

(def curr-txts-path0 (str curr-txts-path-prefix "TEST0" "/"))

(def fnames0 (text-fnames-s curr-txts-path0))


(def set-expected-fnames0 #{})

(deftest fname-tests0
  (is (= (set (id-s fnames0)) #{}) "id from fname")
  (is (= (set (text-fnames-s curr-txts-path0)) (set set-expected-fnames0)) "getting fnames from dir")
)

