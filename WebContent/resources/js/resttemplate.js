function RestTemplate(login, password, loginErrorCallback)
{
	this.$http = angular.injector(["ng"]).get("$http");
	this.userType = RestTemplate.ClientType.UNKNOWN;
	this.user = undefined;
	this.token = undefined;
	this.tryLoginDriver = undefined;
	this.isRetry = false;

	this.login = function(doAfter)
	{
		this.tryLoginDriver = true;
		this.user = undefined;
		this.token = undefined;
		this.userType = RestTemplate.ClientType.UNKNOWN;
		this.$http.get(RestTemplate.RESTURI_DRIVER_LOGIN + "?username=" + login + "&password=" + password)
			.then(this.internalLoginCallback.bind(this, doAfter.bind(this)), 
				this.internalLoginCallbackError.bind(this, doAfter.bind(this)));
	}
	
	this.doAjax = function(url, method, data, callback, errorCallback)
	{ 
		if (this.userType === RestTemplate.ClientType.UNKNOWN)
		{
			this.login(function() { this.doAjax(url, method, data, callback, errorCallback); });
			return;
		}

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

	this.internalLoginCallbackError = function(doAfter, response)
	{
		if (this.tryLoginDriver)
		{
			this.tryLoginDriver = false;
			this.$http.get(RestTemplate.RESTURI_CUSTOMER_LOGIN + "?username=" + login + "&password=" + password)
				.then(this.internalLoginCallback, this.internalLoginCallbackError);
		}
		else
		{
			this.tryLoginDriver = undefined;
			this.user = undefined;
			this.token = undefined;
			this.userType = RestTemplate.ClientType.UNKNOWN;
			if (loginErrorCallback !== undefined && loginErrorCallback != null)
				loginErrorCallback(response.status);
		}
	}

	this.internalLoginCallback = function(doAfter, response)
	{
		if (response.status == 200)
		{
			this.token = response.headers(RestTemplate.HEADER_TOKEN_NAME);
			this.user = response.data;
			if (this.tryLoginDriver)
				this.userType = RestTemplate.ClientType.DRIVER;
			else
				this.userType = RestTemplate.ClientType.CUSTOMER;
			if (doAfter !== undefined && doAfter != null)
				doAfter();
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

	this.driver_GetProfil = function(callback, errorCallback)
	{
		this.doAjax(RestTemplate.RESTURI_DRIVER_PROFIL, "GET", undefined, callback, errorCallback)
	}
}

RestTemplate.ClientType = { UNKNOWN:0, CUSTOMER:1, DRIVER:2 } 
RestTemplate.HEADER_TOKEN_NAME = "tokenauth-token";

RestTemplate.RESTURI_CUSTOMER_LOGIN = "/TukTuk/api/client/login";
RestTemplate.RESTURI_DRIVER_LOGIN = "/TukTuk/api/conducteur/login";
RestTemplate.RESTURI_DRIVER_PROFIL = "/TukTuk/api/conducteur";