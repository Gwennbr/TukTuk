


$(function() {
    $('#toggle-trigger').change(function() {
    	if ($(this).prop('checked') == true) {
    		$("#alertZone").html('<div id="alertDiv" class="alert alert-info">'+
        		'<button type="button" class="close" data-dismiss="alert">x</button>'+
        		'<strong>Info! </strong>'+
        		'Vous êtes désormais disponible.'+
    			'</div>');
    		$("#dispoVar").html('Disponible');
    	}
    	else {
    		$("#alertZone").html('<div id="alertDiv" class="alert alert-info">'+
            		'<button type="button" class="close" data-dismiss="alert">x</button>'+
            		'<strong>Info! </strong>'+
            		'Vous êtes désormais indisponible.'+
        			'</div>');
    		$("#dispoVar").html('Indisponible');
    	}
    	
    	$("#alertDiv").fadeTo(2000, 500).slideUp(500, function(){
    		$("#alertDiv").slideUp(500);
    	});
    	
    });
});
  
  
// <div id="alertDiv" class="alert alert-success">
// <button type="button" class="close" data-dismiss="alert">x</button>
// <strong>Success! </strong>
// Product have added to your wishlist.
// </div>
