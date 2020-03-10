(ns textdb.manip
  (:require [clojure.string :only [join split trim triml trimr ends-with?]]
;           [clojure.java.shell :as shell :only sh]
            
  )
  (:gen-class)
)
  
  
  
; *******************************************************
; *                                                     *
; * these are the only functions that require the path  *
; * to the directory containing the text files that     *
; * we want to manipulate                               *
; *                                                     *
; * -- dir->allobjs-s: returns all File objects         *
; * -- text-fnames-s: returns all text filenames        *
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


; ******************************************
; *                                        *
; * NOTE: operates on a directory of files *
; *                                        *
; ******************************************
(defn dir->allobjs-s
  "returns a seq of File objects for the given directory"
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



; ******************************************
; *                                        *
; * NOTE: operates on a directory of files *
; *                                        *
; ******************************************
(defn text-fnames-s  ; was 'fname-s'
  "returns seq of all text files in dir"
  [dir-path]
      (-> 
      (dir->allobjs-s dir-path)
      (allobjs->fileobjs-s)
      (fileobjs->strings-s)
      (txtfile-strs-only-s)
      )
)


(defn fname-id
  "derives slip's id from its filename"
  [fname]
; --------------------------------------- 
  (re-find #"^\d+" fname)  
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
       ]
    (hash-map :id id, :fname fname, :text text) 
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

; *********************************************
; *                                           *
; * NOTE: requires dir-path to work correctly *
; *                                           *
; *********************************************
(defn slips-db
  "creates a seq containing one map for each slip in the specified directory"
  ; create maps of {:id=id, :fname=fname, :text=slip-contents}
  [dir-path fname-seq]
  ; --------------------------------------- 
  (map (partial slip-map dir-path) fname-seq)
)

(defn find-by-id
  "given an id value, return the map of the slip that has that value;
   returns nil if nothing found"
  [slips-db id-value]
; NOTE: does *not* return a *seq* containing the map
  (first (filter #(= id-value (% :id)) slips-db))
)

(defn truthy
  "if val=nil or false, returns false; else returns true"
  [val]
  (not (or (nil? val) (false? val)))
)

; ===== being tested!

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


(defn pour
  ;may be wrong approach
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

; (map (partial slip-string before-str between-str after-str ) mydb)

; ===== end





; ===== to build a database using the TEST3 directory =====

  (defn text-all-fnames-s  ; was 'fname-s'
    "returns seq of all text files in dir"
    [dir-path]
        (-> 
        (dir->allobjs-s dir-path)
        (allobjs->fileobjs-s)
        (fileobjs->strings-s)
        )
  )
  

  (def currtexts-prefix
    "/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/")
    
  (def currtexts (str currtexts-prefix "TEST3" "/"))
  
  ; this is a seq of textfile name strings
  (def slip-fnames-s (text-fnames-s currtexts))
  
  (def mydb (slips-db currtexts slip-fnames-s))
  
  (def oneslip (find-by-id mydb "201909101111"))
  
  (def twoslip (find-by-id mydb "201910211245"))
  
 ; (export-to-file "foo.md" oneslip)
 ; (export-to-file "foo.md" twoslip)

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

