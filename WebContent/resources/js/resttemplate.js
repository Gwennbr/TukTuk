function RestTemplate(_token, globalErrorCallback)
{
	this.$http = angular.injector(["ng"]).get("$http");
	this.userType = RestTemplate.ClientType.UNKNOWN;
	this.user = undefined;
	this.tryLoginDriver = undefined;
	this.isRetry = false;
	this.token = _token;
	this.pushedCalls = [];
	
	this.addCall = function(url, method, data, callback, errorCallback)
	{
		this.pushedCalls.push({
			url: url,
			method: method,
			data: data,
			callback: callback,
			errorCallback: errorCallback
		});
		if (this.pushedCalls.length == 1)
			this.doAjax(url, method, data, callback, errorCallback);
	}
	
	this.login = function()
	{
		this.tryLoginDriver = true;
		this.user = undefined;
		this.token = undefined;
		this.userType = RestTemplate.ClientType.UNKNOWN;
		this.$http.get(this.buildUrl(RestTemplate.RESTURI_DRIVER_LOGIN, username, password))
			.then(this.internalLoginCallback.bind(this), this.internalLoginCallbackError.bind(this));
	}
	
	this.doAjax = function(url, method, data, callback, errorCallback)
	{ 
		if (data === undefined || data == null)
			this.$http({
				url: url,
				method: method,
				headers: { "tokenauth-token" : this.token }
			}).then(this.internalCallback.bind(this, callback, errorCallback), this.internalErrorCallback.bind(this, callback, errorCallback)); 	
		else
			this.$http({
				url: url,
				method: method,
				headers: { "tokenauth-token" : this.token },
				data: data
			}).then(this.internalCallback.bind(this, callback, errorCallback), this.internalErrorCallback.bind(this, callback, errorCallback)); 	
	}

	this.internalLoginCallbackError = function(response)
	{
		if (this.tryLoginDriver)
		{
			this.tryLoginDriver = false;
			this.$http.get(RestTemplate.RESTURI_CUSTOMER_LOGIN + "?username=" + username + "&password=" + password)
				.then(this.internalLoginCallback, this.internalLoginCallbackError);
		}
		else
		{
			this.tryLoginDriver = undefined;
			this.user = undefined;
			this.token = undefined;
			this.userType = RestTemplate.ClientType.UNKNOWN;
			if (globalErrorCallback !== undefined && globalErrorCallback != null)
				globalErrorCallback(response.status);
		}
	}

	this.internalLoginCallback = function(response)
	{
		if (response.status == 200)
		{
			this.token = response.headers(RestTemplate.HEADER_TOKEN_NAME);
			this.user = response.data;
			if (this.tryLoginDriver)
				this.userType = RestTemplate.ClientType.DRIVER;
			else
				this.userType = RestTemplate.ClientType.CUSTOMER;
		}
	}
	this.internalErrorCallback = function(callback, errorCallback, response)
	{
		var tok = response.headers("tokenauth-token");
		if (tok != undefined && tok != null && tok != "")
			this.token = tok;
		
		if (errorCallback !== undefined && errorCallback != null)
			errorCallback(response.status);
		
		this.pushedCalls = this.pushedCalls.slice(1);

		if (this.pushedCalls.length > 0)
			this.doAjax(this.pushedCalls[0].url, this.pushedCalls[0].method, this.pushedCalls[0].data, this.pushedCalls[0].callback, this.pushedCalls[0].errorCallback);			
	}
	
	this.internalCallback = function(callback, errorCallback, response)
	{
		var tok = response.headers("tokenauth-token");
		if (tok != undefined && tok != null && tok != "")
			this.token = tok;
			
		if (response.status >= 200 && response.status <= 299)
		{
			if (callback !== undefined && callback != null)
				callback(response.data);
		}
		else
		{
			if (errorCallback !== undefined && errorCallback != null)
				errorCallback(response.status);
		}
		
		this.pushedCalls = this.pushedCalls.slice(1);

		if (this.pushedCalls.length > 0)
			this.doAjax(this.pushedCalls[0].url, this.pushedCalls[0].method, this.pushedCalls[0].data, this.pushedCalls[0].callback, this.pushedCalls[0].errorCallback);			
	}

	this.buildUrl = function(fmt)
	{
		fmt = fmt.replace(/{[^{}]*}/g, (function(arguments, match) {
			match = match.slice(1, match.length-1);
			return arguments[Number(match)];
		}).bind(this, arguments) );
		return RestTemplate.RESTURI_API_PREFIX + fmt;
	}

	/*
	 * Paramètre supplémentaire : aucun
	 * Accès restreint : seulement au CONDUCTEUR loggué en session
	 * paramètre fonction succès : 
	 */
	this.driver_GetMyProfil = function(callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_DRIVER_PROFIL), "GET", undefined, 
			(function(data) { this.user = data; callback(data); }).bind(this), errorCallback);
	}

	/*
	 * Paramètre supplémentaire : Object conducteur contenant les données à sauvegarder
	 * Accès restreint : seulement au CONDUCTEUR loggué en session
	 * paramètre fonction succès : L'objet conducteur récup depuis la base après MàJ
	 */
	this.driver_UpdateProfil = function(driver, callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_DRIVER_UPDATEPROFIL), "PUT", driver, 
				(function(data) { this.user = data; callback(data); }).bind(this), errorCallback);
	}

	/*
	 * Paramètre supplémentaire : aucun
	 * Accès restreint : seulement au CONDUCTEUR loggué en session
	 * paramètre fonction succès : Tableau de 'Course'
	 */
	this.driver_GetRunsHistory = function(callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_DRIVER_RUNSHISTORY), "GET", undefined, callback, errorCallback);
	}

	/*
	 * Paramètre supplémentaire : l'id du driver dont on veut les infos
	 * Accès restreint : seulement au CLIENT loggué en session
	 * paramètre fonction succès : objet Driver rempli avec seulement les champs "public"
	 */
	this.driver_GetPublicInfos = function(driverId, callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_DRIVER_GETINFOS, driverId), "GET", undefined, callback, errorCallback);
	}

	/*
	 * Accès restreint : seulement au CONDUCTEUR loggué en session
	 * paramètre fonction succès : 
	 */
	this.driver_SetUnavailable = function(callback, errorCallback)
	{
		console.log("driver_SetUnAvailable");
		this.addCall(this.buildUrl(RestTemplate.RESTURI_DRIVER_UNAVAILABLE), "PUT", undefined, callback, errorCallback);		
	}

	/*
	 * Accès restreint : seulement au CONDUCTEUR loggué en session
	 * paramètre fonction succès : 
	 */
	this.driver_SetAvailable = function(callback, errorCallback)
	{
		console.log("driver_SetAvailable");
		this.addCall(this.buildUrl(RestTemplate.RESTURI_DRIVER_AVAILABLE), "PUT", undefined, callback, errorCallback);		
	}

	/*
	 * Accès restreint : seulement au CONDUCTEUR loggué en session
	 * paramètre fonction succès : 
	 */
	this.driver_refreshPosAndGetAvailableRides = function(lng, lat, callback, errorCallback)
	{
		console.log("driver_refreshPosAndGetAvailableRides");
		this.addCall(this.buildUrl(RestTemplate.RESTURI_DRIVER_REFRESH, lng, lat),
					 "PUT", undefined, callback, errorCallback);
	}

	/*
	 * Accès restreint : seulement au CONDUCTEUR loggué en session
	 * paramètre fonction succès : 
	 */
	this.driver_GetMyNote = function(callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_DRIVER_GETMYNOTE), "GET", undefined, callback, errorCallback);		
	}

	/*
	 * Accès restreint : seulement au CONDUCTEUR loggué en session
	 * paramètre fonction succès : 
	 */
	this.driver_GetNote = function(idDriver, callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_DRIVER_GETNOTE, idDriver), 
				"GET", undefined, callback, errorCallback);				
	}
	
	this.driver_AllAround = function(callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_DRIVER_ALLAROUND), "GET", undefined, callback, errorCallback);				
	}
	
	this.customer_GetMyProfil = function(callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_CUSTOMER_PROFIL), "GET", undefined, callback, errorCallback);				
	}
	
	this.customer_UpdateProfil = function(customer, callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_CUSTOMER_UPDATEPROFIL), "PUT", customer, callback, errorCallback);				
	}
	
	this.customer_GetRunsHistory = function(callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_CUSTOMER_RUNSHISTORY), "GET", undefined, callback, errorCallback);				
	}
	
	this.customer_GetMyNote = function(callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_CUSTOMER_GETMYNOTE), "GET", undefined, callback, errorCallback);				
	}
	
	this.customer_GetNote = function(customerId, callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_CUSTOMER_GETNOTE, customerId), "GET", undefined, callback, errorCallback);				
	}
	
	this.customer_GetPublicInfos = function(customerId, callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_CUSTOMER_GETINFOS, customerId), "GET", undefined, callback, errorCallback);				
	}
	
	this.ride_Request = function(address, callback, errorCallback)
	{
		address = address.replace(/ /g, "+");
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_REQUEST, address), "POST", undefined, callback, errorCallback);				
	}
	
	this.ride_Accept = function(rideId, callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_ACCEPT, rideId), "PUT", undefined, callback, errorCallback);				
	}
	
	this.ride_Validate = function(callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_VALIDATE), "PUT", undefined, callback, errorCallback);				
	}
	
	this.ride_Decline = function(callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_DECLINE), "PUT", undefined, callback, errorCallback);				
	}
	
	this.ride_Infos = function(callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_INFOS), "GET", undefined, callback, errorCallback);				
	}

	this.ride_InfosWithId = function(rideId, callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_INFOS_WITH_ID, rideId), "GET", undefined, callback, errorCallback);				
	}

	this.ride_Start = function(callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_START), "PUT", undefined, callback, errorCallback);				
	}
	
	this.ride_Finish = function(callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_FINISH), "PUT", undefined, callback, errorCallback);				
	}
	
	this.ride_Pause = function(callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_PAUSE), "PUT", undefined, callback, errorCallback);				
	}
	
	this.ride_Resume = function(callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_RESUME), "PUT", undefined, callback, errorCallback);				
	}
	
	this.ride_Comment = function(rideId, note, comment, callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_COMMENT, rideId, note, comment), "PUT", undefined, callback, errorCallback);				
	}
	
	this.ride_Delete = function(callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_DELETE), "DELETE", undefined, callback, errorCallback);				
	}
}

