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

(def expected-fnames-ts 
  [
  "202003061142 'knowing what nobody knows' is an advantage.md" 
  "202003141104 1 line w-CR.md" 
  "202003141105 3 lines.md"
  "202003141107 2 lines.md" 
  "202003141110 1 line no-CR.md" 
  "202003141108 no lines.md" 
;  ".md"
  ]
 )

(def expected-fname-text-ts
  [
    ["202003061142 'knowing what nobody knows' is an advantage.md" "202003061142 'knowing what nobody knows' is an advantage.md\nBrian Glazer; a thousand people didn't think that making a movie about a mermaid in love would work. He felt that was 'knowing what nobody knows'.\n\nDid he see that as a market opportunity? A sign he was more creative than them? Was it another way of saying that he belived in the idea?\n\nDid he use this thought to persevere?\n\n\n\n"]
    ["202003141107 2 lines.md" "202003141107 2 lines.md\nthis is line 1\nline 2 ends with a newline\n"] 
    ["202003141105 3 lines.md" "202003141105 3 lines.md\nline 1\nline 2\nline 3, ends with newline\n"] 
    ["202003141104 1 line w-CR.md" "202003141104 1 line w-CR.md\nthis is one line of text, newline at end\n"] 
    ["202003141110 1 line no-CR.md" "202003141110 1 line no-CR.md\nthis is one line of text, no newline at end"]
    ["202003141108 no lines.md" "202003141108 no lines.md\n"]
  ]
)

(deftest fname-tests-ts
  ; Are the ids in the list of filenames the same as the manually created set?
  (is (= (set (id-s fnames-ts)) 
         #{
           "202003061142" 
           "202003141104" 
           "202003141110" 
           "202003141105" 
           "202003141107" 
           "202003141108" 
         ;  "202003141109" 
         ;  nil
           }
      ) 
    "Are the ids consistent?"
  )
  ; Are the filenames (gotten from files in dir) same as the expected list?
  (is (= (set (txtfile-fnames-s curr-txts-path-ts))
         (set expected-fnames-ts)
      ) 
      "Are the filenames consistent?"
  )
  ; Does update-slip-map-v add the filename to the top of the slip's text?
  (is (= (set (munge-thinking-box curr-txts-path-ts update-slip-map-v))
         (set expected-fname-text-ts)
      )
      "Does update-slip-map-v function work?"
  )
)

; -------------------------------------------
