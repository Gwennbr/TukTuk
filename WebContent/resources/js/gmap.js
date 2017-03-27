<<<<<<< HEAD
var app = angular.module('myApp', ['ngMap']);

  app.controller('mapController', function($interval) {
    var vm = this;
    vm.positions = [];
    var generateMarkers = function() {
      vm.positions = [];//sera egale au tableau de sortie
	  navigator.geolocation.getCurrentPosition(foundLocation);

	  function foundLocation(position) {
		  vm.current = "[" + position.coords.latitude + "," + position.coords.longitude + "]";
	  };
	  
	  
      var numMarkers = Math.floor(Math.random() * 4) + 4; //between 4 to 8 markers
      for (i = 0; i < numMarkers; i++) {
        var lat = 50.6231830 + (Math.random() / 100);
        var lng = 3.1238490 + (Math.random() / 100);
        vm.positions.push({lat:lat, lng:lng});
		
      }
	  
	  
      console.log("vm.positions", vm.positions);
      console.log("vm.current", vm.current);
	  
    };

    $interval(generateMarkers, 5000);//refresh position
  });
=======
//var app = angular.module('myApp', ['ngMap']);
//
//  app.controller('mapController', function($interval) {
//    var vm = this;
//    vm.positions = [];
//	
//    var generateMarkers = function() {
//      
//    };
//
//    $interval(generateMarkers, 2000);
//  });
//  
//  app.controller('MyCtrl', function(NgMap) {
//  var vm = this;
//  vm.types = "['establishment']";
//  vm.placeChanged = function() {
//    vm.place = this.getPlace();
//    vm.map.setCenter(vm.place.geometry.location);
//  }
//  NgMap.getMap().then(function(map) {
//    vm.map = map;
//  });
//  
  
  
//  vm.addMarker = function(event) {
//      var ll = event.latLng;
//      vm.positions.push({lat:ll.lat(), lng: ll.lng()});
//    }
//    vm.deleteMarkers = function() {
//      vm.positions = [];
//    };
  
// });
  
  
>>>>>>> branch 'feature/login' of https://github.com/Gwennbr/TukTuk.git
