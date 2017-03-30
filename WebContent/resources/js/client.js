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

	interval = setInterval(checkRider(course.id), 2000);
});

function checkRider(id){
	var checkCourse = rest.ride_Infos(id);
	if(checkCourse.conducteur != null){
		interval.stop();
	}
};