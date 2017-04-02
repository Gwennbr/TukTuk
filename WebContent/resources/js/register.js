function onRegisterTypeChange()
{
	var t = $("#registerType").val();
	
	if (t === "client")
	{
		$("#client").show();
		$("#conducteur").hide();
	}
	else
		if (t === "conducteur")
		{
			$("#client").hide();
			$("#conducteur").show();
		}
		else
		{
			$("#client").hide();
			$("#conducteur").hide();
		}
}