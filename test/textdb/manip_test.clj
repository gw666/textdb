(ns textdb.manip-test
  (:require [clojure.test :refer :all]
            [textdb.manip :refer :all]
            [clojure.string :refer [ends-with? split trimr join]]
  )
)  

            
(def curr-txts-path-prefix
"/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/")
    
(def curr-txts-path-ts (str curr-txts-path-prefix "TESTSUITE" "/"))

(def fnames-ts (txtfile-fnames-s curr-txts-path-ts))

(println "filenames, from txtfile-fnames-s: " fnames-ts)

(def set-expected-fnames-ts #{
;  "202003061142 'knowing what nobody knows' is an advantage.md" 
;  "202003141104 1 line w-CR.md" 
  "202003141105 3 lines.md"
  "202003141107 2 lines.md" 
;  "202003141109 1 line.md" 
;  "202003141108 no lines.md" 
;  ".md"
  })

(def set-expected-fname-text-v
  #{["202003141107 2 lines.md" "202003141107 2 lines.md\nthis is line 1\nline 2 ends with a newline\n"] ["202003141105 3 lines.md" "202003141105 3 lines.md\nline 1\nline 2\nline 3, ends with newline\n"]})

(deftest fname-tests-ts
  (is (= (set (id-s fnames-ts)) 
         #{
         ;  "202003061142" 
         ;  "202003141104" 
           "202003141105" 
           "202003141107" 
         ;  "202003141108" 
         ;  "202003141109" 
         ;  nil
           }
      ) 
    "id from fname"
  )
  (is (= (set (txtfile-fnames-s curr-txts-path-ts)) 
         (set set-expected-fnames-ts)
      ) 
      "getting fnames from dir"
  )
  (is (= (set (munge-thinking-box curr-txts-path-ts update-slip-map-v))
          set-expected-fname-text-v
      )
      "adding fname to line 1 of text"
  )
)

; -------------------------------------------



