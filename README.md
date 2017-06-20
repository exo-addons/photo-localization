Photo Localization Extension
=======

This extension allows to place on a map geotaggued photos uploaded by users.
One scenario is to create a space dedicated to photo sharing. When a new photo is uploaded, gps data are read and the photo is placed on a map.
![Map](https://raw.githubusercontent.com/exo-addons/photo-localization-extension/master/data/map.png)


#How to install
Build from source and deploy the jar service and webapp.
Or deploy for addons catalog

#Properties
2 properties are needed in gatein/conf/exo.properties :

* maps.api.key={your google maps API Key}
* photo.localization.folder=/Groups/spaces/exo_around_the_world/Documents

The maps API Key can be created on https://console.developers.google.com/ and require a google account. The API you need is "Google Maps JavaScript API".
The folder is the one in which the extension will look for new photos.

#Portlet
The portlet Photo Localization must be added somewhere to display the map. 
In application List, as administrator, put it in a categorie, with access rights like "/platform/users". Then, in a space, you can add it as application. 
Go in the space, in parameters space page, in Applications tab, then Add  new application. 
Choose the Photo Localization Portlet.