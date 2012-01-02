// "Da" Map
var map;

// Global Markers Array
var markers = [];

// Global Debug Flag
var debug = true;
if (console == null) {
    debug = false;
}

// Log Function
function log(msg) {
    if (debug) {
        console.log(msg);
    }
}

// Initialize Map
function initialize() {
    // Map Options
    var myOptions = {
          zoom: 8,
          mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    // Define Map
    map = new google.maps.Map(document.getElementById('map_canvas'), myOptions);

    // Set Map Position
    map.setCenter(new google.maps.LatLng(40.710623, -74.006605));

    // Load Markers
    loadMarkers(map, null);

    // Listen for Map Movements
    google.maps.event.addListener(map, 'idle', function(ev) {
            log("Idle Listener!");
            var bounds = map.getBounds();
            var ne = bounds.getNorthEast(); // LatLng of the north-east corner
            var sw = bounds.getSouthWest(); // LatLng of the south-west corner
            var nw = new google.maps.LatLng(ne.lat(), sw.lng());
            var se = new google.maps.LatLng(sw.lat(), ne.lng());
            var q = "&ne=" + ne.lat() + "&sw=" + sw.lng() + "&nw=" + sw.lat() + "&se=" + ne.lng();
            log("Map Bounds: " + q);
            clearOverlays();
            loadMarkers(map, q);
        });
}

// Clear Markers
function clearOverlays() {
    log("Clear Overlays!");
    if (markers != null) {
        for (i in markers) {
            markers[i].setMap(null);
        }
    }
    markers = new Array();
}

// Cap Words
function capWords(str){
    if (str == null) {
        return "";
    }
    str = str.toLowerCase();
   words = str.split(" ");
   for (i=0 ; i < words.length ; i++){
      testwd = words[i];
      firLet = testwd.substr(0,1); //lop off first letter
      rest = testwd.substr(1, testwd.length -1)
      words[i] = firLet.toUpperCase() + rest
   }
   return words.join(" ");
}

// Load Markers
function loadMarkers(map, extra) {
    $(document).ready(function() {
        zoomLevel = map.getZoom();
        if (zoomLevel == null) {
            zoomLevel = 1;
        }
        var url = '/mapOverlay';
        if (document.location.search) {
            url = url + document.location.search;
        } else {
            url = url + "?1=1"
        }
        if (extra != null) {
            url = url + extra;
        }
        url = url + "&zoom=" + zoomLevel;
        log('URL: ' + url);
        $.getJSON(url, function(json) {
            markers = new Array(json.data.markers.length);
            for (i = 0; i < json.data.markers.length; i++) {
                  // Get Marker
                  var marker = json.data.markers[i];

                  // Customer Logo
                  var icon = "http://geeks.aretotally.in/wp-content/uploads/2011/03/html5_geek_matt_16.png";
                  var logo = "http://geeks.aretotally.in/wp-content/uploads/2011/03/html5_geek_matt_32.png";
                  var width = 32;
                  var height = 32;
                  var customer = marker.customer;

                  // console.log('Marker: ' + marker + ', Lat: ' + marker.latitude + ', Lng: ' + marker.longitude);
                  var contentString = '<div id="content"><font face="Verdana">'+
                      '<div id="siteNotice"><img src="' + logo + '" border="0" width="' + width + '" height="' + height + '"><br><br>'+
                      '</div>'+
                      '<h2 id="firstHeading" class="firstHeading">' + '' + '</h2>'+
                      '<div id="bodyContent">' + capWords(marker.address) + '<br>' + capWords(marker.city) + ', ' + marker.state + ' ' + marker.zip + '<br>' + capWords(marker.county) +
                      '</p>'+
                      '</div>'+
                      '</font></div>';

                  var position = new google.maps.LatLng(marker.latitude, marker.longitude);

                  var m = new google.maps.Marker({
                      position: position,
                      icon: icon,
                      html: contentString
                  });

                  markers[i] = m;
            }

            // Clusters
            for (i = 0; i < json.data.clusters.length; i++) {
                var cluster = json.data.clusters[i];
                log('Cluster: ' + cluster);

                var position = new google.maps.LatLng(cluster.latitude, cluster.longitude);

                for (c = 0; c < cluster.count; c++) {
                  log('Cluster Item: ' + c);
                  var m = new google.maps.Marker({
                      position: position
                  });

                  markers[i] = m;
                }
            }

            // Marker Cluster
            var clusterOptions = { zoomOnClick: true }
            var markerCluster = new MarkerClusterer(map, markers, clusterOptions);

            // Info Window
            var infowindow = new google.maps.InfoWindow({
                content: 'Loading...'
            });

            // Info Window Listener for Markers
            for (var i = 0; i < markers.length; i++) {
                var marker = markers[i];
                google.maps.event.addListener(marker, 'click', function () {
                    // Log Debug
                    log('Marker Click!');

                    // Set Info Window Marker Content
                    infowindow.close();
                    infowindow.setContent(this.html);

                    // Set Current Marker
                    var currentMarker = this;

                    // Get Map Position
                    var mapLatLng = map.getCenter();
                    var markerLatLng = currentMarker.getPosition();

                    // Check Coordinate
                    if (!markerLatLng.equals(mapLatLng)) {
                        // Map will need to pan
                        map.panTo(markerLatLng);
                        google.maps.event.addListenerOnce(map, 'idle', function() {
                            // Open Info Window
                            infowindow.open(map, currentMarker);
                            setTimeout(function () { infowindow.close(); }, 5000);
                        });
                    } else {
                        // Map won't be panning, which wouldn't trigger 'idle' listener so just open info window
                        infowindow.open(map, currentMarker);
                        setTimeout(function () { infowindow.close(); }, 5000);
                    }
                });
            }

        });
    });
}


// Initialize Map
google.maps.event.addDomListener(window, 'load', initialize);
