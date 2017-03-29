function RestTemplate(_token, globalErrorCallback)
{
	this.$http = angular.injector(["ng"]).get("$http");
	this.userType = RestTemplate.ClientType.UNKNOWN;
	this.user = undefined;
	this.tryLoginDriver = undefined;
	this.isRetry = false;
	this.token = _token;
	
	this.login = function()
	{
		this.tryLoginDriver = true;
		this.user = undefined;
		this.token = undefined;
		this.userType = RestTemplate.ClientType.UNKNOWN;
		this.$http.get(RestTemplate.RESTURI_DRIVER_LOGIN + "?username=" + username + "&password=" + password)
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
			}).then(this.internalCallback.bind(this, callback, errorCallback),
						(function(response) { 
							if (this.isRetry == false)
							{
								if (response.status == 403)
								{
									this.isRetry = true;
									this.doAjax(url, method, data, callback, errorCallback);
								}
							}
							else
							{
								this.isRetry = false;
							}
						}).bind(this)); 	
		else
			this.$http({
				url: url,
				method: method,
				headers: {
					"tokenauth-token" : this.token
				},
				data: data
			}).then(this.internalCallback.bind(this, callback, errorCallback),
						(function(response) { 
							if (this.isRetry == false)
							{
								if (response.status == 403)
								{
									this.isRetry = true;
									this.doAjax(url, method, data, callback, errorCallback);
								}
							}
							else
							{
								this.isRetry = false;
							}
						}).bind(this)); 	

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

	this.internalCallback = function(callback, errorCallback, response)
	{
		if (response.status == 200)
		{
			this.token = response.headers("tokenauth-token");
			callback(response.data);
		}
		else
		{
			errorCallback(response.status);
		}
	}

	this.driver_GetMyProfil = function(callback, errorCallback)
	{
		console.log(this.token);
		this.doAjax(RestTemplate.RESTURI_DRIVER_PROFIL, "GET", undefined, 
			(function(data) { this.user = data; callback(data); }).bind(this), errorCallback);
	}

	this.driver_UpdateProfil = function(driver, callback, errorCallback)
	{
		this.doAjax(RestTemplate.RESTURI_DRIVER_UPDATEPROFIL, "PUT", driver, 
			(function(data) { this.user = data; callback(data); }).bind(this), errorCallback);
	}

	this.driver_GetRunsHistory = function(callback, errorCallback)
	{
		this.doAjax(RestTemplate.RESTURI_DRIVER_RUNSHISTORY, "GET", undefined, callback, errorCallback);
	}

	this.driver_GetDriverInfos = function(driverId, callback, errorCallback)
	{
		this.doAjax(RestTemplate.RESTURI_DRIVER_GETINFOS + driverId, "GET", undefined, callback, errorCallback);
	}
}

RestTemplate.ClientType = { UNKNOWN:0, CUSTOMER:1, DRIVER:2 } 
RestTemplate.HEADER_TOKEN_NAME = "tokenauth-token";

RestTemplate.RESTURI_CUSTOMER_LOGIN = "/TukTuk/api/customer/login";
RestTemplate.RESTURI_DRIVER_LOGIN = "/TukTuk/api/driver/login";
RestTemplate.RESTURI_DRIVER_PROFIL = "/TukTuk/api/driver";
RestTemplate.RESTURI_DRIVER_UPDATEPROFIL = "/TukTuk/api/driver";
RestTemplate.RESTURI_DRIVER_RUNSHISTORY = "/TukTuk/api/driver/history";
RestTemplate.RESTURI_DRIVER_GETINFOS = "/TukTuk/api/driver/";
