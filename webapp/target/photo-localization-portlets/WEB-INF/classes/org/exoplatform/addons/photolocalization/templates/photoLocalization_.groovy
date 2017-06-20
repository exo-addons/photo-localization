package org.exoplatform.addons.photolocalization.templates;
;out.print(org.exoplatform.addons.photolocalization.templates.CphotoLocalization.s0);
;out.print("${rssUrl}");
;out.print(org.exoplatform.addons.photolocalization.templates.CphotoLocalization.s1);
;out.print("${mapsJSUrl}");
;out.print(org.exoplatform.addons.photolocalization.templates.CphotoLocalization.s2);

public class CphotoLocalization
{
public static final String s0 = ' <style type="text/css">\n      #map { height: 100%; }\n      #photoLocalizationPortlet {height:800px;margin:15px;}\n    </style>\n\n<div id="photoLocalizationPortlet">\n<div id="map"></div>\n</div>\n\n\n<script type="text/javascript">\n\nvar map;\nfunction initMap() {\n  map = new google.maps.Map(document.getElementById(\'map\'), {\n    center: {lat: -34.397, lng: 150.644},\n    zoom: 8\n  });\n	var url = \'';
public static final String s1 = '?dummy=\'+new Date().getTime();\n\n var georssLayer = new google.maps.KmlLayer({\n   url: url\n  });\n  georssLayer.setMap(map);\n}\n\n</script>\n<script async defer src="';
public static final String s2 = '">\n</script>\n';
public static final Map<Integer, juzu.impl.template.spi.juzu.dialect.gtmpl.Foo> TABLE = [
3:new juzu.impl.template.spi.juzu.dialect.gtmpl.Foo(new juzu.impl.common.Location(20,19),'rssUrl'),
5:new juzu.impl.template.spi.juzu.dialect.gtmpl.Foo(new juzu.impl.common.Location(26,28),'mapsJSUrl')];
}
