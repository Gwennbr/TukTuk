//HISTORIQUE DES COURSE
$("#btn-history").click(function(){
	rest.customer_GetRunsHistory(function(course) {
		$("#history").html('<tr class="warning"><th>Adresse</th><th>Temps</th><th>Prix</th></tr>');
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



//RECHERCHER VEHICULE
dataOk = false;
var inter;
$('#btn-search').click(function(){
	$("#commande-modal").modal("hide");
	$("#waiting-modal").modal("show");
	$('#btn-search').prop('disabled', true);
	$('#vmadresse').prop('disabled', true);
	var course;
	console.log("j'envoie l'adresse " + $('#vmadresse').val() + " au rest ctrl");
	rest.ride_Request($('#vmadresse').val(), function(){
		console.log("il trouve l'adresse");
		inter = setInterval(checkRider, 2000);// refresh position
	}, function() {
		alert("adresse incorrecte");
	});
	
	

});

var checkEndRideIntervalToken = undefined;
var rideId = undefined;
var bRideStarted = false;

var checkEndRide = function()
{
	rest.ride_InfosWithId(rideId, function(course){
		if (course.dateFinCourse != null)
		{
			clearInterval(checkEndRideIntervalToken);
			var time = new Date((course.dateFinCourse - course.dateDebutCourse - course.tempsPause) - 3600000);
			$("#card-block-rideInfos").html('La course est terminée<br /><hr />' + 
					'durée : ' + time.getHours().toString().replace(/^(\d)$/,'0$1') + 'h' + time.getMinutes().toString().replace(/^(\d)$/,'0$1') +
					'<br />prix : ' + course.prix + ' €');		
			$("#rideInfos-modal").modal("show");
			
		}
		else
			if (!bRideStarted && course.dateDebutCourse != null)
			{
				bRideStarted = true;
				$("#card-block-rideInfos").html('Le conducteur a démarré la course');		
				$("#rideInfos-modal").modal("show");				
			}
	});
}

var checkRider = function() {
	rest.ride_Infos(function(checkCourse){
		if(checkCourse.conducteur != null){
			rest.driver_GetNote(checkCourse.conducteur.id, function(noteConducteur){
				if (noteConducteur == -1) {
					noteConducteur = 0;
				};
				clearInterval(inter);
				$("#waiting-modal").modal("hide");
				$("#card-block").html('<h4 class="card-title">' + checkCourse.conducteur.prenom + ' ' + checkCourse.conducteur.nom + '</h4>'+
						'<br>'+
						'<img id="rating" src="'+ host +'/resources/img/rate' + noteConducteur + '.png" alt="Note"/>'+
						'<hr />'+
						'<a id="btn-commandée" onclick="accepteCourseClient();" class="btn btn-success">Accepter</a>'+
						'<a id="btn-annulée" onclick="refuseCourseClient()" class="btn btn-danger">Refuser</a>');		
				$("#driverInfo-modal").modal("show");
				
			});
		}
	});
};


//VERIF COURSE ACTIVE
rest.ride_Infos(function(data){
	$('#btn-search').prop('disabled', true);
	$('#vmadresse').prop('disabled', true);
	if(data.conducteur != null && data.valide != 0){
		$("#alertZone")
		.html(
				'<div id="alertDiv" class="alert alert-info">'
						+ '<strong>Info! </strong>'
						+ 'Vous êtes déjà en course. Le conducteur arrive.'
						+ '</div>');
		
	}
	else {
		$('#btn-search').prop('disabled', true);
		$('#vmadresse').prop('disabled', true);
		$("#waiting-modal").modal("show");
		inter = setInterval(checkRider, 2000);
		$("#alertZone")
		.html(
				'<div id="alertDiv" class="alert alert-info">'
						+ '<strong>Info! </strong>'
						+ 'La recheche de course est toujours en cours.'
						+ '</div>');
	};
	//EFFET ZONE ALERTE
	$("#alertDiv").fadeTo(5000, 500).slideUp(500, function() {
		$("#alertDiv").slideUp(500);
	});
}, function(error) {
	
});

//ACCEPTER COURSE
function accepteCourseClient() {
	rest.ride_Validate(function(data){
		$("#driverInfo-modal").modal("hide");
		$('#btn-search').addClass("disabled")
		$('#vmadresse').addClass("disabled")
		$("#alertZone")
		.html(
				'<div id="alertDiv" class="alert alert-info">'
						+ '<strong>Info! </strong>'
						+ 'Vous avez accepté le chauffeur <strong>'+ data.conducteur.prenom + ' ' + data.conducteur.nom +'</strong> , le conducteur arrive.'
						+ '</div>');
		
		$("#alertDiv").fadeTo(5000, 500).slideUp(500, function() {
			$("#alertDiv").slideUp(500);
		});
		
		bRideStarted = false;
		rideId = data.id;
		checkEndRideIntervalToken = setInterval(checkEndRide, 1000);
	});
	
};

//REFUSER COURSE
function refuseCourseClient() {
	rest.ride_Decline(function(data){
		$("#driverInfo-modal").modal("hide");
		$('#btn-search').addClass("disabled")
		$('#vmadresse').addClass("disabled")
		$("#alertZone")
		.html(
				'<div id="alertDiv" class="alert alert-info">'
						+ '<strong>Info! </strong>'
						+ 'Vous avez refusé le chauffeur !'
						+ '</div>');
		$("#waiting-modal").modal("show");
		inter = setInterval(checkRider, 2000);
		
		$("#alertDiv").fadeTo(5000, 500).slideUp(500, function() {
			$("#alertDiv").slideUp(500);
		});
	});
	
};

//ANNULEE COURSE
function annuleeCourseClient() {
	rest.ride_Delete(function(data){
		$("#modal-waiting").modal("hide");
		$('#btn-search').removeClass("disabled")
		$('#vmadresse').removeClass("disabled")
		$("#alertZone")
		.html(
				'<div id="alertDiv" class="alert alert-info">'
						+ '<strong>Info! </strong>'
						+ 'Vous annulez la recherche !'
						+ '</div>');
		console.log('test');
		$("#waiting-modal").modal("show");
		inter = setInterval(checkRider, 2000);
		
		$("#alertDiv").fadeTo(5000, 500).slideUp(500, function() {
			$("#alertDiv").slideUp(500);
		});
	});
};