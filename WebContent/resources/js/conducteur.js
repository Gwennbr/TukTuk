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
					
					//EFFET ZONE ALERTE
					$("#alertDiv").fadeTo(5000, 1000).slideUp(1000, function() {
						$("#alertDiv").slideUp(1000);
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
	if (profil.available == true) {
			$('#toggle-trigger').bootstrapToggle('on');
	} else {
			$('#toggle-trigger').bootstrapToggle('off');
	}
});

//ACCPETE COURSE
function acceptClient(id) {
	rest.ride_Accept(id);
	rest.driver_SetUnavailable();
	$('#toggle-trigger').bootstrapToggle('off');
	console.log('avant rideInfos');
	var valid = false;
	interCheckRide = setInterval(checkRide, 2000);// refresh position
	
};

var checkRide = function() {
	rest.ride_Infos(function(data){
			alert('coucou');
			console.log('avant boucle');
				console.log('boucle');
				if (data.valide == 1) {
					$('#directionMap').html('<div id="directionMap">'+
						'<directions'+
      					'draggable="false"'+
      					'travel-mode="DRIVING"'+
      					'origin="current-location"'+
      					'destination="lille grand palais">'+
      				'</directions>'+
    					'</div>');
				
					$("#alertZone")
					.html(
							'<div id="alertDiv" class="alert alert-info">'
									+ '<strong>Info! </strong>'
									+ 'Course valider par le client'
									+ '</div>');
				
					$("#alertDiv").fadeTo(5000, 1000).slideUp(1000, function() {
						$("#alertDiv").slideUp(1000);
					});
				
					valid = true;
					clearInterval(interCheckRide);
				};
			
				if (data.conducteur == null) {
					$("#alertZone")
					.html(
							'<div id="alertDiv" class="alert alert-info">'
									+ '<strong>Info! </strong>'
									+ 'Le client a refuser la course.'
									+ '</div>');
				
					$("#alertDiv").fadeTo(5000, 1000).slideUp(1000, function() {
						$("#alertDiv").slideUp(1000);
					});
					rest.driver_SetAvailable();
					valid = true;
					clearInterval(interCheckRide);
				};
		console.log('apres boucle');
	});
};

//REFRESH LAT LNG + COURSE DISPO
var refreshCourse = function() {
	rest.driver_GetMyProfil(function(profil) {
			function foundLocation(position) {
				var lat = position.coords.latitude;
				var lng = position.coords.longitude;
				
				rest.driver_refreshPosAndGetAvailableRides(lat, lng, function(course) {
					var liste = "";
					$('#btn-course-dispo').html('<span class="label label-pill label-success" id="puceBadge">' + course.length + ' course(s) disponnible</span>');
					if (course.length != 0) {
						if (profil.available == true) {
							course.forEach(function(data) {
								var noteClient = data.noteClient;
								if (noteClient == -1) {
									noteClient = 0;
								};
								liste += '<div class="panel panel-primary">'+
								'<div class="alert alert-success">Doge LeChien</div>'+			
								'<div class="panel-body">'+
								'<img id="rating" src="'+ host +'/resources/img/rate' + noteClient + '.png" alt="Note"/>'+
    							'</div>'+
    							'<div class="panel-footer">'+
    							'<button id="btn-accept" onclick="acceptClient(' + data.id + ')" type="button" class="btn btn-success">Accpeter</button>'+
//    							'<button id="btn-reject" type="button" class="btn btn-danger">Refuser</button>'+
  								'</div>'+
							'</div>';
							});
						}
						else {
							$('#btn-course-dispo').html('<span class="label label-pill label-danger" id="puceBadge">Vous n\'êtes pas disponnible</span>');
						};
					}
					else {
						if(profil.available == false) {
							$('#btn-course-dispo').html('<span class="label label-pill label-danger" id="puceBadge">Vous n\'êtes pas disponnible</span>');
					
						};
					};
					$('#listeCourse').html(liste);
				});
			};
			navigator.geolocation.getCurrentPosition(foundLocation);

	});
};


setInterval(refreshCourse, 1000);