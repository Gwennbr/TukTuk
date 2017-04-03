<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html data-ng-app="myApp">
<head>
	<link rel="icon" type="image/png" href="${ pageContext.request.contextPath }/resources/img/favicon.png" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<script src="${ pageContext.request.contextPath }/resources/js/angular.min.js"></script>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css">
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
	<link rel="stylesheet" type="text/css" href="${ pageContext.request.contextPath }/resources/css/global.css"/>
	<link rel="stylesheet" type="text/css" href="${ pageContext.request.contextPath }/resources/css/interface.css"/>
	<link href="https://fonts.googleapis.com/css?family=Raleway" rel="stylesheet">
	<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
	<script src="https://rawgit.com/allenhwkim/angularjs-google-maps/master/build/scripts/ng-map.js"></script>
	<link href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css" rel="stylesheet">
	<script src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>
	<script src="https://maps.google.com/maps/api/js?libraries=placeses,visualization,drawing,geometry,places&key=AIzaSyBWK16ZhsCDITiislRiRzpb4qjPrYfXp4s"></script>
</head>
<body data-ng-app="ngMap">
	<header>
		<nav>
			<c:if test="${ not empty conducteur }">
<!-- 				<a class="left btn btn-lg btn-link" data-toggle="modal" data-target=".nav-course" data-dismiss="modal"><span class="glyphicon glyphicon-th"></span><span id="puceBadge" class="badge"></span></a> -->
				<a class="left btn btn-lg btn-link" data-toggle="modal" data-target=".nav-course" data-dismiss="modal"><span class="glyphicon glyphicon-road"></span> <span id="btn-course-dispo"></span></a>
			</c:if>
				<label class="btn-lg" id="titleNav"><b class="colorWhite">ADOPTE</b>UN<b class="colorWhite">TUK-TUK.COM</b></label>
			<a class="right btn btn-lg btn-link" data-toggle="modal" data-target=".nav-side" data-dismiss="modal"><span class="glyphicon glyphicon-cog"></span></a>
		</nav>
		<div id="alertZone">
			
		</div>
	</header>
	<section id="map" >
		
		<div id="gmap" ng-controller="mapController as vm">
    	<ng-map id="currentMap" zoom="15" center="current" map-type-id="MapTypeId.ROADMAP">
			<c:choose>
				<c:when test="${not empty conducteur}">
					<marker position="{{vm.current}}" animation="Animation.NONE" icon=${ pageContext.request.contextPath }/resources/img/trishawColor.png></marker>			
					<div id="directionMap">
<!-- 						<directions -->
<!--           					draggable="false" -->
<!--           					travel-mode="DRIVING" -->
<!--           					origin="current-location" -->
<!--           					destination="lille grand palais"> -->
<!--         				</directions> -->
        			</div>
<%-- 					<marker ng-repeat="pos in vm.positions" position="{{pos.lat}},{{pos.lng}}" icon=${ pageContext.request.contextPath }/resources/img/userColor.png></marker> --%>
				</c:when>
				<c:otherwise>
					<marker position="{{vm.current}}" animation="Animation.NONE" icon=${ pageContext.request.contextPath }/resources/img/userColor.png></marker>			
					<marker ng-repeat="pos in vm.positions" position="{{pos.lat}},{{pos.lng}}" icon=${ pageContext.request.contextPath }/resources/img/trishawColor.png></marker>
				</c:otherwise>
			</c:choose>
		</ng-map>
	</div>
	
	</section>
	
	<section id="alertSection">
	
	</section>
	
	<section id="control">
		<div id="btn-control" >
			<button type="button" class="btn btn-primary btn-circle btn-xl" data-toggle="modal" data-target=".modal-commande"><i class="glyphicon glyphicon-menu-up"></i></button>
		</div>
		<!-- Modal -->
		<div id="commande-modal" index="modal-control" class="modal fade modal-commande" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
			<div class="modal-dialog modal-lg" role="document">
			
			<c:choose>
  				<c:when test="${not empty conducteur}">
   					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
							<h4 class="modal-title">Option conducteur : ${ conducteur.prenom } ${ conducteur.nom }</h4>
						</div>
						<div class="modal-body">
							<div id="dispoVar">
								<div class="alert alert-info" role="alert">
									<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
  									<span class="sr-only">Info:</span>Vous êtes actuellement <strong>Disponible</strong>
  								</div>
  							</div>
							<button id="btn-pause" type="button" class="btn btn-primary chauffeur">Activer mode pause</button>
							<button id="btn-StartStopCourse" type="button" class="btn btn-success">Démarrer course</button>
<!-- 							<button id="btn-cancelCourse" type="button" class="btn btn-danger">Course annulée</button> -->
						</div>
					</div>	
  				</c:when>
				<c:otherwise>
  					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
							<h4 class="modal-title">Commander un tuk-tuk :</h4>
						</div>
						<div class="modal-body">
							<label>Entrer l'adresse de départ</label>
							<input id="vmadresse" data-places-auto-complete data-ng-model="vm.address" data-component-restrictions="{country:'fr'}" data-types="{{types}}" data-on-place-changed="vm.placeChanged()" />
							<br/>
							<div data-ng-show="vm.place">
								Address = {{vm.place.formatted_address}} <br/>
								Location: {{vm.place.geometry.location}}<br/>
							</div>
							<button id="btn-search" type="button" class="btn btn-primary"  data-toggle="modal" data-target=".modal-waiting" data-dismiss="modal">Rechercher un tuk-tuk</button>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
						</div>
					</div>
  				</c:otherwise>
			</c:choose>
			
			</div>
		</div>
		
		
		
		<div id="waiting-modal" index="modal-control" class="modal fade modal-waiting" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" data-backdrop="static">
			<div id="modal-waiting" role="document">
				<div class="modal-middle">
					<h3>Veuillez patientez...</h3>
					<div class="loader"></div>
					<br>
					<button type="button" onclick="annuleeCourseClient()" class="btn-lg btn-danger" data-dismiss="modal">Annulée</button>
				</div>
			</div>
		</div>

		
		<div id="rideInfos-modal" index="modal-control" class="modal fade modal-ride" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
			<div class="modal-dialog modal-sm" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<h4 class="modal-title">Course</h4>
					</div>
					<div class="modal-body">
						<!-- <label>Information chauffeur</label> -->
						<div class="card">
							<div id="card-block-rideInfos" class="card-block">
							</div>
						</div>
					</div>
				</div>
				
			</div>
		</div>
		
		
		<div id="driverInfo-modal" index="modal-control" class="modal fade modal-chauffeur" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
			<div class="modal-dialog modal-sm" role="document">
			
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" onclick="refuseCourseClient()" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<h4 class="modal-title">Un chauffeur a accepté la course</h4>
					</div>
					<div class="modal-body">
						<!-- <label>Information chauffeur</label> -->
						<div class="card">
