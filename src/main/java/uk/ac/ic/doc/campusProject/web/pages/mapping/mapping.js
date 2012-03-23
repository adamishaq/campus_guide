<wicket:panel>

<html>
  <head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <style type="text/css">
      html { height: 100% }
      body { height: 100%; margin: 0; padding: 0 }
      #map_canvas { height: 100% }
	  #latlong { height: 100% }
    </style>
    <script type="text/javascript"
      src="http://maps.googleapis.com/maps/api/js?key=AIzaSyD26QGOqgvP14GXSIrn9qqK5vhfEZU1_-A&sensor=false">
    </script>
	<!--
    <script type="text/javascript">
    
      var map;
	  var longitude; 
      function initialize() {
        var myOptions = {
          center: new google.maps.LatLng(51.49858,-0.177),
          zoom: 17,
          mapTypeId: google.maps.MapTypeId.HYBRID
        };
        
        map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
			
		
		google.maps.event.addListener(map, 'click', function(event){
		
			longitude = event.latLng;
			placeMarker(longitude);
			
			
		});
	    
  
      }
      
      
      function placeMarker(location) {
            var marker = new google.maps.Marker({
            position: location,
            map: map
         });
		 wicketAjaxPost("http://localhost:8080/wicket/bookmarkable/uk.ac.ic.doc.campusProject.web.pages.mapping.MapGeoTagPage?longitude="location);
	  }
	  </script>
	  -->

      

  </head>
  <body onload="initialize()">
        <div id="map_canvas" style="width:50%; height:50%"></div>
  </body>
</html>


</wicket:panel>