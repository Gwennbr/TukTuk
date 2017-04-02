<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE>
<html>
<head>
<link rel="icon" type="image/png"
	href="${ pageContext.request.contextPath }/resources/img/favicon.png" />
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
</head>
<body>
	<div>
		<header>
			<div>
				<h3>
					<b class="colorBlue">ADOPTE</b>UN<b class="colorBlue">TUK-TUK.COM</b>
				</h3>
				<h5>Inscription</h5>
			</div>
		</header>
		<section>
			<div>
				<c:if test="${not empty errorMsg}">
					<div class="cLogin form-group">
						<div class="alert alert-danger" role="alert">
							<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
							<span class="sr-only">Error:</span>${ errorMsg }
						</div>
					</div>
				</c:if>
				<select class="form-control" id="registerType" onchange="onRegisterTypeChange()">
					<option value="" selected disabled>Type d'inscription</option>
					<option value="client">client</option>
					<option value="conducteur">conducteur</option>
				</select>
				<form action="registerCustomer" method="post" id="client">
					<hr />
					<h3 class="title-form">Compte</h3>
					<br /> 
					<br />
					<div class="cLogin form-group">
						<label class="align-left"><span class="glyphicon glyphicon-option-vertical"></span> LOGIN</label>
						<input name="username" type="text" class="form-control" placeholder="nom d'utilisateur" />
					</div>
					<br />
					<div class="cLogin form-group">
						<label class="align-left"><span class="glyphicon glyphicon-option-vertical"></span> MOT DE PASSE</label>
						<input name="password" type="password" class="form-control" placeholder="5 caractères minimum" />
					</div>
					<br />
					<div class="cLogin form-group">
						<label class="align-left"><span class="glyphicon glyphicon-option-vertical"></span> E-MAIL</label>
						<input name="mail" type="email" class="form-control" placeholder="nom@exemple.fr" />
					</div>
					<br />
					<div class="cLogin form-group">
						<label class="align-left"><span class="glyphicon glyphicon-option-vertical"></span> NOM</label>
						<input name="prenom" type="text" class="form-control" placeholder="Prénom" /> <br>
						<input name="nom" type="text" class="form-control" placeholder="Nom" />
					</div>
					<br />
					<div class="cLogin form-group">
						<label class="align-left"><span class="glyphicon glyphicon-option-vertical"></span> NUMÉRO DE PORTABLE</label>
						<!-- 						<div class="form-group"> -->
						<input name="tel" type="tel" class="form-control" placeholder="0612345678" />
					</div>

					<br />

					<h3 class="title-form">Information de paiement</h3>
					<br />
					<div class="cLogin form-group">
						<label class="align-left"><span class="glyphicon glyphicon-option-vertical"></span> NUMÉRO CARTE DE CREDIT</label>
						<div class="input-group">
							<input name="numeroCarteBancaire" type="text" class="form-control" placeholder="1234 5678 9012 3456" aria-describedby="creditcard" />
							<span class="input-group-addon" id="creditcard"><span class="glyphicon glyphicon-credit-card"></span></span>
						</div>
						<br /> 
						<label class="align-left"><span class="glyphicon glyphicon-option-vertical"></span> CODE DE SECURITÉ</label>
						<input name="pictogramme" type="text" class="form-control"	placeholder="123" /> 
						<br /> 
						<label class="align-left"><span class="glyphicon glyphicon-option-vertical"></span> DATE D'EXPIRATION</label>
						<div class="form-select">
							<select class="form-control selectpicker" name="moisValiditeCB">
								<option value="" disabled selected>Mois</option>
								<option value="01">01</option>
								<option value="02">02</option>
								<option value="03">03</option>
								<option value="04">04</option>
								<option value="05">05</option>
								<option value="06">06</option>
								<option value="07">07</option>
								<option value="08">08</option>
								<option value="09">09</option>
								<option value="10">10</option>
								<option value="11">11</option>
								<option value="12">12</option>
							</select> 
							<select class="form-control selectpicker" name="anneeValiditeCB">
								<option value="" disabled selected>Année</option>
								<option value="2017">2017</option>
								<option value="2018">2018</option>
								<option value="2019">2019</option>
								<option value="2020">2020</option>
							</select>
						</div>
					</div>
					<br /> 
					<br /> 
					<br />
					<div class="cLogin form-group">
						<hr />
						<input name="submit" type="submit"
							class="btn btn-primary btn-lg cLogin" value="Inscription" />
					</div>
				</form>
				<form action="registerDriver" method="post" id="conducteur">
					<hr />
					<h3 class="title-form">Compte</h3>
					<br /> 
					<br />
					<div class="cLogin form-group">
						<label class="align-left"><span class="glyphicon glyphicon-option-vertical"></span> LOGIN</label>
						<input name="username" type="text" class="form-control" placeholder="nom d'utilisateur" />
					</div>
					<br />
					<div class="cLogin form-group">
						<label class="align-left"><span class="glyphicon glyphicon-option-vertical"></span> MOT DE PASSE</label>
						<input name="password" type="password" class="form-control" placeholder="5 caractères minimum" />
					</div>
					<br />
					<div class="cLogin form-group">
						<label class="align-left"><span class="glyphicon glyphicon-option-vertical"></span> E-MAIL</label>
						<input name="mail" type="email" class="form-control" placeholder="nom@exemple.fr" />
					</div>
					<br />
					<div class="cLogin form-group">
						<label class="align-left"><span class="glyphicon glyphicon-option-vertical"></span> NOM</label>
						<input name="prenom" type="text" class="form-control" placeholder="Prénom" /> <br>
						<input name="nom" type="text" class="form-control" placeholder="Nom" />
					</div>
					<br />
					<div class="cLogin form-group">
						<label class="align-left"><span class="glyphicon glyphicon-option-vertical"></span> NUMÉRO DE PORTABLE</label>
						<!-- 						<div class="form-group"> -->
						<input name="tel" type="tel" class="form-control" placeholder="0612345678" />
					</div>

					<br />
					
					<h3 class="title-form">Véhicule</h3>
					<br />
					<div class="cLogin form-group">
						<label class="align-left"><span class="glyphicon glyphicon-option-vertical"></span> IMMATRICULATION</label>
						<!-- 						<div class="form-group"> -->
						<input name="numImmat" type="text" class="form-control" placeholder="AA-123-AA" />
					</div>
					
					<br />
					
					<h3 class="title-form">Information de paiement</h3>
					<br />
					<div class="cLogin form-group">
						<label class="align-left"><span class="glyphicon glyphicon-option-vertical"></span> NUMÉRO IBAN</label>
						<div class="input-group">
							<input name="iban" type="text" class="form-control" placeholder="FR12 1234 1234 1234 1234 1234 123" aria-describedby="creditcard" />
							<span class="input-group-addon" id="creditcard"><span class="glyphicon glyphicon-credit-card"></span></span>
						</div>
						<br> 
						<label class="align-left"><span class="glyphicon glyphicon-option-vertical"></span> CODE BIC</label>
						<input name="bic" type="text" class="form-control"	placeholder="XXXXFRXXXXX" /> 
						<br> 
					</div>
					<br />
					<br />
					<br />
					<div class="cLogin form-group">
						<hr />
						<input name="submit" type="submit"
							class="btn btn-primary btn-lg cLogin" value="Inscription" />
					</div>
				</form>
			</div>
		</section>
	</div>
</body>
<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"	integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u"	crossorigin="anonymous">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
<script	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
<link rel="stylesheet" type="text/css" href="${ pageContext.request.contextPath }/resources/css/global.css" />
<link rel="stylesheet" type="text/css" href="${ pageContext.request.contextPath }/resources/css/login.css" />
<link rel="stylesheet" type="text/css" href="${ pageContext.request.contextPath }/resources/css/register.css" />
<script type="text/javascript" src="${ pageContext.request.contextPath }/resources/js/register.js"></script>
<link href="https://fonts.googleapis.com/css?family=Raleway" rel="stylesheet">
</html>