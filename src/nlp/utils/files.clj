(ns nlp.utils.files)

(defn read-file-content
  "Takes a file path and returns its text content as a single string.
   Returns nil if the file does not exist."
  [path]
  (try
    (slurp path)
    (catch java.io.FileNotFoundException _
      (println (str "Error: File not found at " path))
      nil)))
