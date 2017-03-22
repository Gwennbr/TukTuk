var app = angular.module('myApp', ['ngMap']);

  app.controller('mapController', function($interval) {
    var vm = this;
    vm.positions = [];
	
    var generateMarkers = function() {
      
    };

    $interval(generateMarkers, 2000);
  });
  
  app.controller('MyCtrl', function(NgMap) {
  var vm = this;
  vm.types = "['establishment']";
  vm.placeChanged = function() {
    vm.place = this.getPlace();
    console.log('location', vm.place.geometry.location);
    vm.map.setCenter(vm.place.geometry.location);
  }
  NgMap.getMap().then(function(map) {
    vm.map = map;
  });
 });