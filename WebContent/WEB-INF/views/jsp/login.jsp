<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE>
<html>

	<head>

	
	</head>
	
	<body>
		<div>
			<header>
				<div>
					<h3><b class="colorBlue">ADOPTE</b>UN<b class="colorBlue">TUK-TUK.COM</b></h3>
					<h5>Connexion</h5>
				</div>
			</header>
			<section>
				<div>
					<form id="personne" action="login" method="post">
						<div class="cLogin form-group">
						<hr />
							<input id="username" type="text" class="form-control" placeholder="Nom d'utilisateur"/>
						</div>
						<div class="cLogin form-group">
							<input id="password" type="password" class="form-control" placeholder="Mot de passe"/>
						</div>
						<div class="cLogin form-group">
							<input name="submit" type="submit" class="btn btn-primary btn-lg cLogin" value="Connexion"/>
							<hr />
						</div>
					</form>	
				</div>
			</section>
		</div>
		
		
		
		<div>
			<section>
				<div>
					<div><a href="#">Mot de passe oublié</a></div>
					<div><a href="#">S'inscrire</a></div>
				</div>
			</section>
		</div>
	</body>
	
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
	<link rel="stylesheet" type="text/css" href="${ pageContext.request.contextPath }/resources/css/global.css"/>
	<link rel="stylesheet" type="text/css" href="${ pageContext.request.contextPath }/resources/css/login.css"/>
	<link href="https://fonts.googleapis.com/css?family=Raleway" rel="stylesheet">
</html>