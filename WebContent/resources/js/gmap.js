var app = angular.module('myApp', [ 'ngMap' ]);

app.controller('mapController', function($interval) {
	var vm = this;
	vm.positions = [];
	var geocoder = new google.maps.Geocoder;
	var lat, lng;
	var generateMarkers = (function(vm) {
		vm.positions = [];// sera egale au tableau de sortie

		navigator.geolocation.getCurrentPosition(foundLocation.bind(this, vm));

		function foundLocation(vm, position) {
			lat = position.coords.latitude;
			lng = position.coords.longitude;
			vm.current = "[" + position.coords.latitude + ","
					+ position.coords.longitude + "]";

			if (rest.userType == RestTemplate.ClientType.CUSTOMER)
				rest.driver_AllAround(lng, lat, (function(vm, conducteurs) {
					console.log("////////////TABLEAU DATA////////////");
					console.log(conducteurs);
					conducteurs.forEach((function(vm, conducteur){
						console.log(vm);
						vm.positions.push({               // Il remplit bien le truc correctement
							lat : conducteur.latitude,    // mais il n'affiche rien sur la carte.
							lng : conducteur.longitude    //
						});
						console.log(vm); // C'est bien dedans là
					}).bind(this, vm));
				}).bind(this, vm));
		}
	}).bind(this, vm);
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

