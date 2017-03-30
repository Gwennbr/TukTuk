
//	rest.driver_GetRunsHistory(function(course) {
//			course.forEach(function(item, index) {	    
//			    var time = new Date((item.dateFinCourse - item.dateDebutCourse) - 3600000);
//			    $("#history")
//				.append(
//						'<tr class="success">'
//								+ '<td>' + item.adresseDepart + '</td>'
//								+ '<td>' + time.getHours().toString().replace(/^(\d)$/,'0$1') + 'h' + time.getMinutes().toString().replace(/^(\d)$/,'0$1') + '</td>'
//								+ '<td>' + item.prix + ' €</td>'
//								+ '</tr>');
//			});
//	});
//	
//
//rest.driver_GetMyProfil(function(profil) {
//	$('#idClientNomPrenom').html()
//	if (profil.available == true) {
//		$('#toggle-trigger').bootstrapToggle('on')
//	} else {
//		$('#toggle-trigger').bootstrapToggle('off')
//	}
//});

//function historyAppend(item, index) {
//	var time = new Date((item.dateFinCourse - item.dateDebutCourse) - 3600000);
//	$("#history").append(
//			'<tr class="success">' + '<td>' + item.adresseDepart + '</td>'
//					+ '<td>'
//					+ time.getHours().toString().replace(/^(\d)$/, '0$1') + 'h'
//					+ time.getMinutes().toString().replace(/^(\d)$/, '0$1')
//					+ '</td>' + '<td>' + item.prix + ' €</td>' + '</tr>');
//};


