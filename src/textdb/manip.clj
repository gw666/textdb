(ns textdb.manip
  (:require
      [clojure.string :refer [ends-with? includes? split trimr join]]
  )
  (:gen-class)
)

; *******************************************************
; *                                                     *
; * NOTE: This project is about making reports from     *
; * different subsets of the primary text-db. However,  *
; * *any* seq of slip-maps is a kind of "text database" *
; * and such "minor" text-dbs will be manipulated (by,  *
; * for example, text-db-report                         *
; *                                                     *
; *******************************************************
  
; *******************************************************
; *                                                     *
; * these are the only functions that require the path  *
; * to the directory containing the text files that     *
; * we want to manipulate                               *
; *                                                     *
; * -- dir->allobjs-s: returns all File objects         *
; * -- txtfile-fname-s: returns all text filenames     *
; * -- slip-map: returns a given text file's contents   *
; *      as a map                                       *
; * -- slips-db: returns a seq of slip-map entries, one *
; *      for each text file in the specified directory  *
; *                                                     *
; *******************************************************

; *******************************************************
; *                                                     *
; * Decoding parameter names:                           *
; *                                                     *
; * --allobjs: all the File objects in a directory      *
; *    (plus one File object for the dir itself)        *
; * --dir-path: pathname to dir holding all the txt     *
; *    files comprising the textdb                      *
; * --fileobjs: File objects representing files         *
; * --fname: a string representing a file name          *
; * --smap: abbreviation of 'slip-map;                  *
; * --strings-s: a seq of (usually) filename strings    *
; * --txtfile: any file ending in '.txt' or 'md'        *
; *                                                     *
; *******************************************************

(defn dir->allobjs-s
  "returns a seq of *all* File objects for the given directory
   (including those in subdirectories)"
  [dir-path]
  
  (file-seq                     ; 2. Return the directory's contents
    (clojure.java.io/file dir-path) ; 1. Get the fileobject of the directory
  )
)
(defn allobjs->fileobjs-s  ; was 'only-files'
    "IN: seq of File objects
     OUT: filtered seq of only those File objects that are
          files (File objs of directories removed)"
  [fileobj-s]
  (filter #(.isFile %) fileobj-s)
)

(defn fileobjs->strings-s  ; was 'names-s'
    "Returns the .getName property of a sequence of files, as seq"
    [fileobjs-s]

  ; gets java.io.File objects for a given path, including directories
  ; and items in subdirectories (also Apple ".DS_Store" files)
  ; -- item 0 represents the directory itself; item 1 is first file, etc.
  
  ; at this point, we stop working with File objects, begin
  ; working with STRINGS that represent these File objects

    (map #(.getName %) fileobjs-s)
)

(defn all-fname-s
  "returns seq of all fnames in dir (as strings)"
  [dir-path]
    (-> 
    (dir->allobjs-s dir-path)
    (allobjs->fileobjs-s)
    (fileobjs->strings-s)
    )
)

(defn txtfile-fname-s
  "filters out all strings that do not end with either .txt or .md"
  [dir-path]
  (filter #(or (ends-with? % ".txt") (ends-with? % ".md")) (all-fname-s dir-path))
)


(defn fname-id
  "derives slip's id from its filename"
  [fname]
; --------------------------------------- 
  (re-find #"^\d{12}" fname)  
)
(defn id-s
  "returns a seq of id's for each filename in fname-s"
  [fname-s]
  (map fname-id fname-s)
)

; *********************************************
; *                                           *
; * NOTE: requires dir-path to work correctly *
; *                                           *
; *********************************************
(defn slip-map   ; aka 'smap'
  "all the data of one slip, as a single map,
   key = id, value = [fname contents-of-slip]"
  [dir-path fname]
  ; --------------------------------------- 
  (let [id     (fname-id fname)
          ;;line 0 = id; line 1-N = the file's contents
        text   (slurp (str dir-path fname))
        text-out (if (empty? text) "\n" text)
       ]
;    (println "id/text: " id "[" text-out "]")
;    (println "Is text empty?: " (empty? text) "\n")
    (hash-map :id id, :fname fname, :text text-out) 
  )
)

(defn smap-fname
  "get filename from slip-map"
  [slip-map]
  ; --------------------------------------- 
  (:fname slip-map)
)
(defn smap-text
  "get file text from slip-map"
  [slip-map]
  ; --------------------------------------- 
  (:text slip-map)
)

(defn slips-db
  "creates a seq containing one map for each slip in the specified directory"
  ; create maps of {:id =id, :fname =fname, :text =contents of slip
  [dir-path fname-seq]
  ; --------------------------------------- 
  (map (partial slip-map dir-path) fname-seq)
)
(defn find-by-id
  "given an id value, return the map of the slip that has that value;
   returns nil if nothing found"
  [slips-db id]
; NOTE: returns map, not *seq* containing the map
  (first (filter #(= id (% :id)) slips-db))
)
(defn find-by-fname
  "given an id value, return the map of the slip that has that value;
   returns nil if nothing found"
  [slips-db fname]
; NOTE: returns map, not *seq* containing the map
  (first (filter #(= fname (% :fname)) slips-db))
)
(defn truthy
  "if val=nil or false, returns false; else returns true"
  [val]
  (not (or (nil? val) (false? val)))
)
(defn export-to-file
  ; may be wrong approach
  "appends fname, contents to export-fname; if export-fname
   does not exist, it is created"
  [export-fname slip-map]
  
  (let [slip-fname   (smap-fname slip-map)
        slip-text    (smap-text  slip-map)
       ]
       (spit export-fname (str slip-fname "\n-----\n\n") :append true)
       (spit export-fname (str slip-text
                           "\n--------------------------------------------\n\n\n"
                           )
                           :append true
       )
  )
)
(defn pour   ;may be wrong approach
  "creates new file, based on parameters; always appends;
   does not depend on 'require, 'refer, 'use, elsewhere
   in code"
  [dir-path fname text-str]
  (let [full-fname (str dir-path fname)]
    (spit full-fname text-str :append true)
  )
)
(defn parent-path
  "return complete path to dir-path's parent (assumes
   that dir-path ends with a '/')"
  [dir-path]
  ; example: "/a/b/c/d" returns "/a/b/c/"
  (let [results (re-find #"^(.*)/.*/$" dir-path)]
    ; append "/" to make it easy to append filename
    ; and have it be a valid absolute path
    (str (nth results 1) "/")
  )
)
(defn smap-string
  "creates a formatted string for the specified slip-map"
  [before-str between-str after-str slip-map]
  (str before-str (smap-fname slip-map) between-str (trimr (smap-text slip-map)) after-str)
)

(defn text-db-report
  "creates a title/contents report for all slip-maps in the text-db"
  [a-text-db before-str between-str after-str]
  
  (let [partial-fcn (partial smap-string before-str between-str after-str)
        single-reports-s (map partial-fcn a-text-db)]
    ; force lazy seq to be realized as a single string
    (apply str single-reports-s)  
  )
)
(defn same-ids?
    "returns true iff two strings begin with same slip id"
    [str1 str2]
    (= (fname-id str1) (fname-id str2))
)


#_(defn chop-text
  "split text into vector of <first line> <rest of text>"
  [text-str]
  
  (let [split-s (split text-str #"\n")
        first-ln (nth split-s 0)
        rest-txt (join "\n" (nthrest split-s 1))
       ]
    (vector first-ln rest-txt)
  )
)


(defn chop-text
  "split text into vector of <first line> <rest of text>"
  [text-str]
  
  ; NOTE: regexes will both return nil unless test-str contains
  ;       at least one "\n"; if there is none, this fcn 
  ;       appends a "\n" to the input string
  
  (let [first-ln (second (re-find #"^(.*?)\n" text-str))
        rest-text (second (re-find #"(?s)^.*?\n(.*)$" text-str))
        text-out (if (includes? rest-text "\n")
                         rest-text
                         (str rest-text "\n")
                 )
        
       ]
    (vector first-ln text-out)
  )
)
(defn test-CR-regex  ; temporarily of use
  [text-str]
  (let [first-ln (second (re-find #"^(.*?)\n" text-str))
        rest-text (second (re-find #"(?s)^.*?\n(.*)$" text-str))
       ]
  (println "text-str  -->" text-str "<--")
  (println "rest-text -->" rest-text "<--")  
  )
)

(defn add-fname-to-slip-text
  "ensures that the body of text always begins w/ the current fname"
  [fname slip-text]
  
  (let [chopped-text (chop-text slip-text)
        line-1 (nth chopped-text 0)
        rest-text   (nth chopped-text 1)
       ]
    (if (same-ids? line-1 fname)
      (str fname "\n" rest-text)  ; if fname is already on line 1
      (str fname "\n" slip-text)  ; if it is *not* on line 1
    )
  )
)

(defn update-slip-map-v
  "creates [fname text] from slip-map, adding fname as needed to the text"
  [slip-map]
  
  (let [fname (slip-map :fname)
        slip-text (slip-map :text)
        text-out (if (includes? slip-text "\n")
                   slip-text
                   (str slip-text "\n"))
       ]
    ; the function could be factored out to enable arbitrary changes
    ; to the slip text
    (vector fname (add-fname-to-slip-text fname text-out))  
  )  
)
(defn usmv-test ;debugging only
  [mytext]
  
  (let [fname (slip-map :fname)
        slip-text (slip-map :text)
        text-out (if (includes? slip-text "\n")
                   slip-text
                   (str slip-text "\n"))
       ]
  (println "slip-text  -->" slip-text "<--")
  (println "slip-text type  -->" (type slip-text) "<--")
  (println "text-out -->" text-out "<--")  
  (println "text-out type -->" (type text-out) "<--")  
  )
)

(defn munge-thinking-box
  "use modification-fcn on all slips to create seq of [fname modified-text]"
  [orig-tbox-p modification-fcn]
  
  (let [all-slips-fname-s (txtfile-fname-s orig-tbox-p)
        orig-textdb (slips-db orig-tbox-p all-slips-fname-s)
       ]
       
;    (println "fnames\n" all-slips-fname-s "\n\n")
;    (println "orig-textdb\n" orig-textdb "\n\n")
    ; result of map is a lazy seq of [filename text] for each slip-map
    ; modification-fcn outputs [fname newtext] for each slip-map in orig-textdb
    (map modification-fcn orig-textdb)
  )
)

(defn spit-fname-text-pair
  "Given [fname text] of one slip, reconstitute the file in directory dir-path"
  [dir-path fname-text-pair]
  (let [slip-fname (nth fname-text-pair 0)
        slip-text  (nth fname-text-pair 1)
        full-fname (str dir-path slip-fname "/")
       ]
    (spit full-fname slip-text)
  )  
)

(defn spit-new-textdb
  "Create, in dest-dir, one slip text-file for each [fname text] pair in fname-text-pair-s"
  [dest-dir fname-text-pair-s]
  
  (map #(spit-fname-text-pair dest-dir %1) fname-text-pair-s)
  
  
  
  
  
  
)
; -------------------------------------------
; begin testing code, using GW-thinking-box
; -------------------------------------------
(def textdb-source-dir "/Users/gr/Dropbox/THINKING-BOXES/GW-thinking-box/")

(def textdb-dest-dir "/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/textdb-NEW/")

; 116 records--correct
(def textdb-fnames (txtfile-fname-s "/Users/gr/Dropbox/THINKING-BOXES/GW-thinking-box/"))

; ditto
(def origfulldb (slips-db textdb-source-dir textdb-fnames))

; 57 records; incorrect; note: # is almost half of true record count
(def mfulldb (munge-thinking-box textdb-source-dir update-slip-map-v))

(spit-new-textdb textdb-dest-dir mfulldb)

;  =====================================================================================
;  =====================================================================================
; -------------------------------------------
; doing munge of munded text (above), using DATA/textdb-NEW2
; -------------------------------------------

; **********************************************
; WARNING: this modifies AFTST to determine whether the munging of an
; already munged textb does not introduce any destructive;
; CHECK the resulting textdb, looking for malformed slip files
;
; WHEN DONE, restore the original version to the REPL
; **********************************************
(defn add-fname-to-slip-text
  "adds a dummy line to the twice-munged textdb"
  [fname slip-text]
  
  (str "=====>> T w I c E d-M u N g E d  T e X t <<=====" "\n" slip-text)
)

(def textdb-source-dir "/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/textdb-NEW/")

(def textdb-dest-dir "/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/textdb-NEW2/")

(def textdb-fnames (txtfile-fname-s "/Users/gr/Dropbox/THINKING-BOXES/GW-thinking-box/"))

(def origfulldb (slips-db textdb-source-dir textdb-fnames))

(def mfulldb (munge-thinking-box textdb-source-dir update-slip-map-v))

(spit-new-textdb textdb-dest-dir mfulldb)

; ------------------- end testing code ------------------------
;  =====================================================================================
;  =====================================================================================


; ===== to build a database using the TESTSUITE directory =====

(def source-path-ts "/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/TESTSUITE/")

(def dest-path-ts "/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/TESTSUITE-NEW/")

; this is a seq of txtfile name strings
(def slip-fnames-s (txtfile-fname-s source-path-ts))
  
(def mydb2 (slips-db source-path-ts slip-fnames-s))

; creates a seq of [fname modified-text] pairs
(def my-fname-text-pairs-ts (munge-thinking-box source-path-ts update-slip-map-v))

; creates a textdb that is the munged version of mydb
(spit-new-textdb dest-path-ts my-fname-text-pairs-ts)




; ===== to build a database using the master thinking-box directory =====

(def source-path-ts "/Users/gr/Dropbox/THINKING-BOXES/GW-thinking-box/")

(def dest-path-ts "/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/textdb-NEW/")

; this is a seq of txtfile name strings
(def slip-fnames-s (txtfile-fname-s source-path-ts))
  
(def mydb (slips-db source-path-ts slip-fnames-s))

; creates a seq of [fname modified-text] pairs
(def my-fname-text-pairs-ts (munge-thinking-box source-path-ts update-slip-map-v))

; creates a textdb that is the munged version of mydb
(spit-new-textdb dest-path-ts my-fname-text-pairs-ts)

; ===== 

(def before-str "\n================================\n")
(def between-str "\n--------------------------------\n")
(def after-str   "\n================================\n\n")

(comment
; to aid in debugging smap-string
(def before-str "BEFORE\n")
(def between-str "\nBETWEEN\n")
(def after-str   "\nAFTER\n\n\n")
)

  
(def junkpath "/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/junk/")
(def jfname "junk2.md")
(pour junkpath jfname "woah\n\n")



; ===== end =====