RestTemplate.ClientType = { UNKNOWN:0, CUSTOMER:1, DRIVER:2 } 
RestTemplate.HEADER_TOKEN_NAME = "tokenauth-token";

RestTemplate.RESTURI_API_PREFIX = "/TukTuk/api";

RestTemplate.RESTURI_DRIVER_LOGIN = "/driver/login?username={1}&password={2}";
RestTemplate.RESTURI_DRIVER_PROFIL = "/driver";
RestTemplate.RESTURI_DRIVER_UPDATEPROFIL = "/driver";
RestTemplate.RESTURI_DRIVER_RUNSHISTORY = "/driver/history";
RestTemplate.RESTURI_DRIVER_GETINFOS = "/driver/{1}";
RestTemplate.RESTURI_DRIVER_UNAVAILABLE = "/driver/unavailable";
RestTemplate.RESTURI_DRIVER_AVAILABLE = "/driver/available";
RestTemplate.RESTURI_DRIVER_REFRESH = "/driver/refreshPos?longitude={1}&latitude={2}";
RestTemplate.RESTURI_DRIVER_GETMYNOTE = "/driver/note";
RestTemplate.RESTURI_DRIVER_GETNOTE = "/driver/{1}/note";
RestTemplate.RESTURI_DRIVER_ALLAROUND = "/drivers";

