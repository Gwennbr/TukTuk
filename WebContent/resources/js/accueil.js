$(function() {	
    $('#toggle-trigger').change(function() {
    	if ($(this).prop('checked') == true) {
    		$("#alertZone").html('<div id="alertDiv" class="alert alert-info">'+
        		'<button type="button" class="close" data-dismiss="alert">x</button>'+
        		'<strong>Info! </strong>'+
        		'Vous êtes désormais disponible.'+
    			'</div>');
    		$("#dispoVar").html('<div class="alert alert-info" role="alert">'+
					'<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>'+
						'<span class="sr-only">Info:</span>Vous êtes actuellement <strong>Disponible</strong>'+
					'</div>');
    	}
    	else { 
    		$("#alertZone").html('<div id="alertDiv" class="alert alert-info">'+
            		'<button type="button" class="close" data-dismiss="alert">x</button>'+
            		'<strong>Info! </strong>'+
            		'Vous êtes désormais indisponible.'+
        			'</div>');
    		$("#dispoVar").html('<div class="alert alert-danger" role="alert">'+
					'<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>'+
						'<span class="sr-only">Info:</span>Vous êtes actuellement <strong>Indisponible</strong>'+
					'</div>');
    	}
    	
    	
    	
    	
    	$("#alertDiv").fadeTo(2000, 500).slideUp(500, function(){
    		$("#alertDiv").slideUp(500);
    	});
    	
    });
    
    for(var i = 0; i < 10; i++) {
    	$("#history").append('<tr class="success">'+
				'<td>1 rue de Troy - 59100 - Lille</td>'+
			    '<td>00:12:24</td>'+
			    '<td>5,54 €</td>'+
			'</tr>');
    };
});


  
// <div id="alertDiv" class="alert alert-success">
// <button type="button" class="close" data-dismiss="alert">x</button>
// <strong>Success! </strong>
// Product have added to your wishlist.
// </div>
