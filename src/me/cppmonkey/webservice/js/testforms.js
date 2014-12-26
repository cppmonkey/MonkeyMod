
onerror=function handleErr( msg, url, l )
{
	txt = "";
	txt+="msg=" + msg + "&amp;";
	txt+="url=" + url + "&amp;";
	txt+="line=" + l + "&amp;";
	alert( txt );
	return true;
}

	$().ready(function(){
		$("#tabs").tabs();

		$("#usermessage").submit(function() {
			$("#results").prepend(
				"<div><a href=\"" + "update.php?"+$(this).serialize() + "\">simulate chat</a></div>" );
			return false;
		});

		$("#userconnect").submit(function() {
				$("#results").prepend(
					"<div><a href=\"" + "update.php?"+$(this).serialize()+"\">simulate connection</a></div>");
				return false;
			});

		$.getJSON("serverlist.php?"/*,{ owner_id : "1" }*/, function(data){
				for ( var i = 0; i < data.length; i++ ) {
					$('select[id][name^="serverip"]').append( new Option( data[i].title ,data[i].ip ));
				}

			});

		$("#severipedituseraccess").change(function(){
				$.getJSON("playerlist.php?", { serverip: this.value }, function(data){
						$('select[id][name^="serverknown"]').empty();
						//Get player list from server
						for (var i = 0; i < data.length; i++ ){
							$('select[id][name^="serverknown"]').append( new Option( data[i].name, data[i].id ));
						}
					});
			});

		$("#serveripmessage").change(function(){
            $.getJSON("playerlist.php?", { serverip: this.value }, function(data){
                    $('#playermessage').empty();
                    //Get player list from server
                    for (var i = 0; i < data.length; i++ ){
                        $('#playermessage').append( new Option( data[i].name, data[i].id ));
                    }
                });
        });

	$("#serveripconnect").change(function(){
            $.getJSON("playerlist.php?", { serverip: this.value }, function(data){
                    $('#playerconnect').empty();
                    //Get player list from server
                    for (var i = 0; i < data.length; i++ ){
                        $('#playerconnect').append( new Option( data[i].name, data[i].id ));
                    }
                });
        });



		$("#serveriphistory").change(function(){
				$.getJSON("history.php?", { serverip: this.value, limit: $("#historylimit").val() }, function(data){
						//Process data
						$('#chat tbody').empty();
						for (var i = 0; i < data.length; i++){
							$('#chat tbody').append( "<tr><td>" + data[i].timestamp + "</td><td>"+data[i].name + "</td><td>" + data[i].msg +"</td></tr>");
						}

						$("#chat tbody tr:even").addClass("alt");
					});
			});
		$("#refreshserveriphistory").click(function(){
				$("#serveriphistory").trigger("change");
			});
		$("#serverknownusersedituseraccess").change(function(){
				$.getJSON("playerlist.php?", {serverip: $("#severipedituseraccess").val(), userid: this.value},function(data){
						$("form#edituseraccess input:checkbox").removeAttr("checked");

						for (var i = 0; i < data.length; i++) {
							if (data[i].value == "true") {
								$("#edituseraccess"+data[i].permission).attr("checked","checked");
							}
						}
					});
			});
	});