<%-- 							<img class="card-img-top" src="${ pageContext.request.contextPath }/resources/img/chauffeur.jpg" alt="Jean Dupont"/> --%>
							<div id="card-block" class="card-block">
<!-- 									<h4 class="card-title">Jean Dupont</h4> -->
<!-- 									<br> -->
<%-- 									<img id="rating" src="${ pageContext.request.contextPath }/resources/img/rate5.png" alt="Note"/> --%>
<!-- 									<hr /> -->
<!-- 									<a id="btn-commandée" onclick="accepteCourseClient()" class="btn btn-success">Commandée</a> -->
<!-- 									<a id="btn-annulée" onclick="refuseCourseClient()" class="btn btn-danger">Annulée</a> -->
							</div>
						</div>
					</div>
				</div>
				
			</div>
		</div>
		
		
		<div index="modal-control" class="modal fade nav-side" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
			<div class="modal-dialog navside modal-sm" role="document">
				<div class="modal-content navsider">
					<div class="sidenav-img">
						<img class="img-profil img-circle" height="140px" width="140px" src="${ pageContext.request.contextPath }/resources/img/chauffeur.jpg"/><br><br>
						<label id="idClientNomPrenom">${ conducteur.prenom } ${ conducteur.nom }${ client.prenom } ${ client.nom }</label>
						<hr />
						
						<div class="middle-sidenav">
							<label>Option :</label>
							<button id="btn-payoption" type="button" class="btn btn-success">Option de payement</button>
							<button id="btn-history" type="button" class="btn btn-warning" data-toggle="modal" data-target=".modal-history" data-dismiss="modal">Historique</button>
							<c:if test="${ not empty conducteur }">
							<hr />
							<label>Disponible :</label>
							<input data-dismiss="modal" id="toggle-trigger" type="checkbox" data-toggle="toggle" checked data-on="Disponible" data-off="Non disponible" data-onstyle="success" data-offstyle="danger" data-width="160px" data-dismiss="modal">
							</c:if>
						</div>
						
						<div class="bottom-sidenav">
							<button id="btn-disconnect" href="/logout"  type="button" class="btn btn-danger">Deconnexion</button>
						</div>
					</div>				
				</div>
			</div>
		</div>
		
		
		<div index="modal-control" class="modal fade nav-course" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
			<div class="modal-dialog navcourse modal-sm" role="document">
				<div class="modal-content coursesider">
					
					<div class="coursenav-img">
					<br>
						<label>Liste des course dispo</label>
						<hr />
						<div id="listeCourse">
<!-- 							<div class="panel panel-primary"> -->
<!-- 								<div class="alert alert-success">Doge LeChien</div>				 -->
<!-- 								<div class="panel-body"> -->
<%-- 									<img id="rating" src="${ pageContext.request.contextPath }/resources/img/rate3.png" alt="Note"/> --%>
<!--     							</div> -->
<!--     							<div class="panel-footer"> -->
<!--     								<button id="btn-accept" type="button" class="btn btn-success">Accpeter</button> -->
<!--     								<button id="btn-reject" type="button" class="btn btn-danger">Refuser</button> -->
<!--   								</div> -->
<!-- 							</div> -->
						</div>
					</div>				
				</div>
			</div>
		</div>
	
	
		
		<div index="modal-control" class="modal fade modal-history" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
			<div class="modal-dialog history modal-lg" role="document">
			
				<div class="modal-content hisroty-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<h4 class="modal-title">Historique des course : ${ conducteur.prenom } ${ conducteur.nom }${ client.prenom } ${ client.nom }</h4>
					</div>
					<div class="modal-body">
						<table id="history" class="table table-bordered">
							<tr class="warning">
								<th>Adresse</th>
								<th>Temps</th>
								<th>Prix</th>
							</tr>
							
						</table>
					</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>
				</div>
				
			</div>
		</div>
		
		
		
		
	</section>
	<script type="text/javascript" src="${ pageContext.request.contextPath }/resources/js/resttemplate.js"></script>
	<script type="text/javascript">
		var rest = new RestTemplate("${token}");	
		var host = "${ pageContext.request.contextPath }";
	</script>
	<script src="${ pageContext.request.contextPath }/resources/js/accueil.js"></script>
	<script src="${ pageContext.request.contextPath }/resources/js/gmap.js"></script>
	<c:choose>
		<c:when test="${not empty conducteur}">
				<script src="${ pageContext.request.contextPath }/resources/js/conducteur.js"></script>
			</c:when>
			<c:otherwise>
				<script src="${ pageContext.request.contextPath }/resources/js/client.js"></script>
		</c:otherwise>
	</c:choose>
</body>
</html>