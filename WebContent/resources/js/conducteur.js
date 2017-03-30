//FONCTION DISPONNIBLE INDISPONNIBLE
$('#toggle-trigger')
		.change(
				function() {
					if ($(this).prop('checked') == true) {
						$("#alertZone")
								.html(
										'<div id="alertDiv" class="alert alert-info">'
												+ '<strong>Info! </strong>'
												+ 'Vous êtes désormais <strong>disponible</strong>.'
												+ '</div>');
						$("#dispoVar")
								.html(
										'<div class="alert alert-info" role="alert">'
												+ '<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>'
												+ '<span class="sr-only">Info:</span>Vous êtes actuellement <strong>Disponible</strong>'
												+ '</div>');

						rest.driver_SetAvailable();

					} else {
						$("#alertZone")
								.html(
										'<div id="alertDiv" class="alert alert-info">'
												+ '<strong>Info! </strong>'
												+ 'Vous êtes désormais <strong>indisponible</strong>.'
												+ '</div>');
						$("#dispoVar")
								.html(
										'<div class="alert alert-danger" role="alert">'
												+ '<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>'
												+ '<span class="sr-only">Info:</span>Vous êtes actuellement <strong>Indisponible</strong>'
												+ '</div>');

						rest.driver_SetUnavailable();

					}

					$("#alertDiv").fadeTo(2000, 500).slideUp(500, function() {
						$("#alertDiv").slideUp(500);
					});

				});


//HISTORIQUE DES COURSE
$("#btn-history").click(function(){
rest.driver_GetRunsHistory(function(course) {
	course.forEach(function(item, index) {	    
	    var time = new Date((item.dateFinCourse - item.dateDebutCourse) - 3600000);
	    $("#history")
		.append(
				'<tr class="success">'
						+ '<td>' + item.adresseDepart + '</td>'
						+ '<td>' + time.getHours().toString().replace(/^(\d)$/,'0$1') + 'h' + time.getMinutes().toString().replace(/^(\d)$/,'0$1') + '</td>'
						+ '<td>' + item.prix + ' €</td>'
						+ '</tr>');
		});
	});
});


//TOGGLE BOUTON DISPO, INDISPO
rest.driver_GetMyProfil(function(profil) {
$('#idClientNomPrenom').html()
if (profil.available == true) {
$('#toggle-trigger').bootstrapToggle('on')
} else {
$('#toggle-trigger').bootstrapToggle('off')
}
});


//REFRESH LAT LNG + COURSE DISPO
var refreshCourse = function() {
	function foundLocation(position) {
		var lat = position.coords.latitude;
		var lng = position.coords.longitude;
		
		rest.driver_refreshPosAndGetAvailableRides(lat, lng, function(course) {
			if (course != null) {
				console.log('ta une course nigo !');
			} else {
				console.log('ya quedal ici');
			}
		});
	};
	navigator.geolocation.getCurrentPosition(foundLocation);
};


//setInterval(refreshCourse, 1000);