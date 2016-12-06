(ns url-shortener.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [ring.util.response :refer [redirect]]))

;; Front page
(defn welcome
  [request]
  {:status 200
     :body "<h1>CS 296-25 Final Project: URL Shortener</h1>  
     <p>Append the URL you want to shorten (without the https://www. part) like the format below</p>
     <p>https://cs296-url-shortener.herokuapp.com/shorten/website_to_be_shortened</p>
     <p>For example, https://cs296-url-shortener.herokuapp.com/shorten/google.com"
   :headers {}})

;; Shorten a requested URL
(defn shorten
  [request]
  (let [url (get-in request [:route-params :url])]

     (def random (rand-int 999999))
     ;; Insert new url
     (let [uri "mongodb://cs296:123456789@ds157247.mlab.com:57247/cs296-url-shortener"
      {:keys [conn db]} (mg/connect-via-uri uri)]
        ;; Handle collision
        (def random (+ (* random 10) (mc/count db "urls" {:shortened random})))
        (mc/insert-and-return db "urls" {:original (clojure.string/join ["http://www." url]) :shortened (str random)}))

    {:status 200
     :body (str "You can now access www." url " via https://cs296-url-shortener.herokuapp.com/" random)
     :headers {}}))

;; Redirect to a website via the shortened URL 
(defn redirectx
  [request]
  (let [url (get-in request [:route-params :url])]
    (let [uri "mongodb://cs296:123456789@ds157247.mlab.com:57247/cs296-url-shortener"
      {:keys [conn db]} (mg/connect-via-uri uri)]
        (def obj (mc/find-one-as-map db "urls" {:shortened url}))
        (def original (get obj :original))
        
        (if (nil? original) {
          :status 404
          :body "404! Please check your URL"} (redirect original))
         )))

;; Every request goes here first
(defroutes app
  (GET "/" [] welcome)
  (GET "/shorten/:url" [] shorten)
  (GET "/:url" [] redirectx)
  (not-found "<h3>404! Please check your URL</h3>"))

;; Main function
(defn -main
  [port-number]
  (jetty/run-jetty app
     {:port (Integer. port-number)}))

;; Reloads code changes via the development profile of Leiningen
(defn -dev-main
  [port-number]
  (jetty/run-jetty (wrap-reload #'app)
     {:port (Integer. port-number)}))