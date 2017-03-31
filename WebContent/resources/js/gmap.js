var app = angular.module('myApp', [ 'ngMap' ]);

app.controller('mapController', function($interval) {
	var vm = this;
	vm.positions = [];
	var geocoder = new google.maps.Geocoder;
	var lat, lng;
	var generateMarkers = function() {
		vm.positions = [];// sera egale au tableau de sortie
		navigator.geolocation.getCurrentPosition(foundLocation);

		function foundLocation(position) {
			lat = position.coords.latitude;
			lng = position.coords.longitude;
			vm.current = "[" + position.coords.latitude + ","
					+ position.coords.longitude + "]";
		};

		var numMarkers = Math.floor(Math.random() * 4) + 4; // between 4 to 8
															// markers
		for (i = 0; i < numMarkers; i++) {
			var lat = 50.6231830 + (Math.random() / 100);
			var lng = 3.1238490 + (Math.random() / 100);
			vm.positions.push({
				lat : lat,
				lng : lng
			});

		}
	};
	$interval(generateMarkers, 5000);// refresh position
});
