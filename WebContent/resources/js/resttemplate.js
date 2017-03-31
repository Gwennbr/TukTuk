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
		{
			console.log("Add: pushedCalls == 1");
			this.doAjax(url, method, data, callback, errorCallback);
		}
		else
			console.log("Add: pushedCalls > 1 : " + this.pushedCalls.length);
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
/*		if (this.userType === RestTemplate.ClientType.UNKNOWN)
		{
			this.login(function() { this.doAjax(url, method, data, callback, errorCallback); });
			return;
		}
*/
		if (data === undefined || data == null)
			this.$http({
				url: url,
				method: method,
				headers: {
					"tokenauth-token" : this.token
				}
			}).then(this.internalCallback.bind(this, callback, errorCallback), this.internalErrorCallback.bind(this, callback, errorCallback)); 	
		else
			this.$http({
				url: url,
				method: method,
				headers: {
					"tokenauth-token" : this.token
				},
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
		
		console.log(this.pushedCalls);
		if (this.pushedCalls.length == 1 || this.pushedCalls.length == 0)
			this.pushedCalls = [];
		else
		{
			this.pushedCalls = this.pushedCalls.shift();
			if (!Array.isArray(this.pushedCalls))
				this.pushedCalls = [ this.pushedCalls ];
		}
		console.log(this.pushedCalls);
		if (this.pushedCalls.length > 0)
		{
			console.log("pushedCalls non vide : " + this.pushedCalls.length);
			this.doAjax(this.pushedCalls[0].url, this.pushedCalls[0].method, this.pushedCalls[0].data, this.pushedCalls[0].callback, this.pushedCalls[0].errorCallback);			
		}
		else
			console.log("pushedCalls vide");
	}
	
	this.internalCallback = function(callback, errorCallback, response)
	{
		var tok = response.headers("tokenauth-token");
		if (tok != undefined && tok != null && tok != "")
			this.token = tok;
			
		if (response.status == 200)
		{
			if (callback !== undefined && callback != null)
				callback(response.data);
		}
		else
		{
			if (errorCallback !== undefined && errorCallback != null)
				errorCallback(response.status);
		}
		
		console.log(this.pushedCalls);
		if (this.pushedCalls.length == 1 || this.pushedCalls.length == 0)
			this.pushedCalls = [];
		else
		{
			this.pushedCalls = this.pushedCalls.shift();
			if (!Array.isArray(this.pushedCalls))
				this.pushedCalls = [ this.pushedCalls ];
		}
		console.log(this.pushedCalls);
		if (this.pushedCalls.length > 0)
		{
			console.log("pushedCalls non vide : " + this.pushedCalls.length);
			this.doAjax(this.pushedCalls[0].url, this.pushedCalls[0].method, this.pushedCalls[0].data, this.pushedCalls[0].callback, this.pushedCalls[0].errorCallback);			
		}
		else
			console.log("pushedCalls vide");
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
		this.addCall(this.buildUrl(RestTemplate.RESTURI_DRIVER_UNAVAILABLE), "PUT", undefined, callback, errorCallback);		
	}

	/*
	 * Accès restreint : seulement au CONDUCTEUR loggué en session
	 * paramètre fonction succès : 
	 */
	this.driver_SetAvailable = function(callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_DRIVER_AVAILABLE), "PUT", undefined, callback, errorCallback);		
	}

	/*
	 * Accès restreint : seulement au CONDUCTEUR loggué en session
	 * paramètre fonction succès : 
	 */
	this.driver_refreshPosAndGetAvailableRides = function(lng, lat, callback, errorCallback)
	{
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
	
	this.ride_Validate = function(rideId, callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_VALIDATE, rideId), "PUT", undefined, callback, errorCallback);				
	}
	
	this.ride_Decline = function(rideId, callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_DECLINE, rideId), "PUT", undefined, callback, errorCallback);				
	}
	
	this.ride_Infos = function(rideId, callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_INFOS, rideId), "GET", undefined, callback, errorCallback);				
	}
	
	this.ride_Start = function(rideId, callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_START, rideId), "PUT", undefined, callback, errorCallback);				
	}
	
	this.ride_Finish = function(rideId, callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_FINISH, rideId), "PUT", undefined, callback, errorCallback);				
	}
	
	this.ride_Pause = function(rideId, callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_PAUSE, rideId), "PUT", undefined, callback, errorCallback);				
	}
	
	this.ride_Resume = function(rideId, callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_RESUME, rideId), "PUT", undefined, callback, errorCallback);				
	}
	
	this.ride_Comment = function(rideId, callback, errorCallback)
	{
		this.addCall(this.buildUrl(RestTemplate.RESTURI_RIDE_COMMENT, rideId), "PUT", undefined, callback, errorCallback);				
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
RestTemplate.RESTURI_RIDE_VALIDATE = "/ride/{1}/validate";
RestTemplate.RESTURI_RIDE_DECLINE = "/ride/{1}/decline";
RestTemplate.RESTURI_RIDE_INFOS = "/ride/{1}";
RestTemplate.RESTURI_RIDE_START = "/ride/{1}/start";
RestTemplate.RESTURI_RIDE_FINISH = "/ride/{1}/complete";
RestTemplate.RESTURI_RIDE_PAUSE = "/ride/{1}/pause";
RestTemplate.RESTURI_RIDE_RESUME = "/ride/{1}/resume";
RestTemplate.RESTURI_RIDE_COMMENT = "/ride/{1}/comment";

