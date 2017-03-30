<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html ng-app="myApp">
<head>
	<link rel="icon" type="image/png" href="${ pageContext.request.contextPath }/resources/img/favicon.png" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<script src="${ pageContext.request.contextPath }/resources/js/angular.min.js"></script>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css">
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
	<script src="${ pageContext.request.contextPath }/resources/js/gmap.js"></script>
	<link rel="stylesheet" type="text/css" href="${ pageContext.request.contextPath }/resources/css/global.css"/>
	<link rel="stylesheet" type="text/css" href="${ pageContext.request.contextPath }/resources/css/interface.css"/>
	<link href="https://fonts.googleapis.com/css?family=Raleway" rel="stylesheet">
	<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
	<script src="https://rawgit.com/allenhwkim/angularjs-google-maps/master/build/scripts/ng-map.js"></script>
	<link href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css" rel="stylesheet">
	<script src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>
	<script src="https://maps.google.com/maps/api/js?libraries=placeses,visualization,drawing,geometry,places&key=AIzaSyBWK16ZhsCDITiislRiRzpb4qjPrYfXp4s"></script>
</head>
<body ng-app="ngMap">
	<header>
		<nav>
			<a class="left btn btn-lg btn-link"><span class="glyphicon glyphicon-th"></span></a>
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
					<marker ng-repeat="pos in vm.positions" position="{{pos.lat}},{{pos.lng}}" icon=${ pageContext.request.contextPath }/resources/img/userColor.png></marker>
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
		<div index="modal-control" class="modal fade modal-commande" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
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
							<button id="btn-pause" href="#"  type="button" class="btn btn-primary chauffeur">Activer mode pause</button>
							<button id="btn-endCourse" href="#"  type="button" class="btn btn-success">Course terminer</button>
							<button id="btn-cancelCourse" href="#"  type="button" class="btn btn-danger">Course annulee</button>
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
							<input id="vmadresse" places-auto-complete ng-model="vm.address" component-restrictions="{country:'fr'}" types="{{types}}" on-place-changed="vm.placeChanged()" />
							<br/>
							<div ng-show="vm.place">
								Address = {{vm.place.formatted_address}} <br/>
								Location: {{vm.place.geometry.location}}<br/>
							</div>
							<button id="btn-search" type="button" class="btn btn-primary"  data-toggle="modal" data-target=".modal-chauffeur" data-dismiss="modal">Rechercher un tuk-tuk</button>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
						</div>
					</div>
  				</c:otherwise>
			</c:choose>
			
			</div>
		</div>
		
		
		
		<div index="modal-control" class="modal fade modal-waiting" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" data-backdrop="static">
			<div id="modal-waiting" role="document">
				<div class="modal-middle">
					<h3>Veuillez patientez...</h3>
					<div class="loader"></div>
				</div>
			</div>
		</div>
		
		
		
		<div index="modal-control" class="modal fade modal-chauffeur" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
			<div class="modal-dialog modal-sm" role="document">
			
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<h4 class="modal-title">Commander un tuk-tuk :</h4>
					</div>
					<div class="modal-body">
						<!-- <label>Information chauffeur</label> -->
						<div class="card">
							<img class="card-img-top" src="${ pageContext.request.contextPath }/resources/img/chauffeur.jpg" alt="Jean Dupont"/>
							<div class="card-block">
								<h4 class="card-title">Jean Dupont</h4>
								<img id="rating" src="${ pageContext.request.contextPath }/resources/img/rate5.png" alt="Note"/>
								<a id="btn-commander" href="#" class="btn btn-success">Commander</a>
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
							<button id="btn-payoption" href="#"  type="button" class="btn btn-success">Option de payement</button>
							<button id="btn-history" type="button" class="btn btn-warning" data-toggle="modal" data-target=".modal-history" data-dismiss="modal">Historique</button>
							<c:if test="${ not empty conducteur }">
							<hr />
							<label>Disponible :</label>
							<input data-dismiss="modal" id="toggle-trigger" type="checkbox" data-toggle="toggle" checked data-on="Disponible" data-off="Non disponible" data-onstyle="success" data-offstyle="danger" data-width="160px" data-dismiss="modal">
							</c:if>
						</div>
						
						<div class="bottom-sidenav">
							<button id="btn-disconnect" href="logout"  type="button" class="btn btn-danger">Deconnexion</button>
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
	</script>
	<script src="${ pageContext.request.contextPath }/resources/js/accueil.js"></script>
	<script src="${ pageContext.request.contextPath }/resources/js/gmap.js"></script>
</body>
</html>