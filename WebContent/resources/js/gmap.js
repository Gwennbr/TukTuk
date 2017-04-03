var app = angular.module('myApp', [ 'ngMap' ]);

var markers = [];

app.controller('mapController', function($interval, NgMap) {
	var vm = this;
	vm.positions = [];
	var geocoder = new google.maps.Geocoder;
	var lat, lng;
	var generateMarkers = (function(vm, NgMap) {
		vm.positions = [];// sera egale au tableau de sortie

		navigator.geolocation.getCurrentPosition(foundLocation.bind(this, vm, NgMap));

		function foundLocation(vm, NgMap, position) {
			lat = position.coords.latitude;
			lng = position.coords.longitude;
			vm.current = "[" + position.coords.latitude + ","
					+ position.coords.longitude + "]";

			if (rest.userType == RestTemplate.ClientType.CUSTOMER)
				rest.driver_AllAround(lng, lat, (function(vm, NgMap, conducteurs) {
					NgMap.getMap().then(function(map) {
						for (var i = 0; i < markers.length; i++) {
							markers[i].setMap(null);
						}
						markers = [];
						conducteurs.forEach((function(vm, NgMap, conducteur){
							var loc = new google.maps.LatLng(conducteur.latitude, conducteur.longitude);
							var marker = new google.maps.Marker({
							    position: loc,
							    map: map,
							    title: conducteur.prenom + ' ' + conducteur.nom,
							    icon: host +'/resources/img/trishawColor.png'
							  });
							markers.push(marker);
						}).bind(this, vm, NgMap));
					},10000);
				}).bind(this, vm, NgMap));
		}
	}).bind(this, vm, NgMap);
	rest.updateUserInfos(function(){
		$interval(generateMarkers, 1000);// refresh position		
	});
});



//			var numMarkers = Math.floor(Math.random() * 4) + 4; between 4 to 8
//				
//			for (i = 0; i < numMarkers; i++) {
//				var lat = 50.6231830 + (Math.random() / 100);
//				var lng = 3.1238490 + (Math.random() / 100);
//				vm.positions.push({
//					lat : lat,
//					lng : lng
//				});
//			}