RestTemplate.RESTURI_CUSTOMER_LOGIN = "/customer/login?username={1}&password={2}";
RestTemplate.RESTURI_CUSTOMER_PROFIL = "/customer";
RestTemplate.RESTURI_CUSTOMER_UPDATEPROFIL = "/customer";
RestTemplate.RESTURI_CUSTOMER_RUNSHISTORY = "/customer/history";
RestTemplate.RESTURI_CUSTOMER_GETMYNOTE = "/customer/note";
RestTemplate.RESTURI_CUSTOMER_GETNOTE = "/customer/{1}/note";
RestTemplate.RESTURI_CUSTOMER_GETINFOS = "/customer/{1}";

RestTemplate.RESTURI_RIDE_REQUEST = "/ride/request?adresseDepart={1}";
RestTemplate.RESTURI_RIDE_ACCEPT = "/ride/{1}/accept";
RestTemplate.RESTURI_RIDE_VALIDATE = "/ride/validate";
RestTemplate.RESTURI_RIDE_DECLINE = "/ride/decline";
RestTemplate.RESTURI_RIDE_INFOS = "/ride";
RestTemplate.RESTURI_RIDE_INFOS_WITH_ID = "/ride/{1}";
RestTemplate.RESTURI_RIDE_START = "/ride/start";
RestTemplate.RESTURI_RIDE_FINISH = "/ride/complete";
RestTemplate.RESTURI_RIDE_PAUSE = "/ride/pause";
RestTemplate.RESTURI_RIDE_RESUME = "/ride/resume";
RestTemplate.RESTURI_RIDE_COMMENT = "/ride/{1}/comment?note={2}&commentaire={3}";
RestTemplate.RESTURI_RIDE_DELETE = "/ride";

