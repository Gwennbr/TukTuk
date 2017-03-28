function RestTemplate(login, password, _loginErrorCallback)
{
	this.$http = angular.injector(["ng"]).get("$http");
	this.userType = RestTemplate.ClientType.UNKNOWN;
	this.user = undefined;
	this.token = undefined;
	this.userCallback = undefined;
	this.tryLoginDriver = undefined;
	this.loginErrorCallback = _loginErrorCallback;
	
	this.login = function()
	{
		this.tryLoginDriver = true;
		this.user = undefined;
		this.token = undefined;
		this.userType = RestTemplate.ClientType.UNKNOWN;
		this.$http.get(RestTemplate.RESTURI_DRIVER_LOGIN + "?username=" + login + "&password=" + password)
			.then(this.internalLoginCallback, this.internalLoginCallbackError);
	}
	
	this.doAjax = function(url, method, data, callback)
	{ 
		this.$http({
			url: url,
			method: method,
			headers: {
				HEADER_TOKEN_NAME : this.token
			},
			data: data
		}).then(callback); 
	}
	this.internalLoginCallbackError = (function(response)
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
		}
	}).bind(this);

	this.internalLoginCallback = (function(response)
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
	}).bind(this);
}

RestTemplate.ClientType = { UNKNOWN:0, CUSTOMER:1, DRIVER:2 } 

RestTemplate.HEADER_TOKEN_NAME = "tokenauth-token";

RestTemplate.RESTURI_CUSTOMER_LOGIN = "/TukTuk/api/client/login";
RestTemplate.RESTURI_DRIVER_LOGIN = "/TukTuk/api/conducteur/login";
