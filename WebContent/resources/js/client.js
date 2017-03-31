//HISTORIQUE DES COURSE
$("#btn-history").click(function(){
	rest.customer_GetRunsHistory(function(course) {
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
var interval;
$('#btn-search').click(function(){
	var course = rest.ride_Request($('#vmadresse').val());
	console.log(course);
	inter = setInterval(checkRider(course.id), 2000);
});

function checkRider(id){
	var checkCourse = rest.ride_Infos(id);
	console.log("inner checkRider");
	if(checkCourse.conducteur != null){
		clearInterval(inter);
		$("#waiting-modal").modal("hide");
		$("#card-block").html('<h4 class="card-title">' + checkCourse.conducteur.prenom + ' ' + checkCourse.conducteur.nom + '</h4>'+
				'<br>'+
				'<img id="rating" src="${ pageContext.request.contextPath }/resources/img/rate' + checkCourse.noteConducteur + '.png" alt="Note"/>'+
				'<hr />'+
				'<a id="btn-commandée" oneclick="accepteCourseClient();" class="btn btn-success">Commandée</a>'+
				'<a id="btn-annulée" oneclick="refuseCourseClient" class="btn btn-danger">Annulée</a>');		
		$("#driverInfo-modal").modal("show");
	}
};



//ACCEPTER COURSE
function accepteCourseClient() {
	alert("j'accepte");
};

//REFUSER COURSE
function refuseCourseClient() {
	alert("je refuse");
};