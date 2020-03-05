(ns textdb.manip
  (:require [clojure.string :as str]
            [clojure.pprint :as p]
            [clojure.java.shell :as shell :only sh]
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


(defn txtfile-strs-only-s   ; was 'only-txtstrings-s'
  "filters out all strings that do not end with either '.txt' or '.md'"
  [fname-s]
  (filter #(or (str/ends-with? % ".txt") (str/ends-with? % ".md")) fname-s)
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
    (hash-map :id id, :fname-text (vector fname text)) 
  )
)

(defn smap-fname
  ""
  [slip-map]
  ; --------------------------------------- 
  (first (:fname-text (first slip-map)))
)

(defn smap-text
  ""
  [slip-map]
  ; --------------------------------------- 
  (second (:fname-text (first slip-map)))
)

; *********************************************
; *                                           *
; * NOTE: requires dir-path to work correctly *
; *                                           *
; *********************************************
(defn slips-db
  "creates a seq containing one map for each slip in the specified directory"
  ; create maps of {key=id, value=[fname-id, slip-contents]}
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
