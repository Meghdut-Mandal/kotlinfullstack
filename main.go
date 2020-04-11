package main

import (
	"encoding/json"
	"fmt"
	"github.com/gorilla/mux"
	"io"
	"log"
	"net/http"
	"os"
)

type stringResopnce struct {
	StatusCode int    `json:"statusCode"`
	Message    string `json:"message"`
}

func UploadFile(w http.ResponseWriter, r *http.Request) {
	file, _, err := r.FormFile("uploadFile")
	uploadID := r.FormValue("uploadid")
	if err != nil {
		panic(err)
	}
	defer file.Close()

	f, err := os.OpenFile("uploads/"+uploadID+".pdf", os.O_WRONLY|os.O_CREATE, 0666)
	if err != nil {
		panic(err)
	}
	defer f.Close()
	m := stringResopnce{200, "Uploaded Successfully " + uploadID}
	b, _ := json.Marshal(m)
	w.Write(b)
	_, _ = io.Copy(f, file)
	parent := os.Args[1]
	_, err = http.Get(parent + "/teacher/start/" + uploadID)
	if err != nil {
		//fmt.Println()
	} else {
		fmt.Println(err)
	}

}

func homeLink(w http.ResponseWriter, r *http.Request) {
	_, _ = fmt.Fprintf(w, "Welcome home!")
}

func main() {
	router := mux.NewRouter().StrictSlash(true)
	router.HandleFunc("/", homeLink)
	router.HandleFunc("/upload", UploadFile).Methods("POST")
	log.Fatal(http.ListenAndServe(":8080", router))
}
